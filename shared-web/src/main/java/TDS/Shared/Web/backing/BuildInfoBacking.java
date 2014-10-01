/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web.backing;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import TDS.Shared.Web.BuildInfo;
import TDS.Shared.Web.client.GenericRestAPIClient;

/**
 * @author mpatel
 *
 */
public class BuildInfoBacking
{
  
  private BuildInfo _buildInfo;
  private static final Logger _logger = LoggerFactory.getLogger (BuildInfoBacking.class);
  private boolean appVersionFound = false;
  private boolean jenkinsBuildNumberFound = false;
  
  @PostConstruct
  public void init() {
    try {
       HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance ().getExternalContext ().getRequest ();
       if(request == null || request.getRequestURL ()==null) {
         throw new IllegalArgumentException ("Reuqested URL not found for getting build info (Version)...");
       }
       String restAPIVersionURL = request.getRequestURL ().append ("rest/version").toString ();
       _logger.debug ("restAPIVersionURL:::"+restAPIVersionURL); 
       GenericRestAPIClient restAPIClient = new GenericRestAPIClient (restAPIVersionURL);
      _buildInfo = restAPIClient.getForObject (BuildInfo.class);
      _logger.debug (_buildInfo.toString ());
      if(_buildInfo!=null) {
        if(!StringUtils.isEmpty (_buildInfo.getAppVersion ())) {
          appVersionFound = true;
        }
        if(!StringUtils.isEmpty (_buildInfo.getJenkinsBuildNumber ())) {
          jenkinsBuildNumberFound = true;
        }
      }
    } 
    catch (Exception e) {
      _logger.error (e.toString (),e);
    }
  }
  
  public BuildInfo getBuildInfo () {
    return _buildInfo;
  }

  public void setBuildInfo (BuildInfo buildInfo) {
    this._buildInfo = buildInfo;
  }

  public boolean isAppVersionFound () {
    return appVersionFound;
  }

  public boolean isJenkinsBuildNumberFound () {
    return jenkinsBuildNumberFound;
  }
  
  
}
