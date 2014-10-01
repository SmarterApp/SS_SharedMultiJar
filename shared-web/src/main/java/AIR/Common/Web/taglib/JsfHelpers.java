/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
/**
 * 
 */
package AIR.Common.Web.taglib;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Shiva BEHERA [sbehera@air.org]
 * 
 */
/*
 * we arrived at this solution from multiple different pathways. we looked into
 * FacesServlet and how it renders page. also there were multiple queries
 * floating around on the internet regarding this problem. this particular
 * snippet has been extracted from the following link:
 * http://stackoverflow.com/questions
 * /17411762/insert-composite-component-programatically-in-jsf-via-custom-tag
 * 
 * the significant different between our solution and this one was that we were
 * not doing parent.pushComponentToEL and parent.popComponentFromEL.
 */
// TODO Shiva: it is still not clear what are the implications of
// popComponentFromELs and pushComponentToEL

// TODO Shiva: document what is going on here and how to use this. The use of
// K,V in includeCompositeComponent is very confusing.
public class JsfHelpers
{
  private static final String JAVAX_FACES_GROUP = "javax.faces.Group";

  public static void setValueExpressions(UIComponent component, Map<String, String> valueExpressions, FacesContext context)
  {
    if (valueExpressions == null || valueExpressions.size () == 0)
      return;
    if (context == null)
    {
      context = FacesContext.getCurrentInstance ();
    }
    
    Application application = context.getApplication ();
    // http://stackoverflow.com/a/16058002/1961102
    if (valueExpressions != null && !valueExpressions.isEmpty ()) {
      ExpressionFactory factory = application.getExpressionFactory ();
      ELContext ctx = context.getELContext ();
      for (Map.Entry<String, String> entry : valueExpressions.entrySet ()) {
        ValueExpression expr = factory.createValueExpression (ctx, entry.getValue (), Object.class);
        component.setValueExpression (entry.getKey (), expr);
      }
    }
  }
  
  /**
   * Source:
   * http://stackoverflow.com/questions/15828540/programmatically-create-
   * and-add-composite-component-in-backing-bean
   * 
   * @param parent
   * @param libraryName
   * @param resourceName
   * @param id
   */
  @SuppressWarnings ("unchecked")
  public static <K, V> V includeCompositeComponent (UIComponent parent, String libraryName, String resourceName, String id, Map<String, String> valueExpressions, IUpdater<K> updater)
      throws IncompatibleTypeException, JsfComponentIncludeException {
    // Prepare.
    FacesContext context = FacesContext.getCurrentInstance ();
    Application application = context.getApplication ();
    FaceletContext faceletContext = (FaceletContext) context.getAttributes ().get (FaceletContext.FACELET_CONTEXT_KEY);

    // This basically creates <ui:component> based on <composite:interface>.
    Resource resource;
    if (application.getResourceHandler ().libraryExists (libraryName)) {
      resource = application.getResourceHandler ().createResource (resourceName, libraryName);
    } else {
      resource = application.getResourceHandler ().createResource (resourceName);
    }
    UIComponent composite = application.createComponent (context, resource);
    if (StringUtils.isEmpty (id)) {
      // generate a unique id. remember ids cannot begin with a number - the
      // first character has to be a letter.
      id = "id_" + UUID.randomUUID ().toString ();
    }
    composite.setId (id); // Mandatory for the case composite is part of UIForm!
                          // Otherwise JSF can't find inputs.
    // copy values.
    if (updater != null) {
      try {
        updater.update ((K) composite);
      } catch (ClassCastException exp) {
        throw new IncompatibleTypeException (exp);
      }
    }

    setValueExpressions(composite, valueExpressions, context);

    // This basically creates <composite:implementation>.
    // Shiva: Below was what was in the original source. I have replaced it with
    // what we are comfortable with.
    /*
     * UIComponent implementation = application.createComponent (context,
     * UIPanel.COMPONENT_TYPE, JAVAX_FACES_GROUP);
     */
    UIComponent implementation = application.createComponent (HtmlPanelGroup.COMPONENT_TYPE);
    implementation.setRendererType ("javax.faces.Group");

    composite.getFacets ().put (UIPanel.COMPOSITE_FACET_NAME, implementation);

    // Now include the composite component file in the given parent.
    parent.getChildren ().add (composite);
    parent.pushComponentToEL (context, composite); // This makes #{cc}
                                                   // available.
    try {
      faceletContext.includeFacelet (implementation, resource.getURL ());
    } catch (IOException exp) {
      throw new JsfComponentIncludeException (exp);
    }
    parent.popComponentFromEL (context);

    try {
      return (V) composite;
    } catch (ClassCastException exp) {
      throw new IncompatibleTypeException (exp);
    }
  }
}
