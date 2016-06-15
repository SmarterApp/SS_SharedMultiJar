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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import AIR.Common.Utilities.Path;
import AIR.Common.Utilities.TDSHttpUtils;
import AIR.Common.Web.Session.HttpContext;
import TDS.Shared.Exceptions.TDSHttpException;

/**
 * @author mpatel
 *
 */
public class StaticFileHandler2
{
  // Fields
//  private final int DEFAULT_CACHE_THRESHOLD = 262144;
  private final int ERROR_ACCESS_DENIED = 5;
  private static final Logger _logger = LoggerFactory.getLogger (StaticFileHandler2.class);

  // Methods
  public StaticFileHandler2()
  {
  }

  public boolean isReusable()
  {
        return true;
  }

  private static void BuildFileItemResponse(HttpServletRequest request, HttpServletResponse response, String fileName, long fileSize, Date lastModifiedTime, String strETag) throws IOException, TDSHttpException
  {
//      boolean readIntoMemory = false;
      int num = 262144;
      boolean flag2 = false;
      String str = request.getHeader ("Range");

      if ((str != null) && str.startsWith("bytes"))
      {
          flag2 = true;
      }
      
      if (flag2)
      {
          SendEntireEntity(request, strETag, lastModifiedTime);
      }

    //TODO mpatel/Shiva - Following Dotnet code is related to Memory Caching which we are not considering for now.
      /*if (((fileSize <= num) && !request.getMethod ().equalsIgnoreCase ("(GET)")) && !request.getMethod ().equalsIgnoreCase ("(HEAD)"))
      {
          readIntoMemory = true;
      }*/
      /*if (readIntoMemory)
      {
          response.WriteFile(fileName, readIntoMemory);
      }
      else
      {
          response.TransmitFile(fileName);
      }*/
      
      //Following is the java way of sending entire file to response stream -  Sending whole file as response stream rather than in offset 
        
        // Init servlet response.
        response.reset();
        response.setBufferSize(num);
        response.setHeader("Content-Length", String.valueOf(fileSize));

        // In order to display SVG files in an <img> tag, the browser needs to know the content type, where this isn't needed for other types
        if (fileName != null && fileName.toLowerCase().endsWith(".svg")) {
            response.setHeader("Content-Type", "image/svg+xml");
        }
      
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
  
        try {
            // Open streams.
            File file = new File(fileName);
            input = new BufferedInputStream(new FileInputStream(file), num);
            output = new BufferedOutputStream(response.getOutputStream(), num);
  
            // Write file contents to response.
            byte[] buffer = new byte[num];
            int inputLength;
            while ((inputLength = input.read(buffer)) > 0) {
                output.write(buffer, 0, inputLength);
            }
        } 
        catch(Exception e) {
          _logger.error (e.getMessage (),e);
          // Check for ERROR_ACCESS_DENIED and set the HTTP 
          // status such that the auth modules do their thing
  //        if (IsSecurityError(e.ErrorCode)) 
          {
              throw new TDSHttpException(HttpStatus.UNAUTHORIZED.value (),"Resource_access_forbidden : "+fileName); 
          }
  //        throw e; 
        }
        finally {
            // Gently close streams.
            close(output);
            close(input);
        }
      
      //TODO mpatel/Shiva - Following Dotnet code is related to Memory Caching which we are not considering for now.
     /* if (readIntoMemory)
      {
          response.Cache.AddValidationCallback(new HttpCacheValidateHandler(StaticFileHandler2.CacheValidateHandler), null);
          response.AddFileDependency(fileName);
          //response.Cache.SetExpires(Date.Now.AddDays(1));
      }*/
  }

