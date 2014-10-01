/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Messages;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message
{

  private final List<MessageTranslation> _translationList = new ArrayList<MessageTranslation> ();
  private final MessageContext           _context;
  private final int                      _messageId;
  private final String                   _messageKey;
  private int[][][]                      _translation3D;

  @JsonProperty ("Context")
  public MessageContext getContext () {
    return _context;
  }

  @JsonProperty ("MessageID")
  public int getMessageId () {
    return _messageId;
  }

  @JsonProperty ("MessageKey")
  public String getMessageKey () {
    return _messageKey;
  }

  public Message (MessageContext context, int messageId, String messageKey) {
    _context = context;
    _messageId = messageId;
    _messageKey = messageKey;
  }

  public void initTranslationIndex (int[][][] translation3D) {
    _translation3D = translation3D;
  }

  public void setTranslationIndex (MessageIndex messageIndex, int translationIndex) {
    _translation3D[messageIndex.getLanguageIndex ()][messageIndex.getSubjectIndex ()][messageIndex.getGradeIndex ()] = translationIndex;
  }

  public int[][][] getTranslationIndex () {
    return _translation3D;
  }

  public MessageTranslation addTranslation (String language, String subject, String grade, String text) {
    // set wildcards as NULL
    String wildcard = "--ANY--";
    if (subject.equals (wildcard))
      subject = null;
    if (grade.equals (wildcard))
      grade = null;

    MessageTranslation translation = new MessageTranslation (this, language, subject, grade, text);
    _translationList.add (translation);
    return translation;
  }

  public List<MessageTranslation> getTranslations () {
    return _translationList;
  }

  // message translation selection
  public MessageTranslation getTranslation (MessageIndex messageIndex) {
    int index = 0;

    if (_translation3D != null) {
      index = _translation3D[messageIndex.getLanguageIndex ()][messageIndex.getSubjectIndex ()][messageIndex.getGradeIndex ()];
    }
    return _translationList.get (index);
  }

  public MessageTranslation getTranslation (String language, String subject, String grade) {
    MessageIndexer messageIndexer = _context.getContextType ().getMessageSystem ().getMessageIndexer ();
    MessageIndex messageIndex = messageIndexer.Get (language, subject, grade);
    return getTranslation (messageIndex);
  }

  @Override
  public String toString () {
    return _messageKey;
  }

}
