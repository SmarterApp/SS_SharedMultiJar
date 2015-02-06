/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import AIR.Common.Helpers._Ref;
import AIR.Common.Utilities.TDSHttpUtils;
import AIR.Common.Web.Session.HttpContext;
import TDS.Shared.Exceptions.TDSHttpException;

/**
 * @author mpatel
 *
 */
public class StaticFileHandler3
{

  
    private static final Logger _logger = LoggerFactory.getLogger (StaticFileHandler3.class);

//      private final static String RANGE_BOUNDARY = "<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>";
      private final static String MULTIPART_CONTENT_TYPE = "multipart/byteranges; boundary=<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>"; 
      private final static String MULTIPART_RANGE_DELIMITER = "--<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>\r\n"; 
      private final static String MULTIPART_RANGE_END = "--<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>--\r\n\r\n";
      private final static String CONTENT_RANGE_FORMAT = "bytes %s-%s/%s"; 
      private final static int MAX_RANGE_ALLOWED = 5;
//      private final static int ERROR_ACCESS_DENIED = 5;

      private StaticFileHandler3() {} 
      
      
      public void ProcessRequest(HttpServletRequest request,HttpServletResponse response) throws TDSHttpException, IOException, ParseException 
      {
          ProcessRequestInternal (request, response, null);
      } 

      private static boolean IsOutDated(String ifRangeHeader, Date lastModified) 
      {
          try 
          { 
            Calendar lastModifiedCal  = Calendar.getInstance ();
            lastModifiedCal.setTime (lastModified);
            lastModifiedCal.setTimeZone (TimeZone.getTimeZone ("UTC"));
            Date utcLastModified = lastModifiedCal.getTime ();
            
            Date utc = parseUTCDate(ifRangeHeader);
            
            
            return (utc.before (utcLastModified));
          } 
          catch (Exception e)
          {
            _logger.error (e.getMessage (),e);
              return true; 
          } 
      }
      
      private static String GenerateETag(Date lastModified, Date now) 
      {
          // Get 64-bit FILETIME stamp
          long lastModFileTime = lastModified.getTime ();
          long nowFileTime = Calendar.getInstance ().getTime ().getTime (); 
          String hexFileTime = Long.toHexString (lastModFileTime).toUpperCase ();

          // Do what IIS does to determine if this is a weak ETag. 
          // Compare the last modified time to now and if the difference is
          // less than or equal to 3 seconds, then it is weak 
          if ((nowFileTime - lastModFileTime) <= 3000) 
          {
              return "W/\"" + hexFileTime + "\"";
          }
          return  "\"" + hexFileTime + "\""; 
      }