  //TODO mpatel/Shiva - Following Dotnet code is related to  Caching which we are not considering for now.
 /* private static void CacheValidateHandler(HttpServletRequest request, Object data, HttpValidationStatus validationStatus)
  {
      if (((request.getHeaders("Range") != null) || request.getMethod ().equalsIgnoreCase ("(GET)")) || request.getMethod ().equalsIgnoreCase ("(HEAD)"))
      {
          validationStatus = HttpValidationStatus.IgnoreThisRequest;
      }
  }*/

  private static boolean CompareETags(String strETag1, String strETag2)
  {
      if (strETag1.equals("*") || strETag2.equals("*"))
      {
          return true;
      }

      if (strETag1.startsWith("W/"))
      {
          strETag1 = strETag1.substring(2);
      }
      
      if (strETag2.startsWith("W/"))
      {
          strETag2 = strETag2.substring(2);
      }
      
      return strETag2.equals(strETag1);
  }
  
  //TODO mpatel - Shiva is comming up with implementation for following method
  private static String GenerateETag(Date lastModTime)
  {
      StringBuilder builder = new StringBuilder();
      long num = new Date().getTime ();
      long num2 = lastModTime.getTime ();
      builder.append("\"");
      builder.append(Long.toHexString (num2));
      builder.append(":");
      builder.append(Long.toHexString (num));
      builder.append("\"");
      if ((num - num2) <= 3000L)
      {
          return ("W/" + builder.toString());
      }
      
      return builder.toString();
  }
  
  public static boolean IsSecurityError(int ErrorCode)
  {
      return (ErrorCode == 5);
  }

  public static void ProcessRequestInternal(HttpServletRequest request, HttpServletResponse response) throws TDSHttpException, IOException
  {
      ProcessRequestInternal(request,response, null);
  }

  public static void ProcessRequestInternal(HttpServletRequest request, HttpServletResponse response, String pathOverride) throws TDSHttpException, IOException
  {
    //TODO mpatel - Check Physicalpath and ge tCompleteRequestURL returns the same output.
//      String physicalPath = request.PhysicalPath;
    
      String pathInfo = request.getPathInfo ();
      String physicalPath = TDSHttpUtils.getCompleteRequestURL (request);
      
      String fileName = Path.getFileName(physicalPath);

      if (pathOverride != null)
      {
          physicalPath = pathOverride;
      }

      File info;
      if (!FileExists(physicalPath))
      {
          String error = "File does not exist";
          if (HttpContext.getCurrentContext ().isDebuggingEnabled ()) error += String.format(": %s", physicalPath);
          throw new TDSHttpException(404, error);
      }
      
      try
      {
          info = new File(physicalPath);
      }
     /* catch (IOException exception)
      {
          throw new TDSHttpException(404, "Error trying to enumerate files");
      }*/
      catch (SecurityException exception2)
      {
        _logger.error (exception2.getMessage (),exception2);
          throw new TDSHttpException(401, "File enumerator access denied");
      }

      if (info.length () == 0)
      {
          throw new TDSHttpException(404, "File is empty");
      }

      if (info.isHidden ())
      {
          throw new TDSHttpException(404, "File is hidden");
      }
      if (physicalPath.charAt (physicalPath.length () - 1) == '.')
      {
          throw new TDSHttpException(404, "File does not exist");
      }
      
      if (info.isDirectory ())
      {
          if (TDSHttpUtils.getCompleteRequestURL (request).endsWith ("/"))
          {
              throw new TDSHttpException(403, "Missing star mapping");
          }
          //TODO mpatel remove comment and find sollution Following code is from Dotnet
          response.sendRedirect (TDSHttpUtils.getCompleteRequestURL (request) + "/");
          
      }
      else
      {
          Date lastModTime = new Date(info.lastModified ());
          Calendar cal = Calendar.getInstance ();
          Date currentDateTime = cal.getTime ();
          if (lastModTime.after (currentDateTime))
          {
              lastModTime = currentDateTime;
          }
          
          String strETag = GenerateETag( lastModTime);
          
          try
          {
              BuildFileItemResponse(request, response, physicalPath, info.length (), lastModTime, strETag);
          }
          catch (Exception exception3)
          {
            _logger.error (exception3.getMessage (),exception3);
            //TODO mpatel - Check the equivalent of ExternalException in java
             /* if ((exception3 instanceof ExternalException) && IsSecurityError((except))
              {*/
                  throw new TDSHttpException(401, "Resource access forbidden");
//              }
          }
          
        //TODO mpatel/Shiva - Following Dotnet code is related to  Caching which we are not considering for now.
          // set cache headers
          /*HttpCachePolicy cachePolicy = context.Response.Cache;
          SetCachingPolicy(cachePolicy);
          
          // set cache file info
          cachePolicy.SetETag(strETag);
          cachePolicy.SetLastModified(lastModTime);*/
      }
  }

  
  private static void temporaryHack(HttpServletRequest request,HttpServletResponse response,File file) {
 // Get content type by filename.
    String contentType = request.getSession ().getServletContext ().getMimeType(file.getName());
    int DEFAULT_BUFFER_SIZE = 10240;
    // If content type is unknown, then set the default value.
    // To add new content types, add new mime-mapping entry in web.xml.
    if (contentType == null) {
        contentType = "application/octet-stream";
    }
    
    // Init servlet response.
    response.reset();
    response.setBufferSize(DEFAULT_BUFFER_SIZE);
    response.setContentType(contentType);
    response.setHeader("Content-Length", String.valueOf(file.length()));
    response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

    // Prepare streams.
    BufferedInputStream input = null;
    BufferedOutputStream output = null;

    try {
        // Open streams.
        input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
        output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

        // Write file contents to response.
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
    } 
    catch(Exception e) {
      e.printStackTrace ();
    }
    finally {
        // Gently close streams.
        close(output);
        close(input);
    }
  }
  
//Helpers (can be refactored to public utility class) ----------------------------------------

