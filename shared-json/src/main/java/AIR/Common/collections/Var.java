/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.collections;

import java.util.HashMap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author temp_rreddy
 * 
 */

// replacement for C# var.
@JsonSerialize (using = HashMapDataSerializer.class)
public class Var extends HashMap<String, Object>
{
  private static final long serialVersionUID = 1L;

  public Var assign (String key, Object object)
  {
    this.put (key, object);
    return this;
  }
}
