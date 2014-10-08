/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.springframework.stereotype.Component;

@FacesComponent (value = "PlaceHolderTDS")
public class PlaceHolder extends UINamingContainer implements IRebindableComponent
{
  private String _repeaterElementName = null;

  @Override
  public String getFamily () {
    return "javax.faces.NamingContainer";
  }

  public void addComponent (UIComponent componentToAdd) {
    getChildren ().add (componentToAdd);
  }

  @Override
  public void encodeAll (FacesContext context) throws IOException {
    encodeChildren (context);
  }

  public String getRepeaterElementName () {
    return _repeaterElementName;
  }

  public void setRepeaterElementName (String value) {
    _repeaterElementName = value;
  }
}