      private static File GetFileInfo(_Ref<String> physicalPathRef) throws TDSHttpException 
      {
        File fileInfo = null;
          try 
          {
              String physicalPath = physicalPathRef.get ();
              fileInfo = new File(physicalPath);
              // Check whether the file exists
              if (!fileInfo.exists ()) 
              { 
                  //If file with the given filename does not exists than check if file with the same name and different case exists 
                  // And if it exists than return the new File object
                  _Ref<File> fileInfoRef =new _Ref<File> (fileInfo);
                  if(isCaseInsensitiveFileExists (fileInfoRef,physicalPathRef)) {
                    physicalPath = physicalPathRef.get ();
                    fileInfo = fileInfoRef.get ();
                  } else {
                    //Try to find file ignoring the case
                    _logger.error ("::File_does_not_exist:::");
                    throw new TDSHttpException(HttpStatus.NOT_FOUND.value (), "File_does_not_exist");
                  }
              }
              
              // To prevent the trailing dot problem, error out all file names with trailing dot. 
              if (physicalPath.charAt (physicalPath.length ()-1) == '.') 
              {
                _logger.error ("::File with trailing dot:::");
                  throw new TDSHttpException(HttpStatus.NOT_FOUND.value (), "File_does_not_exist"); 
              }

              // To be consistent with IIS, we won't serve out hidden files 
              if (fileInfo.isHidden ()) 
              { 
                  throw new TDSHttpException(HttpStatus.NOT_FOUND.value (), "File_is_hidden"); 
              }
    
          } 
          catch (SecurityException secEx) 
          {
            _logger.error (secEx.getMessage (),secEx);
              throw new TDSHttpException(HttpStatus.UNAUTHORIZED.value (), "File_enumerator_access_denied");
          } 
          return fileInfo;
      }
      /**
       * Ignore the filename case in the parent directory and check if file exists
       * @param file
       * @return
       */
      private static boolean isCaseInsensitiveFileExists(_Ref<File> fileRef,_Ref<String> physicalPathRef) {
        String pathEncoded = HttpContext.getCurrentContext ().getRequest ().getParameter("path");
        String pathDecoded = EncryptionHelper.DecodeFromBase64(pathEncoded);
        String fileName = HttpContext.getCurrentContext ().getRequest ().getParameter("file");
        _logger.info ("pathEncoded::"+pathEncoded);
        _logger.info ("pathDecoded::"+pathEncoded);
        _logger.info ("fileName::"+pathEncoded);
        File fileDir = new File(pathDecoded);
        for(String fileInDir:fileDir.list ()) {
          if(fileInDir.equalsIgnoreCase (fileName)) {
            fileRef.set (new File(pathDecoded,fileInDir));
            physicalPathRef.set (physicalPathRef.get ().replace (fileName, fileInDir));
            _logger.info ("Actual Case sensitive File Name ::"+fileInDir);
            return true;
          }
        }
        return false;
      }
      
      // initial space characters are skipped, and the String of digits up until the first non-digit
      // are converted to a long.  If digits are found the method returns true; otherwise false. 
      private static boolean GetLongFromSubstring(String s, ReferenceArgument refArg) 
      {
          refArg.setResult (0);

          // get index of first digit
          MovePastSpaceCharacters(s, refArg);
          int beginIndex = refArg.getStartIndex ();

          // get index of last digit
          MovePastDigits(s, refArg); 
          int endIndex = refArg.getStartIndex () - 1; 

          // are there any digits? 
          if (endIndex < beginIndex) 
          {
              return false;
          }

          long multipleOfTen = 1;
          for(int i = endIndex; i >= beginIndex; i--) 
          {
              int digit = s.charAt (i) - '0'; 
              refArg.setResult (refArg.getResult () + digit * multipleOfTen);
              multipleOfTen *= 10; 
              // check for overflow
              if (refArg.getResult () < 0) {
                  return false;
              } 
          }
          return true; 
      } 

