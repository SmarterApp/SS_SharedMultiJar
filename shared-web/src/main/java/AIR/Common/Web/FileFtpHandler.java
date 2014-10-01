/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
/**
 * 
 */
package AIR.Common.Web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import AIR.Common.Configuration.ConfigurationSection;
import AIR.Common.Utilities.TDSHttpUtils;
import TDS.Shared.Configuration.TDSCommonPropertyNames;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
// / <summary>
// /
// / </summary>

public class FileFtpHandler extends HttpServlet
{

  @Autowired
  @Qualifier ("appSettings")
  private ConfigurationSection appSettings      = null;

  private static final long    serialVersionUID = 1L;

  // Methods
  public FileFtpHandler () {
  }

  public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet (request, response);
  }

  public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequestInternal (request, response);
  }

  // TODO Shiva
  protected void processRequestInternal (HttpServletRequest httpRequest, HttpServletResponse httpResponse, String pathOverride) throws IOException {

    String physicalPath = TDSHttpUtils.getCompleteRequestURL (httpRequest);
    
    if (pathOverride != null) { physicalPath = pathOverride; }
    
    // write out file from ftp to web stream 
    writeFtp (physicalPath, httpResponse.getOutputStream ()); 
    httpResponse.setContentType (MimeMapping.getMapping (physicalPath));
    
    //We are not taking care of caching policy for now.
    // set cache headers StaticFileHandler2.SetCachingPolicy
//    (context.Response.Cache);

  }

  protected  void processRequestInternal (HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
    processRequestInternal (httpRequest, httpResponse, null);
  }

  // / <summary>
  // / The serverUri parameter should use the ftp:// scheme.
  // / Example: ftp://air.org/someFile.txt.
  // / </summary>
  public boolean allowScheme (String filePath) {
     if (appSettings.getBoolean (TDSCommonPropertyNames.ALLOW_FTP)) {
      // check if file path is ftp scheme
      return StringUtils.startsWith (filePath, "ftp://");
    }

    return false;
  }

  
  public static InputStream openStream (URI requestUri) throws FtpResourceException {
    try {
      URL url = requestUri.toURL ();
      URLConnection urlc = url.openConnection ();
      return urlc.getInputStream ();
    } catch (IOException exp) {
      throw new FtpResourceException (exp);
    }
  }
  
  /**
   * Makes FTP call to the provided URI and retrieves contents
   *  
   * @param uri
   * @return ByteArrayInputStream
   * @throws FtpResourceException
   */
  public static byte[] getBytes (URI uri) throws FtpResourceException {
    try {
      FTPClient ftp = new FTPClient ();
      String[] credentialsAndHost = uri.getAuthority().split("@");
      String host = credentialsAndHost[1];
      String[] credentials = credentialsAndHost[0].split(":");

      ftp.connect (host);
      ftp.enterLocalPassiveMode();
      if (!ftp.login (credentials[0], credentials[1])) {
        ftp.logout ();
        ftp.disconnect ();
        throw new RuntimeException ("FTP Authentication Failure");
      }
      int reply = ftp.getReplyCode ();
      if (!FTPReply.isPositiveCompletion (reply)) {
        ftp.logout ();
        ftp.disconnect ();
        throw new RuntimeException ("FTP No reponse from server");
      }
      ftp.setFileType (FTPClient.BINARY_FILE_TYPE);

      ByteArrayOutputStream output = new ByteArrayOutputStream ();
      ftp.retrieveFile (uri.getPath (), output);
      output.close ();
      ftp.logout ();
      ftp.disconnect ();
      return output.toByteArray ();

    } catch (IOException ex) {
      throw new FtpResourceException (ex);
    }
  }

  public static InputStream openStream (String ftpFilePath) throws FtpResourceException {
    try {
      ftpFilePath = StringUtils.replace (ftpFilePath, "\\", "/");
      URI requestUri = new URI (ftpFilePath);
      return openStream (requestUri);
    } catch (URISyntaxException exp) {
      throw new FtpResourceException (exp);
    }
  }

  // TODO shiva see if we can use Apache Commons-Net
  // This code is specific to the specific
  // / <summary>
  // / Check if the file exists on the ftp server.
  // / </summary>
  public static boolean exists (URI requestUri) {
    try {
      requestUri.toURL ().openConnection ();
    } catch (MalformedURLException e) {
      throw new FtpResourceException (e);
    } catch (IOException e) {
      final String NON_EXISTENT_MESSAGE = "The system cannot find the file specified";
      if (StringUtils.contains (e.getMessage (), NON_EXISTENT_MESSAGE)) {
        // TODO Shiva this part is highly specific to the ftp server AIR uses on
        // its webservices.
        return false;
      }
      throw new FtpResourceException (e);
    }
    return true;
  }

  // TODO Shiva. Here I changed some logic which may cause issues with encoding.
  public static void writeFtp (String ftpFilePath, OutputStream ioO) throws IOException {
    InputStream io = openStream (ftpFilePath);
    OutputStreamWriter outputStream = new OutputStreamWriter (ioO);

    try (InputStreamReader inputStream = new InputStreamReader (io)) {
      int bufferSize = 2048;
      int readCount;
      char[] buffer = new char[bufferSize];

      readCount = inputStream.read (buffer, 0, bufferSize);

      while (readCount > 0) {
        outputStream.write (buffer, 0, readCount);
        readCount = inputStream.read (buffer, 0, bufferSize);
      }
    }
  }
}
