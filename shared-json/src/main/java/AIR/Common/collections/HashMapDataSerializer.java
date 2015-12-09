/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.collections;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class HashMapDataSerializer extends JsonSerializer<HashMap<String, ?>>
{
  public HashMapDataSerializer () {
  }

  @Override
  public void serialize (HashMap<String, ?> empObj, JsonGenerator jgen,
      SerializerProvider serializerProvider) throws IOException,
      JsonProcessingException {
    try {
      jgen.writeStartArray ();
      for (Map.Entry<String, ?> entry : empObj.entrySet ()) {

        jgen.writeStartObject ();

        jgen.writeStringField ("Key", entry.getKey ().toString ());

        jgen.writeObjectField ("Value", entry.getValue ());

        jgen.writeEndObject ();

      }
      jgen.writeEndArray ();
    } catch (Exception exp) {
      System.err.println (exp.getMessage ());
      exp.printStackTrace ();
    }
  }
}
