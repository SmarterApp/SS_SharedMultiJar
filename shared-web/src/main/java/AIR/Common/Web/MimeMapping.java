/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jmambo
 *
 */
public class MimeMapping
{
  // Fields
  private static Map<String, String> _extensionToMimeMappingTable = new HashMap<>();

  // Methods
  static {
      // CUSTOM:
      addMapping(".swf", "application/x-shockwave-flash");
      addMapping(".flv", "video/x-flv");
      addMapping(".png", "image/png");
      addMapping(".ogg", "application/ogg"); // audio/ogg
      addMapping(".m4a", "audio/mp4");
      addMapping(".mp4", "video/mp4");

      // ASP.NET's:
      addMapping(".323", "text/h323");
      addMapping(".asx", "video/x-ms-asf");
      addMapping(".acx", "application/internet-property-stream");
      addMapping(".ai", "application/postscript");
      addMapping(".aif", "audio/x-aiff");
      addMapping(".aiff", "audio/aiff");
      addMapping(".axs", "application/olescript");
      addMapping(".aifc", "audio/aiff");
      addMapping(".asr", "video/x-ms-asf");
      addMapping(".avi", "video/x-msvideo");
      addMapping(".asf", "video/x-ms-asf");
      addMapping(".au", "audio/basic");
      addMapping(".application", "application/x-ms-application");
      addMapping(".bin", "application/octet-stream");
      addMapping(".bas", "text/plain");
      addMapping(".bcpio", "application/x-bcpio");
      addMapping(".bmp", "image/bmp");
      addMapping(".cdf", "application/x-cdf");
      addMapping(".cat", "application/vndms-pkiseccat");
      addMapping(".crt", "application/x-x509-ca-cert");
      addMapping(".c", "text/plain");
      addMapping(".css", "text/css");
      addMapping(".cer", "application/x-x509-ca-cert");
      addMapping(".crl", "application/pkix-crl");
      addMapping(".cmx", "image/x-cmx");
      addMapping(".csh", "application/x-csh");
      addMapping(".cod", "image/cis-cod");
      addMapping(".cpio", "application/x-cpio");
      addMapping(".clp", "application/x-msclip");
      addMapping(".crd", "application/x-mscardfile");
      addMapping(".deploy", "application/octet-stream");
      addMapping(".dll", "application/x-msdownload");
      addMapping(".dot", "application/msword");
      addMapping(".doc", "application/msword");
      addMapping(".dvi", "application/x-dvi");
      addMapping(".dir", "application/x-director");
      addMapping(".dxr", "application/x-director");
      addMapping(".der", "application/x-x509-ca-cert");
      addMapping(".dib", "image/bmp");
      addMapping(".dcr", "application/x-director");
      addMapping(".disco", "text/xml");
      addMapping(".exe", "application/octet-stream");
      addMapping(".etx", "text/x-setext");
      addMapping(".evy", "application/envoy");
      addMapping(".eml", "message/rfc822");
      addMapping(".eps", "application/postscript");
      addMapping(".flr", "x-world/x-vrml");
      addMapping(".fif", "application/fractals");
      addMapping(".gtar", "application/x-gtar");
      addMapping(".gif", "image/gif");
      addMapping(".gz", "application/x-gzip");
      addMapping(".hta", "application/hta");
      addMapping(".htc", "text/x-component");
      addMapping(".htt", "text/webviewhtml");
      addMapping(".h", "text/plain");
      addMapping(".hdf", "application/x-hdf");
      addMapping(".hlp", "application/winhlp");
      addMapping(".html", "text/html");
      addMapping(".htm", "text/html");
      addMapping(".hqx", "application/mac-binhex40");
      addMapping(".isp", "application/x-internet-signup");
      addMapping(".iii", "application/x-iphone");
      addMapping(".ief", "image/ief");
      addMapping(".ivf", "video/x-ivf");
      addMapping(".ins", "application/x-internet-signup");
      addMapping(".ico", "image/x-icon");
      addMapping(".jpg", "image/jpeg");
      addMapping(".jfif", "image/pjpeg");
      addMapping(".jpe", "image/jpeg");
      addMapping(".jpeg", "image/jpeg");
      addMapping(".js", "application/x-javascript");
      addMapping(".lsx", "video/x-la-asf");
      addMapping(".latex", "application/x-latex");
      addMapping(".lsf", "video/x-la-asf");
      addMapping(".manifest", "application/x-ms-manifest");
      addMapping(".mhtml", "message/rfc822");
      addMapping(".mny", "application/x-msmoney");
      addMapping(".mht", "message/rfc822");
      addMapping(".mid", "audio/mid");
      addMapping(".mpv2", "video/mpeg");
      addMapping(".man", "application/x-troff-man");
      addMapping(".mvb", "application/x-msmediaview");
      addMapping(".mpeg", "video/mpeg");
      addMapping(".m3u", "audio/x-mpegurl");
      addMapping(".mdb", "application/x-msaccess");
      addMapping(".mpp", "application/vnd.ms-project");
      addMapping(".m1v", "video/mpeg");
      addMapping(".mpa", "video/mpeg");
      addMapping(".me", "application/x-troff-me");
      addMapping(".m13", "application/x-msmediaview");
      addMapping(".movie", "video/x-sgi-movie");
      addMapping(".m14", "application/x-msmediaview");
      addMapping(".mpe", "video/mpeg");
      addMapping(".mp2", "video/mpeg");
      addMapping(".mov", "video/quicktime");
      addMapping(".mp3", "audio/mpeg");
      addMapping(".mpg", "video/mpeg");
      addMapping(".ms", "application/x-troff-ms");
      addMapping(".nc", "application/x-netcdf");
      addMapping(".nws", "message/rfc822");
      addMapping(".oda", "application/oda");
      addMapping(".ods", "application/oleobject");
      addMapping(".pmc", "application/x-perfmon");
      addMapping(".p7r", "application/x-pkcs7-certreqresp");
      addMapping(".p7b", "application/x-pkcs7-certificates");
      addMapping(".p7s", "application/pkcs7-signature");
      addMapping(".pmw", "application/x-perfmon");
      addMapping(".ps", "application/postscript");
      addMapping(".p7c", "application/pkcs7-mime");
      addMapping(".pbm", "image/x-portable-bitmap");
      addMapping(".ppm", "image/x-portable-pixmap");
      addMapping(".pub", "application/x-mspublisher");
      addMapping(".pnm", "image/x-portable-anymap");
      addMapping(".pml", "application/x-perfmon");
      addMapping(".p10", "application/pkcs10");
      addMapping(".pfx", "application/x-pkcs12");
      addMapping(".p12", "application/x-pkcs12");
      addMapping(".pdf", "application/pdf");
      addMapping(".pps", "application/vnd.ms-powerpoint");
      addMapping(".p7m", "application/pkcs7-mime");
      addMapping(".pko", "application/vndms-pkipko");
      addMapping(".ppt", "application/vnd.ms-powerpoint");
      addMapping(".pmr", "application/x-perfmon");
      addMapping(".pma", "application/x-perfmon");
      addMapping(".pot", "application/vnd.ms-powerpoint");
      addMapping(".prf", "application/pics-rules");
      addMapping(".pgm", "image/x-portable-graymap");
      addMapping(".qt", "video/quicktime");
      addMapping(".ra", "audio/x-pn-realaudio");
      addMapping(".rgb", "image/x-rgb");
      addMapping(".ram", "audio/x-pn-realaudio");
      addMapping(".rmi", "audio/mid");
      addMapping(".ras", "image/x-cmu-raster");
      addMapping(".roff", "application/x-troff");
      addMapping(".rtf", "application/rtf");
      addMapping(".rtx", "text/richtext");
      addMapping(".sv4crc", "application/x-sv4crc");
      addMapping(".spc", "application/x-pkcs7-certificates");
      addMapping(".setreg", "application/set-registration-initiation");
      addMapping(".snd", "audio/basic");
      addMapping(".stl", "application/vndms-pkistl");
      addMapping(".setpay", "application/set-payment-initiation");
      addMapping(".stm", "text/html");
      addMapping(".shar", "application/x-shar");
      addMapping(".sh", "application/x-sh");
      addMapping(".sit", "application/x-stuffit");
      addMapping(".spl", "application/futuresplash");
      addMapping(".sct", "text/scriptlet");
      addMapping(".scd", "application/x-msschedule");
      addMapping(".sst", "application/vndms-pkicertstore");
      addMapping(".src", "application/x-wais-source");
      addMapping(".sv4cpio", "application/x-sv4cpio");
      addMapping(".tex", "application/x-tex");
      addMapping(".tgz", "application/x-compressed");
      addMapping(".t", "application/x-troff");
      addMapping(".tar", "application/x-tar");
      addMapping(".tr", "application/x-troff");
      addMapping(".tif", "image/tiff");
      addMapping(".txt", "text/plain");
      addMapping(".texinfo", "application/x-texinfo");
      addMapping(".trm", "application/x-msterminal");
      addMapping(".tiff", "image/tiff");
      addMapping(".tcl", "application/x-tcl");
      addMapping(".texi", "application/x-texinfo");
      addMapping(".tsv", "text/tab-separated-values");
      addMapping(".ustar", "application/x-ustar");
      addMapping(".uls", "text/iuls");
      addMapping(".vcf", "text/x-vcard");
      addMapping(".wps", "application/vnd.ms-works");
      addMapping(".wav", "audio/wav");
      addMapping(".wrz", "x-world/x-vrml");
      addMapping(".wri", "application/x-mswrite");
      addMapping(".wks", "application/vnd.ms-works");
      addMapping(".wmf", "application/x-msmetafile");
      addMapping(".wcm", "application/vnd.ms-works");
      addMapping(".wrl", "x-world/x-vrml");
      addMapping(".wdb", "application/vnd.ms-works");
      addMapping(".wsdl", "text/xml");
      addMapping(".xml", "text/xml");
      addMapping(".xlm", "application/vnd.ms-excel");
      addMapping(".xaf", "x-world/x-vrml");
      addMapping(".xla", "application/vnd.ms-excel");
      addMapping(".xls", "application/vnd.ms-excel");
      addMapping(".xof", "x-world/x-vrml");
      addMapping(".xlt", "application/vnd.ms-excel");
      addMapping(".xlc", "application/vnd.ms-excel");
      addMapping(".xsl", "text/xml");
      addMapping(".xbm", "image/x-xbitmap");
      addMapping(".xlw", "application/vnd.ms-excel");
      addMapping(".xpm", "image/x-xpixmap");
      addMapping(".xwd", "image/x-xwindowdump");
      addMapping(".xsd", "text/xml");
      addMapping(".z", "application/x-compress");
      addMapping(".zip", "application/x-zip-compressed");
      addMapping(".*", "application/octet-stream");
  }

  private static void addMapping(String extension, String mimeType)
  {
      _extensionToMimeMappingTable.put(extension.toLowerCase (), mimeType);
  }

  public static String getMapping(String fileName)
  {
      String str = null;
      int startIndex = fileName.lastIndexOf('.');
      if ((0 < startIndex) && (startIndex > fileName.lastIndexOf('\\')))
      {
          str =  _extensionToMimeMappingTable.get(fileName.substring(startIndex));
      }
      if (str == null)
      {
          str =  _extensionToMimeMappingTable.get (".*");
      }
      return str;
  }
}
