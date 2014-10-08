/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Json;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper
{

  public static <T> String serialize (T obj) throws JsonGenerationException, JsonMappingException, IOException
  {
    if (obj == null)
      return null;

    ObjectMapper mapper = new ObjectMapper ();

    String json = null;

    StringWriter sw = new StringWriter ();
    mapper.writeValue (sw, obj);

    json = sw.toString ();
    sw.close ();

    return json;
  }

  public static <T> T deserialize (String json, Class<T> class1) throws JsonParseException, JsonMappingException, IOException
  {
    if (StringUtils.isEmpty (json))
      return null;

    ObjectMapper mapper = new ObjectMapper ();

    return mapper.readValue (json, class1);

  }

}
