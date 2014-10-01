/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class PatternUrlRewriter implements IUrlRewriter
{

  private Pattern _pattern     = null;
  private String  _replacement = null;

  public String getPattern () {
    return _pattern.pattern ();
  }

  public void setPattern (String value) {
    if (!StringUtils.isBlank (value)) {
      _pattern = Pattern.compile (value);
    }
    else {
      _pattern = null;
    }
  }

  public String getReplacement () {
    return _replacement;
  }

  public void setReplacement (String value) {
    _replacement = value;
  }

  @Override
  public URL rewrite (Object url) throws MalformedURLException {
    String string_url = url.toString ();
    if (_pattern == null) {
      return new URL (string_url);
    }
    Matcher m = _pattern.matcher (string_url);
    String new_url = m.replaceFirst (_replacement);
    return new URL (new_url);
  }
}
