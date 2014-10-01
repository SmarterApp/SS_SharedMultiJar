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
package AIR.Common.Utilities;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
public class Path
{
  public static String combine (String dir, String fileName) {
    if (StringUtils.isEmpty (dir))
      return fileName;
    else if (StringUtils.isEmpty (fileName))
      return dir;
    if (dir.endsWith(File.separator)){
      return String.format ("%s%s", dir, fileName);
    } else {
      return String.format ("%s%s%s", dir, File.separator, fileName);
    }
  }

  /*
   * This has the same semantics as Path
   * http://msdn.microsoft.com/en-us/library/system.io.path.getfilename.aspx
   */
  public static String getFileName (String path) {
    return FilenameUtils.getName (path);
  }

  public static boolean exists (String path) {
    return (new File (path)).exists ();
  }

  public static String getFileNameWithoutExtension (String fileName)
  {
    return FilenameUtils.getBaseName (fileName);
  }

  public static String getExtension (String fileName)
  {
    return FilenameUtils.getExtension (fileName);
  }
  
  public static String getDirectoryName (String path) {
    if (StringUtils.endsWith (path, "/") || StringUtils.endsWith (path, "\\"))
      return path.substring (0, path.length () - 1);
    File f = new File (path);
    return f.getParent ();
  }

  public static Collection<File> getFilesMatchingExtensions (String folder, final String[] extensions) {
    return (Collection<File>) FileUtils.listFiles (new File ("C:/WorkSpace/TDSCore/AppsCurrent/ItemPreview/TDS.ItemPreview.Web/Content"), new IOFileFilter ()
    {
      @Override
      public boolean accept (File arg0) {
        return matches (arg0.getAbsolutePath ());
      }

      @Override
      public boolean accept (File arg0, String arg1) {
        return matches (arg1);
      }

      private boolean matches (String file) {
        for (int counter1 = 0; counter1 < extensions.length; ++counter1) {
          if (StringUtils.endsWithIgnoreCase (file, extensions[counter1]))
            return true;
        }
        return false;
      }

    }, TrueFileFilter.INSTANCE);
  }

  public static void main (String[] arhs) {
    try {
      System.err.println (getFileName ("c:/zy/abc"));
      System.err.println (getFileName ("c:/zy/"));
      System.err.println (getFileName ("c:/zy/abc.txt"));
      System.err.println (getFileNameWithoutExtension ("c:/zy/abc"));
      System.err.println (getFileNameWithoutExtension ("c:/zy/"));
      System.err.println (getFileNameWithoutExtension ("c:/zy/abc.txt"));
      URI uri = new URI ("file://c:/file/xyz");
      System.err.println (uri.getScheme ());
      System.err.println (uri.getPath ());
      System.err.println (uri.getRawPath ());
      System.err.println (uri.getFragment ());

    } catch (Throwable exp) {
      exp.printStackTrace ();
    }
  }

}
