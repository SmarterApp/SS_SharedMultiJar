/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Browser;

import AIR.Common.Web.BrowserOS;
import AIR.Common.Web.BrowserParser;

/**
 * @author mpatel
 *
 */
public class BrowserInfo
{
  public BrowserOS OSName ;
  public double OSVersion ;
  public String architecture ;
  public String name ;
  public double version ;

  
  public BrowserOS getOSName () {
    return OSName;
  }

  public void setOSName (BrowserOS oSName) {
    OSName = oSName;
  }

  public double getOSVersion () {
    return OSVersion;
  }

  public void setOSVersion (double oSVersion) {
    OSVersion = oSVersion;
  }

  public String getArchitecture () {
    return architecture;
  }

  public void setArchitecture (String architecture) {
    this.architecture = architecture;
  }

  public String getName () {
    return name;
  }

  public void setName (String name) {
    this.name = name;
  }

  public double getVersion () {
    return version;
  }

  public void setVersion (double version) {
    this.version = version;
  }

  public BrowserInfo()
  {
  }

  public BrowserInfo(BrowserOS osName, double osVersion, String architecture, String name, double version)
  {
      this.OSName = osName;
      this.OSVersion = osVersion;
      this.architecture = architecture;
      this.name = name;
      this.version = version;
  }

  /// <summary>
  /// Get the current http browser info
  /// </summary>
  public static BrowserInfo GetHttpCurrent()
  {
      BrowserParser browser = new BrowserParser();
      return new BrowserInfo(browser.getOsName (),browser.getOSVersion () , browser.getHardwareArchitecture (), browser.getName (), browser.getVersion ());
  }
}
