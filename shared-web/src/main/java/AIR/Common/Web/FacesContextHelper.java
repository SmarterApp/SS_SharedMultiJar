/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

public class FacesContextHelper
{
  @SuppressWarnings ("unchecked")
  public static <T> T getBean (String beanName, final Class<T> clazz) {
    FacesContext currentContext = FacesContext.getCurrentInstance ();
    ELContext elContext = currentContext.getELContext ();
    return (T) currentContext.getApplication ().getELResolver ().getValue (elContext, null, beanName);
  }

  @SuppressWarnings ("unchecked")
  public static <T> T getBean (FacesContext context, String beanName, final Class<T> clazz) {
    ELContext elContext = context.getELContext ();
    return (T) context.getApplication ().getELResolver ().getValue (elContext, null, beanName);
  }
}
