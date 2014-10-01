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
import java.util.List;
import java.util.ArrayList;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent (value = "ClientScript")
public class ClientScript extends UIComponentBase
{

  private List<String> _jsCode = new ArrayList<String> ();

  public List<String> getJsCode () {
    return _jsCode;
  }

  public void setJsCode (List<String> _jsCode) {
    this._jsCode = _jsCode;
  }

  public void addToJsCode (String js) {
    this._jsCode.add (js);
  }

  public void registerClientScriptBlock (String postAction, String scripts, boolean bool) {
    if (bool)
      _jsCode.add (scripts);
  }

  @Override
  public void encodeAll (FacesContext context) throws IOException {

    ResponseWriter writer = context.getResponseWriter ();
    writer.write ("\r\n");
    writer.write ("<script text=\"text/javascript\">");
    for (String jsline : _jsCode) {
      writer.write (jsline + "\r\n");
    }

    writer.write ("</script>");
  }

  @Override
  public String getFamily () {
    return "ClientScript";
  }
}
