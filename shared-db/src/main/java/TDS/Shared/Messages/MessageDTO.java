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

public class MessageDTO
{

  // / <summary>
  // / This is the source of the message translation. For example you can
  // / have a hierarchy of "Student", "Oregon", "Oregon_PT".
  // / </summary>
  private String      _messageSource;

  // / <summary>
  // / The context type grouping.
  // / </summary>
  private ContextType _contextType;
  // / <summary>
  // / The context of the message.
  // / </summary>
  private String      _context;
  // / <summary>
  // / This is a message ID that is sometimes included with
  // / the message translation (if it is an error for example).
  // / </summary>
  private int         _messageId;
  // / <summary>
  // / This is the message key used for lookups.
  // / </summary>
  private String      _appKey;
  private String      _message;
  private String      _language;
  private String      _grade;
  private String      _subject;

  // / <summary>
  // / This is the translated text for the app key.
  // / </summary>

  @JsonProperty ("MessageSource")
  public String getMessageSource () {
    return _messageSource;
  }

  public void setMessageSource (String messageSource) {
    _messageSource = messageSource;
  }

  @JsonProperty ("ContextType")
  public ContextType getContextType () {
    return _contextType;
  }

  public void setContextType (ContextType contextType) {
    _contextType = contextType;
  }

  @JsonProperty ("Context")
  public String getContext () {
    return _context;
  }

  public void setContext (String context) {
    _context = context;
  }

  @JsonProperty ("MessageID")
  public int getMessageId () {
    return _messageId;
  }

  public void setMessageId (int messageId) {
    this._messageId = messageId;
  }

  @JsonProperty ("AppKey")
  public String getAppKey () {
    return _appKey;
  }

  public void setAppKey (String appKey) {
    _appKey = appKey;
  }

  @JsonProperty ("Language")
  public String getLanguage () {
    return _language;
  }

  public void setLanguage (String language) {
    _language = language;
  }

  @JsonProperty ("Grade")
  public String getGrade () {
    return _grade;
  }

  public void setGrade (String grade) {
    _grade = grade;
  }

  @JsonProperty ("Subject")
  public String getSubject () {
    return _subject;
  }

  public void setSubject (String subject) {
    _subject = subject;
  }

  @JsonProperty ("Message")
  public String getMessage () {
    return _message;
  }

  public void setMessage (String message) {
    _message = message;
  }

}
