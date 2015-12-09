/*******************************************************************************
 * Educational Online Test Delivery System Copyright (c) 2014 American
 * Institutes for Research
 * 
 * Distributed under the AIR Open Source License, Version 1.0 See accompanying
 * file AIR-License-1_0.txt or at http://www.smarterapp.org/documents/
 * American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package AIR.Common.Web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Helpers._Ref;

public class PatternUrlRewriter implements IUrlRewriter
{

  private List<PatternReplacement> _patterns = new ArrayList<PatternReplacement> ();
  private PatternReplacement       _defaultPattern;

  public PatternUrlRewriter () {
    try {
      /*
       * Shiva hack!!! This is a quick fix for multiple regexes. Tab separated
       * file.
       */
      InputStream in = this.getClass ().getClassLoader ().getResourceAsStream ("PatternUrlRewriter.txt");
      try (BufferedReader bfr = new BufferedReader (new InputStreamReader (in))) {
        String line = null;
        while ((line = bfr.readLine ()) != null) {
          line = line.trim ();
          if (!StringUtils.isEmpty (line)) {
            final String[] pair = StringUtils.split (line, '\t');
            _patterns.add (new PatternReplacement ()
            {
              {
                this._pattern = Pattern.compile (pair[0].trim ());
                this._replacement = pair[1].trim ();
              }
            });
          }
        }
      }
    } catch (Exception exp) {
      System.out.println ("Error reading patterns from external file.");
    }
  }

  public String getPattern () {
    if (_defaultPattern != null)
      return _defaultPattern._pattern.pattern ();
    return null;
  }

  public void setPattern (String value) {
    createDefaultPattern ();
    if (!StringUtils.isBlank (value)) {
      _defaultPattern._pattern = Pattern.compile (value);
    } else {
      _defaultPattern = null;
    }
  }

  public String getReplacement () {
    return _defaultPattern._replacement;
  }

  public void setReplacement (String value) {
    createDefaultPattern ();
    _defaultPattern._replacement = value;
  }

  @Override
  public URL rewrite (Object url) throws MalformedURLException {
    String string_url = url.toString ();

    _Ref<URL> ref = new _Ref<URL> ();

    for (PatternReplacement pattern : _patterns) {
      if (pattern._pattern != null) {
        if (rewrite (string_url, pattern._pattern, pattern._replacement, ref))
          return ref.get ();
      }
    }

    return new URL (string_url);
  }

  private boolean rewrite (String url, Pattern p, String replacement, _Ref<URL> output) throws MalformedURLException {
    if (p == null) {
      output.set (new URL (url));
      return false;
    }

    Matcher m = p.matcher (url);
    if (m.matches ()) {
      output.set (new URL (m.replaceFirst (replacement)));
      return true;
    }

    return false;
  }

  public static void maint (String[] args) {
    try {
      PatternUrlRewriter writer = new PatternUrlRewriter ();

      String pattern = "(file://D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-179/Items/Item-(?<bankid>[^/]*)-(?<itemid>[^/]*)/([^/]*))|(https://tds2\\.airws\\.org/Test_Student_[^/]*/V[^/]*/ItemScoringRubric\\.axd\\?itembank=(?<bankidx>[^/]*)&itemid=(?<itemidx>[^/]*))";

      // String replacement =
      // "file:///C:/AIROSE Trainer Deployments/tmp/Datafiles/Item-${bankidx}-${itemidx}/Item_${itemidx}_v5_rubric.xml";
      String replacement = "file:///C:/AIROSE Trainer Deployments/tmp/Datafiles/Item-${bankid}-${itemid}/Item_${itemid}_v5.xml";

      writer.setReplacement (replacement);
      writer.setPattern (pattern);

      // System.err.println (writer.rewrite (new URL
      // ("https://tds2.airws.org/Test_Student_Sat1/V70/ItemScoringRubric.axd?itembank=195&itemid=6821")));
      //System.err.println (writer.rewrite (new URL ("file:///D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-179/Items/Item-179-22489/Item_22489_v5_rubric.xml")));
      System.err.println (writer.rewrite (new URL ("https://tds2.airws.org/Test_Student_Sat1/V67/ItemScoringRubric.axd?itembank=195&itemid=5332")));

    } catch (Exception exp) {
      exp.printStackTrace ();
    }
  }

  public static void maino (String[] args) {
    try {
      PatternUrlRewriter writer = new PatternUrlRewriter ();
      System.err.println (writer.rewrite (new URL ("file:///D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-179/Items/Item-195-6821/Item_22489_v5_rubric.xml")));
    } catch (Exception exp) {
      exp.printStackTrace ();
    }
  }
  
  public static void main (String[] args) {
    try {
      Pattern p = Pattern.compile ("file:///D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-([^/]*)/Items/Item-(?<bankid>[^/]*)-(?<itemid>[^/]*)/([^/]*)");
    
      Matcher m = p.matcher ("file:///D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-179/Items/Item-195-6821/Item_22489_v5_rubric.xml");
  
      URL url = new URL(m.replaceFirst ("file:///C:/AIROSE Trainer Deployments/tmp/Datafiles/Item-${bankid}-${itemid}/Item_${itemid}_v5_rubric.xml"));
      System.err.println(url);
    } catch (Exception exp) {
      exp.printStackTrace ();
    }
  }

  public static void mainz (String[] args) {
    try {
      PatternUrlRewriter writer = new PatternUrlRewriter ();

      String pattern = "file://D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-179/Items/([^/]*)/";
      String replacement = "file:///C:/AIROSE Trainer Deployments/tmp/Datafiles/$1/";

      writer.setReplacement (replacement);
      writer.setPattern (pattern);

      // System.err.println (writer.rewrite (new URL
      // ("https://tds2.airws.org/Test_Student_Sat1/V70/ItemScoringRubric.axd?itembank=195&itemid=6821")));
      System.err.println (writer.rewrite (new URL ("file://D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-179/Items/Item-179-22489/Item_22489_v5_rubric.xml")));

    } catch (Exception exp) {
      exp.printStackTrace ();
    }
  }

  public static void mainx (String[] arg) {
    try {
      PatternUrlRewriter pattern = new PatternUrlRewriter ();
      // pattern.setPattern
      // ("file://D:/DataFiles/BB_Files/tds2_airws_org/TDSCore_2013-2014/Bank-179/Items/([^/]*)/");

      String patternString = "https://tds2\\.airws\\.org/Test_Student_[^/]*/V[^/]*/ItemScoringRubric\\.axd\\?itembank=(?<bankid>[^/]*)&itemid=(?<itemid>[^/]*)";
      pattern.setPattern (patternString);

      pattern.setReplacement ("file://C:/AIROSE Trainer Deployments/tmp/Datafiles/Item-${bankid}-${itemid}/Item_${itemid}_v5.xml");

      String url = "https://tds2.airws.org/Test_Student_Sat1/V67/ItemScoringRubric.axd?itembank=195&itemid=5332";
      System.err.println (pattern.rewrite (new URL (url)));
    } catch (Exception exp) {
      exp.printStackTrace ();
    }
  }

  private void createDefaultPattern () {
    if (_defaultPattern == null) {
      _defaultPattern = new PatternReplacement ();
      _patterns.add (_defaultPattern);
    }
  }
}

class PatternReplacement
{
  public Pattern _pattern;
  public String  _replacement;
}
