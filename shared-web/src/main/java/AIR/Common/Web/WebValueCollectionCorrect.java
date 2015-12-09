/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AIR.Common.Helpers.CaseInsensitiveMap;
import AIR.Common.Json.JsonHelper;
import AIR.Common.Utilities.UrlEncoderDecoderUtils;

// Shiva: use this instead of WebValueCollection
public class WebValueCollectionCorrect extends CaseInsensitiveMap<Object>
{

  private static final long   serialVersionUID = -8189580537291609856L;
  private static final Logger _logger          = LoggerFactory.getLogger (WebValueCollectionCorrect.class);

  @Override
  public String toString () {
    return this.toString (true, null);
  }

  public String toString (boolean urlencoded) {
    return this.toString (urlencoded, null);
  }

  public String toString (boolean urlencoded, Map<String, Object> excludeKeys) {
    int count = this.size ();

    if (count == 0) {
      return "";
    }

    Map<String, Object> newMap = new HashMap<String, Object> ();

    for (Map.Entry<String, Object> entry : this.entrySet ()) {
      String key = entry.getKey ();
      Object value = entry.getValue ();

      if (excludeKeys != null && excludeKeys.containsKey (key))
        continue;

      newMap.put (key, value);
    }

    try {
      String json = JsonHelper.serialize (newMap);
      if (urlencoded)
        json = UrlEncoderDecoderUtils.encode (json);
      return json;
    } catch (IOException exp) {
      throw new RuntimeException (exp);
    }
  }

  @Override
  public Object put (String key, Object value) {
    if (key == null || StringUtils.isEmpty (key.toString ()) || value == null)
      return null;
    return super.put (key.toString (), value);
  }

  public static WebValueCollectionCorrect getInstanceFromString (String strn, boolean urlEncoded) {
    if (StringUtils.isEmpty (strn))
      return null;
    try {
      if (urlEncoded)
        strn = UrlEncoderDecoderUtils.decode (strn);
      Map<Object, Object> o = JsonHelper.deserialize (strn, Map.class);
      
      WebValueCollectionCorrect collection = new WebValueCollectionCorrect ();
      for(Map.Entry<Object, Object> entry: o.entrySet ())
      {
        collection.put (entry.getKey ().toString (), entry.getValue ());
      }
      return collection;
    } catch (Exception exp) {
      throw new RuntimeException (exp);
    }
  }

  public static void main (String[] args) {
    try {
      WebValueCollectionCorrect collection = new WebValueCollectionCorrect ();
      collection.put ("a", "A");
      collection.put ("b", Arrays.asList ("1", "2"));
      collection.put ("c", UUID.randomUUID ());
      System.err.println (collection.toString ());
      WebValueCollectionCorrect.getInstanceFromString (collection.toString (), true);
    } catch (Exception exp) {
      exp.printStackTrace ();
    }
  }
}
