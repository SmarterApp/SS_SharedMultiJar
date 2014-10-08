/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Namespace;

public class XmlNamespaceManager
{
  List<Namespace> nsList = new ArrayList<Namespace> ();

  public void addNamespace (String prefix, String url) {
    Namespace newNs = Namespace.getNamespace (prefix, url);
    for (int counter1 = 0; counter1 < nsList.size (); ++counter1) {
      Namespace existing = nsList.get (counter1);
      if (StringUtils.equals (existing.getPrefix (), newNs.getPrefix ())) {
        nsList.remove (counter1);
        nsList.add (counter1, newNs);
        return;
      }
    }
    
    nsList.add (newNs);
  }

  public List<Namespace> getNamespaceList () {
    return nsList;
  }
}
