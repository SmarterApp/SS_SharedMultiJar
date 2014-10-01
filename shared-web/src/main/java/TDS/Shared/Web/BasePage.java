/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Utilities.SpringApplicationContext;
import AIR.Common.Utilities.TDSStringUtils;
import AIR.Common.Web.FacesContextHelper;
import AIR.Common.Web.Session.HttpContext;
import AIR.Common.Web.taglib.ClientScript;
import AIR.Common.Web.taglib.ClientScriptContainerBean;
import TDS.Shared.Configuration.TDSSettings;

public class BasePage
{
  private HttpContext  httpContext  = HttpContext.getCurrentContext ();
  private TDSSettings  tdsSettings  = null;
  private ClientScript clientScript = null;

  public BasePage () {
    tdsSettings = SpringApplicationContext.getBean ("tdsSettings", TDSSettings.class);
    clientScript = getBean ("clientScriptBackingBean", ClientScriptContainerBean.class).getClientScript ();
  }

  public TDSSettings getTdsSettings () {
    return tdsSettings;
  }

  public HttpContext getCurrentContext () {
    return httpContext;
  }

  public String getLoginClientName () {
    String clientName = tdsSettings.getClientNameFromQueryString ();
    if (StringUtils.isEmpty (clientName))
      clientName = tdsSettings.getClientNameFromConfig ();
    return clientName;
  }

  public String buildClientMessage (String message) {
    return TDSStringUtils.format ("<span i18n-content=\"{0}\" class=\"messageBox\"></span>", message);
  }

  public String getClientStylePath (String filePath) {
    return filePath;// string.Format("Projects/{0}/{1}", ClientPath, filePath);
  }

  protected HttpServletRequest getRequest () {
    return httpContext.getRequest ();
  }

  protected HttpServletResponse getResponse () {
    return httpContext.getResponse ();
  }

  // TODO Shiva: Do we need to use FacesContextHelper here or can we do a
  // direct injection.
  protected <T> T getBean (String beanName, final Class<T> clazz) {
    return FacesContextHelper.getBean (beanName, clazz);
  }

  /*
   * To use this successfully you need to comlete the following 4 steps:
   */
  /*
   * 1) In your faces-config.xml file (or wherever you define faces beans add
   * the following bean definition:
   * 
   * <managed-bean>
   * <managed-bean-name>clientScriptBackingBean</managed-bean-name>
   * <managed-bean
   * -class>AIR.Common.Web.taglib.ClientScriptContainerBean</managed-bean-class>
   * <managed-bean-scope>request</managed-bean-scope> </managed-bean>
   */
  /*
   * 2) You need to define the ClientScript is now available as a tag in your
   * taglib: <tag> <tag-name>ClientScript</tag-name> <component>
   * <component-type>ClientScript</component-type> </component> </tag>
   */
  /*
   * 3) Make sure the following namespace is imported into your XHTML file:
   * xmlns:tds="http://airast.org/jsfcustom"
   */
  /*
   * 4) Add the following tag just before the </h:body> end tag.
   * <tds:ClientScript binding="#{clientScriptBackingBean.clientScript}" />
   */
  protected ClientScript getClientScript () {
    return clientScript;
  }
}
