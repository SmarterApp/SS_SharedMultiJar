/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Messages;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageTranslation
{
  private final Message _message;
  private final String  _text;
  private final String  _language;
  private final String  _grade;
  private final String  _subject;

  @JsonProperty ("Message")
  public Message getMessage () {
    return _message;
  }

  @JsonProperty ("Text")
  public String getText () {
    return _text;
  }

  @JsonProperty ("Language")
  public String getLanguage () {
    return _language;
  }

  @JsonProperty ("Grade")
  public String getGrade () {
    return _grade;
  }

  @JsonProperty ("Subject")
  public String getSubject () {
    return _subject;
  }

  public MessageTranslation (Message message, String language, String subject, String grade, String text) {
    _message = message;
    _text = text;
    _language = language;
    _grade = grade;
    _subject = subject;
  }

  // / <summary>
  // / Get the client message formatted with the message code.
  // / </summary>
  public String getFormatted () {
    if (StringUtils.isEmpty (getText ()))

    {
      return String.format ("%s [Message Code: %s]", _message.getMessageKey (), _message.getMessageId ());
    }

    return String.format ("%s [Message Code: %s]", getText (), _message.getMessageId ());
  }

  @Override
  public String toString () {
    return _text;
  }

}
