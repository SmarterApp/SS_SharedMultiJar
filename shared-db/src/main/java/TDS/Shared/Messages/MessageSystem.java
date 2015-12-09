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
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageSystem
{
  private final HashMap<ContextType, MessageContextType> _messageContextTypes = new HashMap<ContextType, MessageContextType> ();
  private final MessageIndexer                           _messageIndexer      = new MessageIndexer ();

  private boolean                                        IsLoaded;

  // get; internal set;

  @JsonProperty ("IsLoaded")
  public boolean isLoaded () {
    return IsLoaded;
  }

  protected void setLoaded (boolean isLoaded) {
    IsLoaded = isLoaded;
  }

  public MessageSystem () {
  }

  @JsonProperty ("MessageIndexer")
  public MessageIndexer getMessageIndexer () {
    return _messageIndexer;
  }

  public List<MessageContextType> getContextTypes () {
    // TODO Review how Ravi handles toList() conversions
    List<MessageContextType> tmpList = new ArrayList<MessageContextType> (_messageContextTypes.values ());
    return tmpList;
    // return _messageContextTypes.values ().toList ();
  }

  public MessageContextType getMessageContextType (ContextType contextType) {
    MessageContextType messageContextType;

    messageContextType = _messageContextTypes.get (contextType);
    return messageContextType;
  }

  public MessageContextType addMessageContextType (ContextType contextType) {
    MessageContextType messageContextType = new MessageContextType (this, contextType);
    _messageContextTypes.put (contextType, messageContextType);
    return messageContextType;
  }

  public MessageTranslation getTranslation (ContextType contextType, String context, String messageKey, String language, String subject, String grade) {
    MessageContextType messageContextType;

    // if (_messageContextTypes.TryGetValue(contextType, out
    // messageContextType))
    if (_messageContextTypes.containsKey (contextType)) {
      messageContextType = _messageContextTypes.get (contextType);
      MessageContext messageContext = messageContextType.getContext (context);

      if (messageContext != null) {
        Message message = messageContext.getMessage (messageKey);

        if (message != null) {
          MessageIndex messageIndex = _messageIndexer.Get (language, subject, grade);
          return message.getTranslation (messageIndex);
        }
      }
    }

    return null;
  }

  public List<MessageTranslation> getContextTranslations (ContextType contextType, String context, String language, String subject, String grade) {
    List<MessageTranslation> translations = new ArrayList<MessageTranslation> ();

    MessageContextType messageContextType = getMessageContextType (ContextType.ClientSide);

    if (messageContextType != null) {
      MessageContext messageContext = messageContextType.getContext (context);

      if (messageContext != null) {
        List<Message> messages = messageContext.getMessages ();

        if (messages != null) {
          for (Message message : messages) {
            MessageIndex messageIndex = getMessageIndexer ().Get (language, subject, grade);
            MessageTranslation messageTranslation = message.getTranslation (messageIndex);
            translations.add (messageTranslation);
          }
        }
      }
    }

    return translations;
  }

  public void buildIndex () {
    // build main index lookup
    for (MessageContextType messageContextType : getContextTypes ()) {
      for (MessageContext messageContext : messageContextType.getContexts ()) {
        for (Message message : messageContext.getMessages ()) {
          for (MessageTranslation messageTranslation : message.getTranslations ()) {
            _messageIndexer.addLanguage (messageTranslation.getLanguage ());
            _messageIndexer.addSubject (messageTranslation.getSubject ());
            _messageIndexer.addGrade (messageTranslation.getGrade ());
          }
        }
      }
    }

    // build lookup for each message
    for (MessageContextType messageContextType : getContextTypes ()) {
      for (MessageContext messageContext : messageContextType.getContexts ()) {
        for (Message message : messageContext.getMessages ()) {
          message.initTranslationIndex (_messageIndexer.createIndexer3D ());

          int translationIndex = 0;
          for (MessageTranslation messageTranslation : message.getTranslations ()) {
            MessageIndex messageIndex = _messageIndexer.Get (messageTranslation.getLanguage (), messageTranslation.getSubject (), messageTranslation.getGrade ());
            message.setTranslationIndex (messageIndex, translationIndex);
            translationIndex++;
          }
        }
      }
    }
  }

}
