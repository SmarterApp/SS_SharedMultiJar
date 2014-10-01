/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web.taglib;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * @author mpatel
 *
 */
public abstract class TDSComponentBase extends UIComponentBase  implements NamingContainer
{
  
  protected enum TDS_ATTRIBUTES { i18n_content(" i18n-content");
    /**
     * 
     */
    private TDS_ATTRIBUTES (String attrName) {
      _attrName = attrName;
    }
    
    private final String _attrName;
    
    public String getAttrName ()
    {
      return _attrName;
    }
  };
  
  protected void encodeTdsAttributes(FacesContext context)
  {
    /*for(TDS_ATTRIBUTES attr : TDS_ATTRIBUTES.values ())
    {
      //TODO: Do nothing. In the future we need to add attributes to the html tag here.
      
    }*/
  }
}
