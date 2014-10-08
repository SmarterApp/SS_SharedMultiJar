/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.JsonSerializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author efurman
 *
 */
/*
 * In Jackson, serializaing a boolean attribute leads to something like the following by 
 * default:
 * 
 * {..., booleanObject: booleanValue, ... }
 * 
 * booleanValue takes either the values "true" or "false", without the quotes.
 * So if booleanValue takes "true" this would have looked like:
 * 
 * {..., booleanObject: true, ... }
 * 
 * However, we ran into an issue with the Simulator where the boolean values needed
 * to be serialized as a String as follows:
 * 
 * {..., "booleanObject": "True", ...}
 * 
 * So, not only are we serializaing it as a String but also it starts with capital letters
 * e.g. "True" instead of "true".
 * 
 * This class exists to solve this problem.
 * 
 * To use this class on the getter add the following annotation
 *   @JsonSerialize(using=BoolToString.class)
 *   
 * Remember you may as add many attributes as you like e.g. other Jackson annotations.  
 */
public class BoolToString extends JsonSerializer<Boolean>
{
  @Override
  public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
          JsonProcessingException {

    if (value)
      jgen.writeString ("True");
    else
      jgen.writeString ("False");     
 
  }
}
