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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import AIR.Common.Web.Session.HttpContext;

/**
 * @author Milan Patel
 */
public class BrowserParser
{

  
  private String _userAgent;
  private ReadableUserAgent _agentParser;

  // parser lookups
  static Map<BrowserOS, Pattern> _osVersionParsers = new HashMap<BrowserOS, Pattern>();
  static  Map<String, Pattern> _browserVersionParsers = new HashMap<String, Pattern>();

  // platforms
  private static final List<String> _platformWindows = Arrays.asList ("Windows","Win32","Win64"); 
  private static final  List<String> _platformOSX = Arrays.asList ( "Macintosh", "MacPPC", "MacIntel" );
  private static final  List<String> _platformLinux = Arrays.asList ( "X11", "Linux", "BSD" );
  private static final  List<String> _platformIOS = Arrays.asList ( "iPad", "iPod", "iPhone");
  
  
  static 
  {
      LoadOSVersionParsers();
      LoadBrowserVersionParsers();
  }

  static void AddOSVersionParser(BrowserOS os, String regex)
  {
      _osVersionParsers.put(os, Pattern.compile (regex));
  }

  static void AddBrowserVersionParser(String name, String regex)
  {
      _browserVersionParsers.put(name, Pattern.compile (regex));
  }

  static void LoadOSVersionParsers()
  {
      AddOSVersionParser(BrowserOS.OSX, "Mac OS X (\\d+\\.\\d+)");
      AddOSVersionParser(BrowserOS.Android, "Android (\\d+\\.\\d+)");
      AddOSVersionParser(BrowserOS.IOS, "i[PSa-z\\s]+;.*?CPU\\s[OSPa-z\\s]+(?:(\\d_\\d)|;)");
      AddOSVersionParser(BrowserOS.Chrome, "Chrome\\/(\\d+\\.\\d+)");
      AddOSVersionParser(BrowserOS.Windows, "Windows NT (\\d+\\.\\d+)");
  }

  static void LoadBrowserVersionParsers()
  {
      AddBrowserVersionParser("AIRSecureBrowser", "AIRSecureBrowser/(\\d+\\.\\d+)");
      AddBrowserVersionParser("AIRMobileSecureBrowser", "AIRMobile[SecureBrowser]*/(\\d+\\.\\d+)");
  }
  
  
  static double ParseVersion(String userAgent, Pattern regex)
  {
      double version = 0;

      Matcher matches = regex.matcher(userAgent);
      
      boolean result = matches.find ();
      
      if (result && matches.groupCount ()>=1)
      {
          String value = matches.group (1);
          if (!StringUtils.isEmpty (value))
          {
              value = value.replace('_', '.');
              version = Double.parseDouble (value);
          }
      }

      return version;
  }

  double ParseOSVersion(BrowserOS os)
  {
      Pattern versionParser = _osVersionParsers.get (os);

      if (versionParser!=null)
      {
          return ParseVersion(getUserAgent (), versionParser);
      }

      return 0;
  }

  double ParseBrowserVersion(String name)
  {
      Pattern versionParser = _browserVersionParsers.get (name);

      if (versionParser!=null)
      {
          return ParseVersion(getUserAgent (), versionParser);
      }

      return 0;
  }
  
  public static BrowserParser getCurrent () {
    return new BrowserParser ();
  }
  
  public BrowserParser(String userAgent)
  {
      _userAgent = userAgent;
      UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
      _agentParser = uaParser.parse(_userAgent);
  }
  
  public BrowserParser()
  {
      _userAgent = HttpContext.getCurrentContext ().getUserAgent ();
      UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
      _agentParser = uaParser.parse(_userAgent);
      
  }
  /**
   * The full user agent for the browser
   * @return String
   */
  public String getUserAgent () {
    return _userAgent;
  }
  