  private static void close(Closeable resource) {
      if (resource != null) {
          try {
              resource.close();
          } catch (IOException e) {
              // Do your thing with the exception. Print it, log it or mail it.
              e.printStackTrace();
          }
      }
  }
  /// <summary>
  /// Sets the caching used for resources on the http response cache policy.
  /// </summary>
//TODO mpatel/Shiva - Following Dotnet code is related to  Caching which we are not considering for now.
 /* public static void SetCachingPolicy(HttpCachePolicy cachePolicy)
  {
      cachePolicy.SetCacheability(HttpCacheability.Private);
      cachePolicy.SetNoServerCachrun clean ing();
      cachePolicy.SetMaxAge(TimeSpan.FromMinutes(5));

       NOTE: The two calls below prevent resources from being stored on 
         disk but they also set the expiration to be immediate. Which means 
         that if we preload images (e.x., grid) then the next time they are 
         requested they will again go back to the server. So we cannot use these. 
      // cachePolicy.SetNoStore();
      // cachePolicy.SetCacheability(HttpCacheability.NoCache);
  }*/

  private static boolean SendEntireEntity(HttpServletRequest request, String strETag, Date lastModifiedTime)
  {
      boolean flag = false;
      String str = request.getHeader("If-Range");
      
      if (str == null)
      {
          return false;
      }
      
      if (str.charAt (0) == '"')
      {
          if (!CompareETags(str, strETag))
          {
              flag = true;
          }
          return flag;
      }
      
      try
      {
          DateFormat dateFormat = DateFormat.getDateInstance ();
          Date time = dateFormat.parse(str);
          if (lastModifiedTime.compareTo (time) == 1)
          {
              flag = true;
          }
      }
      catch(Exception e)
      {
          flag = true;
      }
      
      return flag;
  }

  private static boolean FileExists(String filename)
  {
      boolean flag = false;
      
      try
      {
          flag = new File(filename).exists ();
      }
      catch(Exception e)
      {
        e.printStackTrace ();
      }
      
      return flag;
  }
}