      // The Range header consists of one or more byte range specifiers.  E.g, "Range: bytes=0-1024,-1024" is a request 
      // for the first and last 1024 bytes of a file. Before this method is called, startIndex points to the beginning
      // of a byte range specifier; and afterwards it points to the beginning of the next byte range specifier.
      // If the current byte range specifier is syntactially inavlid, this function will return false indicating that the
      // Range header must be ignored.  If the function returns true, then the byte range specifier will be converted to 
      // an offset and length, and the startIndex will be incremented to the next byte range specifier.  The byte range
      // specifier (offset and length) returned by this function is satisfiable if and only if isSatisfiable is true. 
      private static boolean GetNextRange(String rangeHeader, long fileLength, ReferenceArgument refArg) 
      {
          // startIndex is first char after '=', or first char after ','
          // Debug.Assert(startIndex < rangeHeader.Length, "startIndex < rangeHeader.Length"); 
          if ((refArg.getStartIndex () < rangeHeader.length ()) != true) throw new IllegalArgumentException("startIndex < rangeHeader.Length");

          refArg.setOffset (0);
          refArg.setLength (0);
          refArg.setSatisfiable (false);

          // A Range request to an empty file is never satisfiable, and will always receive a 416 status. 
          if (fileLength <= 0) 
          { 
              // put startIndex at end of String so we don't try to call GetNextRange again
              refArg.setStartIndex (rangeHeader.length ()); 
              return true;
          }

          MovePastSpaceCharacters(rangeHeader, refArg); 

          if (refArg.getStartIndex () < rangeHeader.length () && rangeHeader.charAt (refArg.getStartIndex ()) == '-') 
          { 
              // this range is of the form "-mmm" 
              refArg.incrementStartIndex ();
              
              boolean result = GetLongFromSubstring(rangeHeader, refArg);
              refArg.setLength (refArg.getResult ());
              if (!result) 
              { 
                  return false;
              }
              if (refArg.getLength () > fileLength) 
              {
                  // send entire file 
                  refArg.setOffset (0);
                  refArg.setLength (fileLength);
              } 
              else 
              {
                  // send last N bytes 
                  refArg.setOffset (fileLength - refArg.getLength ());
              }
              refArg.setSatisfiable (IsRangeSatisfiable(refArg.getOffset (), refArg.getLength (), fileLength));
              // we parsed the current range, but need to successfully move the startIndex to the next range 
              return IncrementToNextRange(rangeHeader, refArg);
          } 
          else 
          { 
              // this range is of the form "nnn-[mmm]"
              boolean result = GetLongFromSubstring(rangeHeader, refArg);
              refArg.setOffset (refArg.getResult ());
              if (!result) 
              { 
                  return false;
              }
              // increment startIndex past '-'
              if (refArg.getStartIndex () < rangeHeader.length () && rangeHeader.charAt(refArg.getStartIndex ()) == '-') 
              { 
                  refArg.incrementStartIndex ();
              } 
              else 
              { 
                  return false;
              } 
              long endPos;
              result = GetLongFromSubstring(rangeHeader, refArg);
              endPos = refArg.getResult ();
              if (!result) 
              {
                  // assume range is of form "nnn-".  If it isn't,
                  // the call to IncrementToNextRange will return false 
                refArg.setLength (fileLength - refArg.getOffset ());
              } 
              else 
              { 
                  // if...greater than or equal to the current length of the entity-body, last-byte-pos
                  // is taken to be equal to one less than the current length of the entity- body in bytes. 
                  if (endPos > fileLength - 1) 
                  {
                      endPos = fileLength - 1;
                  }

                  refArg.setLength (endPos - refArg.getOffset () + 1);

                  if (refArg.getLength () < 1) 
                  { 
                      // the byte range specifier is syntactially invalid
                      // because the last-byte-pos < first-byte-pos 
                      return false;
                  }
              }
              refArg.setSatisfiable (IsRangeSatisfiable(refArg.getOffset (), refArg.getLength (), fileLength)); 
              // we parsed the current range, but need to successfully move the startIndex to the next range
              return IncrementToNextRange(rangeHeader, refArg); 
          } 
      }

      private static boolean IncrementToNextRange(String s, ReferenceArgument refArg) 
      {
          // increment startIndex until next token and return true, unless the syntax is invalid
          MovePastSpaceCharacters(s, refArg);
          if (refArg.getStartIndex () < s.length ()) 
          { 
              if (s.charAt(refArg.getStartIndex()) != ',') 
              {
                  return false; 
              } 
              // move to first char after ','
              refArg.incrementStartIndex (); 
          }
          return true;
      }

      private static boolean IsRangeSatisfiable(long offset, long length, long fileLength) 
      {
          return (offset < fileLength && length > 0); 
      } 

      public boolean isReusable()
      { 
          return true; 
      }

    /*  private static boolean IsSecurityError(int ErrorCode) 
      { 
          return(ErrorCode == ERROR_ACCESS_DENIED);
      } */

      private static void MovePastSpaceCharacters(String s, ReferenceArgument refArg) 
      {
          while (refArg.getStartIndex () < s.length () && s.charAt(refArg.getStartIndex ()) == ' ') 
          { 
              refArg.incrementStartIndex (); 
          }
      }

