/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Messages;

import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

public class MessageIndexer
{
  private final TreeMap<String, Integer> _languageDictionary = new TreeMap<String, Integer> (); // change
                                                                                                // to
                                                                                                // array
  private final TreeMap<String, Integer> _subjectDictionary  = new TreeMap<String, Integer> ();
  private final TreeMap<String, Integer> _gradeDictionary    = new TreeMap<String, Integer> ();

  public MessageIndexer () {
    init ();// clear old data
  }

  private void init () {
    // clear old data
    _languageDictionary.clear ();
    _subjectDictionary.clear ();
    _gradeDictionary.clear ();
    _languageDictionary.put ("ENU", 0); // default will be at 0 index
    _subjectDictionary.put ("DefaultSubject", 0); // default will be at 0 index
    _gradeDictionary.put ("DefaultGrade", 0); // default will be at 0 index
  }

  public int addLanguage (String language) {
    int index;
    if (StringUtils.isEmpty (language))
      return 0;

    // check if language
    if (_languageDictionary.containsKey (language)) {
      index = _languageDictionary.get (language);
      return index;
    }

    _languageDictionary.put (language, _languageDictionary.size ());
    return _languageDictionary.size () - 1;
  }

  public int addSubject (String subject) {
    int index;
    if (StringUtils.isEmpty (subject))
      return 0;

    // check if subject
    if (_subjectDictionary.containsKey (subject)) {
      index = _subjectDictionary.get (subject);
      return index;
    }

    _subjectDictionary.put (subject, _subjectDictionary.size ());
    return _subjectDictionary.size () - 1;
  }

  public int addGrade (String grade) {
    int index;
    if (StringUtils.isEmpty (grade))
      return 0;

    // check if grade
    if (_gradeDictionary.containsKey (grade)) {
      index = _gradeDictionary.get (grade);
      return index;
    }

    _gradeDictionary.put (grade, _gradeDictionary.size ());
    return _gradeDictionary.size () - 1;
  }

  public int[][][] createIndexer3D () {
    int i1 = (_languageDictionary.size () > 0) ? _languageDictionary.size () : 1;
    int i2 = (_subjectDictionary.size () > 0) ? _subjectDictionary.size () : 1;
    int i3 = (_gradeDictionary.size () > 0) ? _gradeDictionary.size () : 1;

    return new int[i1][i2][i3];
  }

  private int getLanguageIndex (String language) {
    if (StringUtils.isEmpty (language) || _languageDictionary.size () == 0)
      return 0;

    int index = 0;
    if (_languageDictionary.containsKey (language)) {
      index = _languageDictionary.get (language);
    }
    return index;
  }

  private int getSubjectIndex (String subject) {
    if (StringUtils.isEmpty (subject) || _subjectDictionary.size () < 1)
      return 0;

    int index = 0;
    if (_subjectDictionary.containsKey (subject)) {
      index = _subjectDictionary.get (subject);
    }
    return index;
  }

  private int getGradeIndex (String grade) {
    if (StringUtils.isEmpty (grade) || _gradeDictionary.size () == 1)
      return 0;

    int index = 0;
    if (_gradeDictionary.containsKey (grade)) {
      index = _gradeDictionary.get (grade);
    }
    return index;
  }

  public MessageIndex Get (String language, String subject, String grade) {
    return new MessageIndex (getLanguageIndex (language), getSubjectIndex (subject), getGradeIndex (grade));
  }

}