  /**
   * The full OS marketing name (e.x., Windows 98)
   * @return String
   */
  public String getOSFullName() {

      String osFullName = (_agentParser.getOperatingSystem ().getName () != null) ? _agentParser.getOperatingSystem ().getName () : "Unknown";
      
      // BUG #11956: Mac OS X 10.4
      // EXAMPLE: Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.8.1.7) Gecko/20090728 (SZcr0DctLYrAxsYhQbnS) AIRSecureBrowser/3.0
      // check if the browser could not be detected
      if (osFullName.equals ("Unknown"))
      {
        if (_userAgent.contains("OS X"))
        {
            return "Macintosh OS X";
        }
  
        // EXAMPLE: Mozilla/5.0 (X11; CrOS x86_64 2913.331.0) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.127.111 Safari/537.11 AIRSecureBrowser/1.1
        if (_userAgent.contains(" CrOS "))
        {
            return "Chrome OS";
        }
      }

    // BUG #54823: Windows 8
    // EXAMPLE: Mozilla/5.0 (Windows NT 6.2; WOW64; rv:10.0.1) Gecko/20100101 Firefox/10.0.1
    if (_userAgent.contains("Windows NT 6.2"))
    {
        return "Windows 8";
    }

    if (_userAgent.contains("Windows NT 6.3"))
    {
        return "Windows 8.1";
    }

    // BUG #123729: Diagnostic page displays incorrect OS for iOS SB 2.2
    if (osFullName == "iPhone OS X")
    {
        return "iOS " + getOSVersion ();
    }

    return osFullName;
  }

  /**
   *  The general OS platform being used (e.x., WINDOWS)
   *  @return BrowserOS
   */
  public BrowserOS getOsName()
  {
          if (_userAgent.contains(" CrOS "))
          {
              return BrowserOS.Chrome;
          }

          if (_userAgent.contains("Android"))
          {
              return BrowserOS.Android;
          }
          if (CollectionUtils.exists (_platformIOS, new Predicate()
          {
            @Override
            public boolean evaluate (Object object) {
              return _userAgent.contains ((String)object);
            }
          })) {
              return BrowserOS.IOS; // NOTE: needs to be before OS X
          }
          if(CollectionUtils.exists (_platformOSX, new Predicate()
          {
            @Override
            public boolean evaluate (Object object) {
              return _userAgent.contains ((String)object);
            }
          }))
          {
              return BrowserOS.OSX;
          }
          if(CollectionUtils.exists (_platformWindows, new Predicate()
          {
            @Override
            public boolean evaluate (Object object) {
              return _userAgent.contains ((String)object);
            }
          }))
          {
              return BrowserOS.Windows;
          }
          if(CollectionUtils.exists (_platformLinux, new Predicate()
          {
            @Override
            public boolean evaluate (Object object) {
              return _userAgent.contains ((String)object);
            }
          }))
          {
              return BrowserOS.Linux;
          }
          
          return BrowserOS.Unknown;
  }
  
  public double getOSVersion() {
    return ParseOSVersion (getOsName ());
  }
  
  /**
   *  Browsers name (e.x., Firefox)
   *  @return String
   */
  public String getName()
  {
          // check for secure browsers
          if (_userAgent != null)
          {
              if (_userAgent.contains("AIRSecureBrowser"))
              {
                  return "AIRSecureBrowser";
              }

              if (_userAgent.contains("AIRMobile"))
              {
                  return "AIRMobileSecureBrowser";
              }
          }

          if (_agentParser.getName () != null)
          {
              if (_agentParser.getName ().equals ("InternetExplorer"))
              {
                  return "IE";
              }
          }

          return _agentParser.getName ()!=null ?_agentParser.getName (): "";
  }
  
  public boolean isIE()
  {
          if (_userAgent == null) return false;
          return _userAgent.contains("MSIE") || 
                 _userAgent.contains("Trident");
  }
  
  public boolean isSafari()
  {
          if (_userAgent == null) return false;
          return _userAgent.contains("Safari");
  }

  public boolean isChrome()
  {
          if (_userAgent == null) return false;
          return _userAgent.contains("Chrome");
  }

  public boolean isWebKit()
  {
          if (_userAgent == null) return false;
          return _userAgent.contains("AppleWebKit");
  }

  public boolean isFirefox()
  {
          if (_userAgent == null) return false;

          // ignore browsers that are "like Gecko" (Safari, IE 11)
          if (_userAgent.contains("like Gecko")) return false;
          
          // check if Firefox
          return (_userAgent.contains("Firefox") || _userAgent.contains("Gecko"));
  }

  /// <summary>
  /// Check if browser is capable of playing ogg audio.
  /// </summary>
  public boolean isSupportsOggAudio()
  {
          // mobile devices do not support ogg regardless of the browser
          if (getOsName () == BrowserOS.IOS ||
              getOsName () == BrowserOS.Android) return false;

          // return browsers that support ogg natively
          return (isFirefox () || isChrome ());
  }