      private static void MovePastDigits(String s, ReferenceArgument refArg) 
      {
          while (refArg.getStartIndex () < s.length () && s.charAt (refArg.getStartIndex ()) <= '9' && s.charAt(refArg.getStartIndex ()) >= '0') 
          { 
            refArg.incrementStartIndex ();
          }
      } 


      private static boolean ProcessRangeRequest(HttpServletRequest request, HttpServletResponse response, 
                                               String physicalPath,
                                               long fileLength, 
                                               String rangeHeader, 
                                               String etag,
                                               Date lastModified) throws IOException, TDSHttpException 
      { 
          boolean handled = false;

          // return "416 Requested range not satisfiable" if the file length is zero.
          if (fileLength <= 0) 
          { 
              SendRangeNotSatisfiable(response, fileLength); 
              handled = true;
              return handled; 
          }

          String ifRangeHeader = request.getHeader("If-Range");
          if (ifRangeHeader != null && ifRangeHeader.length () > 1) 
          { 
              // Is this an ETag or a Date? We only need to check two
              // characters; an ETag either begins with W/ or it is quoted. 
              if (ifRangeHeader.charAt (0) == '"') 
              { 
                  // it's a strong ETag
                  if (ifRangeHeader != etag) 
                  { 
                      // the etags do not match, and we will therefore return the entire response
                      return handled;
                  }
              } 
              else if (ifRangeHeader.charAt (0) == 'W' && ifRangeHeader.charAt (1) == '/') 
              {
                  // it's a weak ETag, and is therefore not usable for sub-range retrieval and 
                  // we will return the entire response 
                  return handled;
              } 
              else 
              {
                  // It's a date. If it is greater than or equal to the last-write time of the file, we can send the range.
                  if (IsOutDated(ifRangeHeader, lastModified)) 
                  {
                      return handled; 
                  }
              } 
          } 

          // the expected format is "bytes = <range1>[, <range2>, ...]" 
          // where <range> is "<first_byte_pos>-[<last_byte_pos>]" or "-<last_n_bytes>".
          int indexOfEquals = rangeHeader.indexOf('=');
          if (indexOfEquals == -1 || indexOfEquals == rangeHeader.length () - 1) 
          {
              //invalid syntax 
              return handled;
          } 

          // iterate through the byte ranges and write each satisfiable range to the response
          ReferenceArgument refArg = new ReferenceArgument ();
          refArg.setStartIndex (indexOfEquals + 1);
          boolean isRangeHeaderSyntacticallyValid = true;
          boolean exceededMax = false;
          ByteRange[] byteRanges = null;
          int sizeOfByteRange = 16; //size of two long variables
          int byteRangesCount = 0; 
          long totalBytes = 0;
          while (refArg.getStartIndex () < rangeHeader.length () && isRangeHeaderSyntacticallyValid) 
          { 
              isRangeHeaderSyntacticallyValid = GetNextRange(rangeHeader, fileLength, refArg);
              if (!isRangeHeaderSyntacticallyValid) 
              {
                  break;
              } 
              if (!refArg.isSatisfiable ()) 
              {
                  continue; 
              } 
              if (byteRanges == null) 
              {
                  byteRanges = new ByteRange[16]; 
              }

              if (byteRangesCount >= byteRanges.length) 
              {
                  // grow byteRanges array
                  ByteRange[] buffer = new ByteRange[byteRanges.length * 2]; 
                  long byteCount = byteRanges.length * sizeOfByteRange;
                  System.arraycopy(byteRanges, 0, buffer, 0,  (int) byteCount);

                  byteRanges = buffer;
              }
              if (byteRanges[byteRangesCount] == null)  {
                byteRanges[byteRangesCount] = new ByteRange();
              }
              byteRanges[byteRangesCount].setOffset ( refArg.getOffset ()); 
              byteRanges[byteRangesCount].setLength (refArg.getLength ());
              byteRangesCount++; 
              // IIS imposes this limitation too, and sends "400 Bad Request" if exceeded 
              totalBytes += refArg.getLength ();
              if (totalBytes > fileLength * MAX_RANGE_ALLOWED) 
              { 
                  exceededMax = true;
                  break;
              }
          } 

          if (!isRangeHeaderSyntacticallyValid) 
          { 
              return handled; 
          }

          if (exceededMax) 
          {
              SendBadRequest(response);
              handled = true;
              return handled; 
          }

          if (byteRangesCount == 0) 
          { 
              // we parsed the Range header and found no satisfiable byte ranges, so return "416 Requested Range Not Satisfiable"
              SendRangeNotSatisfiable(response, fileLength); 
              handled = true;
              return handled;
          }

          String contentType = MimeMapping.getMapping(physicalPath);
          if (byteRangesCount == 1) 
          { 
              refArg.setOffset (byteRanges[0].getOffset ()); 
              refArg.setLength (byteRanges[0].getLength ());
              response.setContentType(contentType); 
              String contentRange = String.format(CONTENT_RANGE_FORMAT, refArg.getOffset (), refArg.getOffset () + refArg.getLength () - 1, fileLength);
              response.addHeader("Content-Range", contentRange);

              SendFile(physicalPath, refArg.getOffset (), refArg.getLength (), fileLength, response); 
          }
          else 
          { 
              response.setContentType(MULTIPART_CONTENT_TYPE); 
              String contentRange;
              String partialContentType = "Content-Type: " + contentType + "\r\n"; 
              for(int i = 0; i < byteRangesCount; i++) 
              {
                  refArg.setOffset (byteRanges[i].getOffset ());
                  refArg.setLength (byteRanges[i].getLength ());
                  response.getWriter ().write (MULTIPART_RANGE_DELIMITER); 
                  response.getWriter ().write (partialContentType);
                  response.getWriter ().write ("Content-Range: "); 
                  contentRange = String.format(CONTENT_RANGE_FORMAT, refArg.getOffset (), refArg.getOffset () + refArg.getLength () - 1, fileLength); 
                  response.getWriter ().write (contentRange);
                  response.getWriter ().write ("\r\n\r\n"); 
                  SendFile(physicalPath, refArg.getOffset (), refArg.getLength (), fileLength, response);
                  response.getWriter ().write ("\r\n");
              }
              response.getWriter ().write (MULTIPART_RANGE_END); 
          }

          // if we make it here, we're sending a "206 Partial Content" status 
          response.setStatus (HttpStatus.PARTIAL_CONTENT.value ());
          response.addHeader ("Last-Modified", FormatHttpDateTime(lastModified)); 
          response.addHeader("Accept-Ranges", "bytes");
          response.addHeader("ETag", etag);
          response.addHeader("Cache-Control", "private");

          handled = true;
          return handled; 
      }


