/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.Session;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// TODO Shiva Difference between this and org.apache.catalina.LifecycleListener
// ?
// org.apache.catalina.LifecycleListener seems to be dependent on apache
// packages whereas ServletContextListener
// seems to be more generic.
/*
 * place holder in case we want to do anything on context load.
 */
public abstract class BaseServletContextListener implements ServletContextListener
{
  public void contextInitialized (ServletContextEvent sce) {
    // TODO Shiva we could have loaded configuration file here but we have done
    // it as a WebAppInitializer.
    // TODO Shiva can we save the ServletContext as part of Server.
    ServletContext context = sce.getServletContext ();
    Server.setServletContext (context);
    Server.setContextPath (context.getContextPath ());
    Server.setDocBasePath (context.getRealPath (Server.getContextPath ()));
  }

  public void contextDestroyed (ServletContextEvent sce) {
  }
}
