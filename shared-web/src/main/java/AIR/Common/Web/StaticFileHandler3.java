/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
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


      private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.
      private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";


      private StaticFileHandler3() {} 
      
      
      public void ProcessRequest(HttpServletRequest request,HttpServletResponse response) throws TDSHttpException, IOException, ParseException 
      {
          ProcessRequestInternal (request, response, null);
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
        File fileDir = new File(pathDecoded);
        for(String fileInDir:fileDir.list ()) {
          if(fileInDir.equalsIgnoreCase (fileName)) {
            fileRef.set (new File(pathDecoded,fileInDir));
            physicalPathRef.set (physicalPathRef.get ().replace (fileName, fileInDir));
            return true;
          }
        }
        return false;
      }
      
     
      public static void ProcessRequestInternal(HttpServletRequest request, HttpServletResponse response, String pathOverride) throws TDSHttpException, IOException 
      { 
          String physicalPath; 
          File fileInfo;
          long fileLength; 
          Date lastModifiedInUtc; 
          String etag;
          String rangeHeader; 
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
          etag = GenerateETag(lastModifiedInUtc, utcNow);
          fileLength = fileInfo.length (); 

          
          
          
          // is this a Range request?
          rangeHeader = request.getHeader("Range");
          if (rangeHeader != null && rangeHeader.toLowerCase ().startsWith("bytes") )//&& 
//              ProcessRangeRequest(request,response, physicalPath, fileLength, rangeHeader, etag, lastModifiedInUtc)) 
          {
        	  processRangeRequest(request, response, true, physicalPath, fileLength, rangeHeader, etag, lastModifiedInUtc);
        	  return;
          }

          // if we get this far, we're sending the entire file
          SendFile(physicalPath, response);
          
      }
      
      private static void SendRangeNotSatisfiable(HttpServletResponse response, long fileLength) 
      {
          response.setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value ()); //416 - REQUESTED_RANGE_NOT_SATISFIABLE
          response.setContentType(null); 
          response.addHeader("Content-Range", "bytes */" + fileLength);
      } 

      private static void SendFile(String physicalPath, HttpServletResponse response) throws TDSHttpException
      {
    	  	
              File srcFile;
			try {
				srcFile = new File(physicalPath);
			} catch (Exception e) {
	             throw new TDSHttpException(HttpStatus.UNAUTHORIZED.value (),"Resource_access_forbidden : "+physicalPath); 
			}
              try {
				byte[] bytes = java.nio.file.Files.readAllBytes(srcFile.toPath());

                // In order to display SVG files in an <img> tag, the browser needs to know the content type, where this isn't needed for other types
                if (physicalPath != null && physicalPath.toLowerCase().endsWith(".svg")) {
                    response.setHeader("Content-Type", "image/svg+xml");
                }

				response.getOutputStream().write(bytes);
			}  catch (IOException e) {
				_logger.error("Error while writing file to response output stream:: ",e);
				throw new TDSHttpException(HttpStatus.UNAUTHORIZED.value (),"Not able to write file to Outputstream: "+physicalPath);
			}
              
      }
      
	private static void processRangeRequest(HttpServletRequest request,
			HttpServletResponse response, boolean content, String physicalPath,
			long fileLength, String rangeHeader, String eTag, Date lastModifiedDate)
			throws IOException {
		// Validate the requested file ------------------------------------------------------------
        // URL-decode the file name (might contain spaces and on) and prepare file object.
        File file = new File(physicalPath);

        // Check if file actually exists in filesystem.
        if (!file.exists()) {
            // Do your thing if the file appears to be non-existing.
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // return "416 Requested range not satisfiable" if the file length is zero.
        if (fileLength <= 0) 
        { 
            SendRangeNotSatisfiable(response, fileLength); 
            return;
        }

        // Prepare some variables. The ETag is an unique identifier of the file.
        String fileName = file.getName();
        long length = fileLength;
        long lastModified = lastModifiedDate.getTime();
//        String eTag = fileName + "_" + fileLength + "_" + lastModified;
        long expires =  System.currentTimeMillis()+1000*5*60;


        // Validate request headers for caching ---------------------------------------------------

        // If-None-Match header should contain "*" or ETag. If so, then return 304.
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setHeader("ETag", eTag); // Required in 304.
            response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
            return;
        }

        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
        // This header is ignored if any If-None-Match header is specified.
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setHeader("ETag", eTag); // Required in 304.
            response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
            return;
        }


        // Validate request headers for resume ----------------------------------------------------

        // If-Match header should contain "*" or ETag. If not, then return 412.
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !matches(ifMatch, eTag)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }


        // Validate and process range -------------------------------------------------------------

        // Prepare some variables. The full Range represents the complete file.
        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<Range>();

        // Validate and process Range and If-Range headers.
        String range = request.getHeader("Range");
        if (range != null) {

            // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }

            // If-Range header should either match ETag or be greater then LastModified. If not,
            // then return full file.
            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(eTag)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte range.
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    // Assuming a file with length of 100, the following examples returns bytes at:
                    // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                    long start = sublong(part, 0, part.indexOf("-"));
                    long end = sublong(part, part.indexOf("-") + 1, part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    // Add range.
                    ranges.add(new Range(start, end, length));
                }
            }
        }


        // Prepare and initialize response --------------------------------------------------------

        // Get content type by file name and set default GZIP support and content disposition.
        String contentType = MimeMapping.getMapping(physicalPath);
        boolean acceptsGzip = false;
        String disposition = "inline";

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // If content type is text, then determine whether GZIP content encoding is supported by
        // the browser and expand content type with the one and right character encoding.
        if (contentType.startsWith("text")) {
            String acceptEncoding = request.getHeader("Accept-Encoding");
            acceptsGzip = acceptEncoding != null && accepts(acceptEncoding, "gzip");
            contentType += ";charset=UTF-8";
        } 

        // Else, expect for images, determine content disposition. If content type is supported by
        // the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
        else if (!contentType.startsWith("image")) {
            String accept = request.getHeader("Accept");
            disposition = accept != null && accepts(accept, contentType) ? "inline" : "attachment";
        }

        // Initialize response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", eTag);
        response.setDateHeader("Last-Modified", lastModified);
        response.setDateHeader("Expires", expires);


        // Send requested file (part(s)) to client ------------------------------------------------

        // Prepare streams.
        RandomAccessFile input = null;
        OutputStream output = null;

        try {
            // Open streams.
            input = new RandomAccessFile(file, "r");
            output = response.getOutputStream();

            if (ranges.isEmpty() || ranges.get(0) == full) {

                // Return full file.
                Range r = full;
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);

                if (content) {
                    if (acceptsGzip) {
                        // The browser accepts GZIP, so GZIP the content.
                        response.setHeader("Content-Encoding", "gzip");
                        output = new GZIPOutputStream(output, DEFAULT_BUFFER_SIZE);
                    } else {
                        // Content length is not directly predictable in case of GZIP.
                        // So only add it if there is no means of GZIP, else browser will hang.
                        response.setHeader("Content-Length", String.valueOf(r.length));
                    }

                    // Copy full range.
                    copy(input, output, r.start, r.length);
                }

            } else if (ranges.size() == 1) {

                // Return single part of file.
                Range r = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                if (content) {
                    // Copy single part range.
                    copy(input, output, r.start, r.length);
                }

            } else {

                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                if (content) {
                    // Cast back to ServletOutputStream to get the easy println methods.
                    ServletOutputStream sos = (ServletOutputStream) output;

                    // Copy multi part range.
                    for (Range r : ranges) {
                        // Add multipart boundary and header fields for every range.
                        sos.println();
                        sos.println("--" + MULTIPART_BOUNDARY);
                        sos.println("Content-Type: " + contentType);
                        sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

                        // Copy single part range of multi part range.
						copy(input, output, r.start, r.length);
						
                    }

                    // End with multipart boundary.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY + "--");
                }
            }
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }
	}
      

      /**
       * Returns true if the given accept header accepts the given value.
       * @param acceptHeader The accept header.
       * @param toAccept The value to be accepted.
       * @return True if the given accept header accepts the given value.
       */
      private static boolean accepts(String acceptHeader, String toAccept) {
          String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
          Arrays.sort(acceptValues);
          return Arrays.binarySearch(acceptValues, toAccept) > -1
              || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
              || Arrays.binarySearch(acceptValues, "*/*") > -1;
      }

      /**
       * Returns true if the given match header matches the given value.
       * @param matchHeader The match header.
       * @param toMatch The value to be matched.
       * @return True if the given match header matches the given value.
       */
      private static boolean matches(String matchHeader, String toMatch) {
          String[] matchValues = matchHeader.split("\\s*,\\s*");
          Arrays.sort(matchValues);
          return Arrays.binarySearch(matchValues, toMatch) > -1
              || Arrays.binarySearch(matchValues, "*") > -1;
      }

      /**
       * Returns a substring of the given string value from the given begin index to the given end
       * index as a long. If the substring is empty, then -1 will be returned
       * @param value The string value to return a substring as long for.
       * @param beginIndex The begin index of the substring to be returned as long.
       * @param endIndex The end index of the substring to be returned as long.
       * @return A substring of the given string value as long or -1 if substring is empty.
       */
      private static long sublong(String value, int beginIndex, int endIndex) {
          String substring = value.substring(beginIndex, endIndex);
          return (substring.length() > 0) ? Long.parseLong(substring) : -1;
      }
      
      
      /**
       * Copy the given byte range of the given input to the given output.
       * @param input The input to copy the given range to the given output for.
       * @param output The output to copy the given range from the given input for.
       * @param start Start of the byte range.
       * @param length Length of the byte range.
       * @throws IOException If something fails at I/O level.
       */
      private static void copy(RandomAccessFile input, OutputStream output, long start, long length)
      {
          try {
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			  int read;

			  if (input.length() == length) {
			      // Write full range.
			      while ((read = input.read(buffer)) > 0) {
			          output.write(buffer, 0, read);
			      }
			  } else {
			      // Write partial range.
			      input.seek(start);
			      long toRead = length;

			      while ((read = input.read(buffer)) > 0) {
			          if ((toRead -= read) > 0) {
			              output.write(buffer, 0, read);
			          } else {
			              output.write(buffer, 0, (int) toRead + read);
			              break;
			          }
			      }
			  }
          } catch (Exception e) {
				_logger.error("Error while copying file to output stream : "+e.toString());
			}
      }
      
      
      private static void close(Closeable resource) {
          if (resource != null) {
              try {
                  resource.close();
              } catch (IOException ie) {
            	  _logger.error(ie.getMessage());
              }
          }
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
      
      private static class Range 
      { 
    	  long start;
          long end;
          long length;
          long total;

          /**
           * Construct a byte range.
           * @param start Start of the byte range.
           * @param end End of the byte range.
           * @param total Total length of the byte source.
           */
          public Range(long start, long end, long total) {
              this.start = start;
              this.end = end;
              this.length = end - start + 1;
              this.total = total;
          }
      } 
      
      /*
      
      private final static String RANGE_BOUNDARY = "<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>";
      private final static String MULTIPART_CONTENT_TYPE = "multipart/byteranges; boundary=<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>"; 
      private final static String MULTIPART_RANGE_DELIMITER = "--<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>\r\n"; 
      private final static String MULTIPART_RANGE_END = "--<q1w2e3r4t5y6u7i8o9p0zaxscdvfbgnhmjklkl>--\r\n\r\n";
      private final static String CONTENT_RANGE_FORMAT = "bytes %s-%s/%s"; 
      private final static int MAX_RANGE_ALLOWED = 5;
      private final static int ERROR_ACCESS_DENIED = 5;
      
      
      private static String FormatHttpDateTime(Date dt) 
      {
          return formatUTCDate (dt);
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
      
       // initial space characters are skipped, and the String of digits up until the first non-digit
      // are converted to a long.  If digits are found the method returns true; otherwise false. 
      /*private static boolean GetLongFromSubstring(String s, ReferenceArgument refArg) 
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
      /*private static boolean GetNextRange(String rangeHeader, long fileLength, ReferenceArgument refArg) 
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

      private static boolean IsSecurityError(int ErrorCode) 
      { 
          return(ErrorCode == ERROR_ACCESS_DENIED);
      } 

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



      private static void SendBadRequest(HttpServletResponse response) throws IOException 
      { 
          response.setStatus(400);
          response.getWriter ().write("<html><body>Bad Request</body></html>");
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

      */
      
}