      /// <summary>
      /// Sets the caching used for resources on the http response cache policy.
      /// </summary>
      //TODO mpatel - This method is commented because we are not implementing caching for now.(As per discussion with Shiva)
      /*private static void SetCachingPolicy(HttpCachePolicy cachePolicy)
      {
          cachePolicy.SetCacheability(HttpCacheability.ServerAndPrivate);
          cachePolicy.SetNoServerCaching();
          cachePolicy.SetMaxAge(TimeSpan.FromMinutes(5));

//           NOTE: The two calls below prevent resources from being stored on 
//             disk but they also set the expiration to be immediate. Which means 
//             that if we preload images (e.x., grid) then the next time they are 
//             requested they will again go back to the server. So we cannot use these. 
          // cachePolicy.SetNoStore();
          // cachePolicy.SetCacheability(HttpCacheability.NoCache);
      }*/

      private static void SendBadRequest(HttpServletResponse response) throws IOException 
      { 
          response.setStatus(400);
          response.getWriter ().write("<html><body>Bad Request</body></html>");
      }

      
      public static void ProcessRequestInternal(HttpServletRequest request, HttpServletResponse response, String pathOverride) throws TDSHttpException, IOException 
      { 
          String physicalPath; 
          File fileInfo;
          long fileLength; 
          Date lastModifiedInUtc; 
          String etag;
          String rangeHeader; 
          _logger.info ("pathOverride::"+pathOverride);
          if (pathOverride != null)
          {
              physicalPath = pathOverride;
          }
          else
          {
              physicalPath = TDSHttpUtils.getCompleteRequestURL (request);
          }
           _Ref<String> physicalPathRef = new _Ref<String> (physicalPath);
          try
          {
              fileInfo = GetFileInfo(physicalPathRef);
              physicalPath = physicalPathRef.get ();
          }
          catch (TDSHttpException hex)
          {
            _logger.error (hex.getMessage (),hex);
              int httpCode = hex.getHttpStatusCode ();
              String httpError = String.format("%s (%s)", hex.getMessage (), physicalPath);
              throw new TDSHttpException(httpCode, httpError);
          }

          // Determine Last Modified Time.  We might need it soon 
          // if we encounter a Range: and If-Range header
          // Using UTC time to avoid daylight savings time bug 83230 
          Calendar lastModifiedCal = Calendar.getInstance ();
          lastModifiedCal.setTimeInMillis (fileInfo.lastModified ());
          lastModifiedInUtc = lastModifiedCal.getTime ();
              

          // Because we can't set a "Last-Modified" header to any time 
          // in the future, check the last modified time and set it to
          // Date.Now if it's in the future. 
          // This is to fix VSWhidbey #402323
          Date utcNow = new Date();
          if (lastModifiedInUtc.after (utcNow)) 
          {
              // use 1 second resolution 
            //TODO mpatel - Talk to Shiva on how to implement following
//              lastModifiedInUtc = new Date(utcNow.Ticks - (utcNow.Ticks % TimeSpan.TicksPerSecond), DateTimeKind.Utc);
          } 

          etag = GenerateETag(lastModifiedInUtc, utcNow);
          fileLength = fileInfo.length (); 

          // is this a Range request?
          rangeHeader = request.getHeader("Range");
          if (rangeHeader != null && rangeHeader.toLowerCase ().startsWith("bytes") && 
              ProcessRangeRequest(request,response, physicalPath, fileLength, rangeHeader, etag, lastModifiedInUtc)) 
          {
              return;
          }

          // if we get this far, we're sending the entire file
          SendFile(physicalPath, 0, fileLength, fileLength, response); 

          
          // We want to flush cache entry when static file has changed
//          response.AddFileDependency(physicalPath); 
          
          // set cache headers
          //TODO mpatel - Not doing caching for now as per discussion with Shiva
          /*HttpCachePolicy cachePolicy = response.Cache;
          SetCachingPolicy(cachePolicy);

          // set cache file info
          cachePolicy.SetETag(etag);
          cachePolicy.SetLastModified(lastModifiedInUtc);*/
      }
      
