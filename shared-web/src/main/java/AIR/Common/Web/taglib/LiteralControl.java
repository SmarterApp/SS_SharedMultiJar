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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.render.Renderer;

@FacesComponent ("Literal")
public class LiteralControl extends UIComponentBase implements IRebindableComponent
{
  private String _repeaterElementName;
  private String _value;

  public LiteralControl (String value) {
    setText (value);
  }

  public LiteralControl () {
  }

  public String getText () {
    return _value;
  }

  public void setText (String value) {
    _value = value;
  }

  @Override
  public String getFamily () {
    return HtmlOutputText.COMPONENT_FAMILY;
  }

  @Override
  public void encodeAll (FacesContext context) throws IOException {
    context.getResponseWriter ().write (getText ());
  }
  
  public String getRepeaterElementName () {
    return _repeaterElementName;
  }

  public void setRepeaterElementName (String value) {
    _repeaterElementName = value + Math.random ();
  }

}
