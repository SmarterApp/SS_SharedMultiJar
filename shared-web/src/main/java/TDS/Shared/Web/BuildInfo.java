/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web;

/**
 * @author mpatel
 *
 */
public class BuildInfo
{
  private String appVersion;
  private String jenkinsBuildNumber;
  private String jenkinsBuildDate;
  private String jenkinsBuildUrl;
  private String gitRevision;
  private String manifestVersion;
  
  public String getAppVersion () {
    return appVersion;
  }
  public String getJenkinsBuildNumber () {
    return jenkinsBuildNumber;
  }
  public String getJenkinsBuildDate () {
    return jenkinsBuildDate;
  }
  public String getJenkinsBuildUrl () {
    return jenkinsBuildUrl;
  }
  public String getGitRevision () {
    return gitRevision;
  }
  public String getManifestVersion () {
    return manifestVersion;
  }
  public void setAppVersion (String appVersion) {
    this.appVersion = appVersion;
  }
  public void setJenkinsBuildNumber (String jenkinsBuildNumber) {
    this.jenkinsBuildNumber = jenkinsBuildNumber;
  }
  public void setJenkinsBuildDate (String jenkinsBuildDate) {
    this.jenkinsBuildDate = jenkinsBuildDate;
  }
  public void setJenkinsBuildUrl (String jenkinsBuildUrl) {
    this.jenkinsBuildUrl = jenkinsBuildUrl;
  }
  public void setGitRevision (String gitRevision) {
    this.gitRevision = gitRevision;
  }
  public void setManifestVersion (String manifestVersion) {
    this.manifestVersion = manifestVersion;
  }
  @Override
  public String toString () {
    return "AppVersionInfo [appVersion=" + appVersion + ", jenkinsBuildNumber=" + jenkinsBuildNumber + ", jenkinsBuildDate=" + jenkinsBuildDate + ", jenkinsBuildUrl=" + jenkinsBuildUrl
        + ", gitRevision=" + gitRevision + ", manifestVersion=" + manifestVersion + "]";
  }
  
}