      private static void SendRangeNotSatisfiable(HttpServletResponse response, long fileLength) 
      {
          response.setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value ()); //416 - REQUESTED_RANGE_NOT_SATISFIABLE
          response.setContentType(null); 
          response.addHeader("Content-Range", "bytes */" + fileLength);
      } 

      private static void SendFile(String physicalPath, long offset, long length, long fileLength, HttpServletResponse response) throws TDSHttpException
      {
              // When not hosted on IIS, TransmitFile sends bytes in memory similar to WriteFile 
              // HttpRuntime.CheckFilePermission(physicalPath);
            
            //TODO mpatel - Following dot net code is transmitting the file in offsets. So make sure to come up with similar implementation in java
//              response.TransmitFile(physicalPath, offset, length); 
              
              
              //TODO mpatel- Following is the java way of sending entire file to response stream - Temp Fix Sending whole file as response stream rather than in offset 
              int DEFAULT_BUFFER_SIZE = 10240;
              
              // Init servlet response.
//              String preserveContentType = response.getContentType();
              
              response.reset();
              response.setBufferSize(DEFAULT_BUFFER_SIZE);
              response.setHeader("Content-Length", String.valueOf(fileLength));
              response.setDateHeader("Expires", System.currentTimeMillis()+1000*5*60);
//              response.setContentType(preserveContentType); 
              String contentRange = String.format(CONTENT_RANGE_FORMAT, offset, offset + length - 1, fileLength);
              response.addHeader("Content-Range", contentRange);
              
              // Specify content type. Use extension to do the mapping
              response.setContentType (MimeMapping.getMapping(physicalPath)); 
              // Static file handler supports byte ranges
              response.addHeader("Accept-Ranges", "bytes");
              
              //Required for Firefox 3.6 versions
              response.setStatus(HttpStatus.PARTIAL_CONTENT.value()); //set the status to 206
              
              BufferedInputStream input = null;
              BufferedOutputStream output = null;
  
              try {
                  // Open streams.
                  File file = new File(physicalPath);
                  input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
                  output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
  
                  // Write file contents to response.
                  byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                  int inputLength;
                  
                  input.skip(offset); //skip on requests for portions of bytes. Required for Firefox 3.6
                  
                  while ((inputLength = input.read(buffer)) > 0) {
                      output.write(buffer, 0, inputLength);
                  }
              } 
              catch(Exception e) {
                _logger.error (e.getMessage (),e);
                // Check for ERROR_ACCESS_DENIED and set the HTTP 
                // status such that the auth modules do their thing
//                if (IsSecurityError(e.ErrorCode)) 
                {
                    throw new TDSHttpException(HttpStatus.UNAUTHORIZED.value (),"Resource_access_forbidden : "+physicalPath); 
                }
//                throw e; 
              }
              finally {
                  // Gently close streams.
                  close(output);
                  close(input);
              }
              
      }
      
      private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
              _logger.error (e.getMessage (),e);
                // Do your thing with the exception. Print it, log it or mail it.
                e.printStackTrace();
            }
        }
    }

      private static String FormatHttpDateTime(Date dt) 
      {
          return formatUTCDate (dt);
      }
      
      public static Date parseUTCDate(String strDate) throws ParseException  {
        String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss a";
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone (TimeZone.getTimeZone("UTC"));
        return format.parse (strDate);
      }
      
      public static String formatUTCDate(Date date) {
        String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss a";
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone (TimeZone.getTimeZone("UTC"));
        return format.format (date);
      }
      
      private static class ByteRange 
      { 
        private long offset; 
        private long length;
        
        public long getOffset () {
          return offset;
        }
        public void setOffset (long offset) {
          this.offset = offset;
        }
        public long getLength () {
          return length;
        }
        public void setLength (long length) {
          this.length = length;
        }
      } 
      
      private static class ReferenceArgument {
        private int startIndex;
        private long result;
        private long offset;
        private boolean isSatisfiable;
        private long length;
        public int getStartIndex () {
          return startIndex;
        }
        public void setStartIndex (int startIndex) {
          this.startIndex = startIndex;
        }
        public long getResult () {
          return result;
        }
        public void setResult (long result) {
          this.result = result;
        }
        public long getOffset () {
          return offset;
        }
        public void setOffset (long offset) {
          this.offset = offset;
        }
        public boolean isSatisfiable () {
          return isSatisfiable;
        }
        public void setSatisfiable (boolean isSatisfiable) {
          this.isSatisfiable = isSatisfiable;
        }
        public long getLength () {
          return length;
        }
        public void setLength (long length) {
          this.length = length;
        }
        public void incrementStartIndex() {
          this.startIndex++;
        }
      }
}