  /**
   *  The browsers version (e.x., 2.0)
   *  This is only major and minor version #'s
   * @return
   */
  
  public double getVersion()
  {
          // check if custom version parser
          double version = ParseBrowserVersion(getName ());
          if (version > 0) return version;

          // this code is not working in .NET 4.0, (e.x., Firefox 3.5: Max=3 + Min=5.0 = 8)
          // return browserCapabilities.MajorVersion + browserCapabilities.MinorVersion;

          // version String is an integer and a decimal
          if (_agentParser.getVersionNumber ().toVersionString () != null)
          {
            String strVersion = _agentParser.getVersionNumber ().toVersionString ();
            try {
              version = Double.parseDouble (strVersion);
            } catch (NumberFormatException e) {
              //Following code is for Version like 10.0.1(Version with more than 1 dot)
              if(strVersion!=null && strVersion.indexOf (".",strVersion.indexOf('.') + 1)!=-1) {
                strVersion = strVersion.substring (0,strVersion.indexOf  (".",strVersion.indexOf (".")+1));
                version = Double.parseDouble (strVersion);
              }
            }
          }
          return version;
  }

  /**
   *  Get the type of hardware this OS is running.
   * @return
   */
  public String getHardwareArchitecture()
  {
          if (getOsName () == BrowserOS.OSX)
          {
              if (_userAgent.contains("Intel")) return "Intel";
              if (_userAgent.contains("PPC")) return "PPC";
          }
          else if (getOsName () == BrowserOS.IOS)
          {
              if (_userAgent.contains("iPad")) return "iPad";
              if (_userAgent.contains("iPhone")) return "iPhone";
              if (_userAgent.contains("iPod")) return "iPod";
          }
          else if (getOsName () == BrowserOS.Chrome)
          {
              if (_userAgent.contains("i686")) return "i686";
              if (_userAgent.contains("x86_64")) return "x86_64";
              if (_userAgent.contains("armv7l")) return "ARM";
          }
          else if (getOsName () == BrowserOS.Windows)
          {
              if (_userAgent.contains("ARM;")) return "ARM";
              else return "Intel";
          }
          
          return null;
  }

  /**
   *  Check if the browser is one our valid secure browsers.
   * @return
   */
  public boolean isSecureBrowser()
  {
          // this can be null if using load testing app of some sort
          if (StringUtils.isEmpty (_userAgent)) return false;

          // check for desktop SB
          if (getName () == "AIRSecureBrowser") return true;

          // check for mobile SB
          if (getName () == "AIRMobileSecureBrowser") return true;
          return false;
  }
  
//Following method is for Testing Purpose only.
/*  public static void main (String[] args) {
//    String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:22.0) Gecko/20130328 Firefox/22.0";
//    String userAgent = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; HTC_DesireS_S510e Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
//    String userAgent = "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 Mobile/9B176 Safari/7534.48.3";
//    String userAgent = "Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.8.1.7) Gecko/20090728 (SZcr0DctLYrAxsYhQbnS) AIRSecureBrowser/3.0";
//    String userAgent = "Mozilla/5.0 (X11; CrOS x86_64 2913.331.0) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.127.111 Safari/537.11 AIRSecureBrowser/1.1";
    String userAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:10.0.1) Gecko/20100101 Firefox/10.0.1";
    
    UserAgentStringParser uaParser = UADetectorServiceFactory.getResourceModuleParser();
    ReadableUserAgent agent = uaParser.parse(userAgent);
    System.out.println (agent.getOperatingSystem ().getName ());
    System.out.println (agent.getOperatingSystem ().getVersionNumber ().toVersionString ());
    System.out.println (agent.getVersionNumber ().toVersionString ());
    
    BrowserParser bp = new BrowserParser (userAgent);
    System.out.println ("Browser Name   : "+bp.getName ());
    System.out.println ("OS Full Name   : "+bp.getOSFullName ());
    System.out.println ("OS Version     : "+bp.getOSVersion ());
    System.out.println ("BrowserVersion : "+bp.getVersion ());
    System.out.println ("OS Name        : "+bp.getOsName ());
  }*/
}
