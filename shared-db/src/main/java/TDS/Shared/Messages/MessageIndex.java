/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageIndex
{

  private final int _langIdx;
  private final int _subjectIdx;
  private final int _gradeIdx;

  @JsonProperty ("LanguageIndex")
  public int getLanguageIndex () {
    return _langIdx;
  }

  @JsonProperty ("SubjectIndex")
  public int getSubjectIndex () {
    return _subjectIdx;
  }

  @JsonProperty ("GradeIndex")
  public int getGradeIndex () {
    return _gradeIdx;

  }

  public MessageIndex (int langIdx, int subjectIdx, int gradeIdx) {
    _langIdx = langIdx;
    _subjectIdx = subjectIdx;
    _gradeIdx = gradeIdx;
  }
}
