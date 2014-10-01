/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.Shared.Messages;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;

// Example:
// {"c_l":"ESN",
// "c_a":[{"c":"Global",
// "m_a":[{"id":10884,
// "m":"Global.Path.Help",
// "t_a":[{"t":"..\/Projects\/Oregon\/Help\/help.html",
// "l":"ENU"}]
// },
// {"id":11690,
// "m":"Messages.Label.XHRError",
// "t_a":[{"t":"Select Yes to try again or No to log out.",
// "l":"ENU"}]
// }
// ]
// }
// ]
// }

public class MessageJson
{

  private final MessageSystem _messageSystem;
  private final String        _language;
  private final String        _subject;
  private final String        _grade;

  public MessageJson (MessageSystem messageSystem) {
    _messageSystem = messageSystem;
    _language = null;
    _subject = null;
    _grade = null;
  }

  public MessageJson (MessageSystem messageSystem, String language) {
    _messageSystem = messageSystem;
    _language = language;
    _subject = null;
    _grade = null;
  }

  public MessageJson (MessageSystem messageSystem, String language, String subject, String grade) {
    _messageSystem = messageSystem;
    _language = language;
    _subject = subject;
    _grade = grade;
  }

  // / <summary>
  // / Write out the context group.
  // / </summary>
  private void writeContextElement (MessageContext messageContext, JsonGenerator jsonWriter) throws JsonGenerationException, IOException {
    jsonWriter.writeStartObject (); // {
    jsonWriter.writeStringField ("c", messageContext.getName ()); // "c": "name"
    jsonWriter.writeFieldName ("m_a"); // "m_a":
    jsonWriter.writeStartArray (); // [
    List<Message> messages = messageContext.getMessages ();
    for (Message message : messages) {
      writeMessageElement (message, jsonWriter);
    }
    jsonWriter.writeEndArray (); // ]
    jsonWriter.writeEndObject (); // }
  }

  // / <summary>
  // / Write out the message id and key.
  // / </summary>
  private void writeMessageElement (Message message, JsonGenerator jsonWriter) throws JsonGenerationException, IOException {
    // {"id":10884,
    // "m":"Global.Path.Help",
    // "t_a":[{"t":"..\/Projects\/Oregon\/Help\/help.html",
    // "l":"ENU"}]
    // }
    jsonWriter.writeStartObject (); // {
    jsonWriter.writeNumberField ("id", message.getMessageId ());
    jsonWriter.writeStringField ("m", message.getMessageKey ());

    jsonWriter.writeFieldName ("t_a"); // "t_a":
    jsonWriter.writeStartArray (); // [

    List<MessageTranslation> translations;

    // if there is no language specified they get all translations
    if (StringUtils.isEmpty (_language)) {
      translations = message.getTranslations ();
    } else {
      // if a language is provided then find the best language and only
      // include that
      MessageTranslation defaultTranslation = message.getTranslation (_language, _subject, _grade);
      translations = new ArrayList<MessageTranslation> ();
      translations.add (defaultTranslation);
    }

    for (MessageTranslation translation : translations) {
      writeTranslationElement (translation, jsonWriter);
    }

    jsonWriter.writeEndArray (); // ]
    jsonWriter.writeEndObject (); // }
  }

  // / <summary>
  // / Write out the translations for a key.
  // / </summary>
  private void writeTranslationElement (MessageTranslation translation, JsonGenerator jsonWriter) throws IOException {
    // {"t":"..\/Projects\/Oregon\/Help\/help.html",
    // "l":"ENU"}
    jsonWriter.writeStartObject (); // {
    jsonWriter.writeStringField ("t", translation.getText ());
    jsonWriter.writeStringField ("l", translation.getLanguage ());

    if (translation.getSubject () != null)
      jsonWriter.writeStringField ("s", translation.getSubject ());
    if (translation.getGrade () != null)
      jsonWriter.writeStringField ("g", translation.getGrade ());
    jsonWriter.writeEndObject (); // }
  }

  public String create (ContextType contextType, List<String> contexts) throws ReturnStatusException {
    MessageContextType messageContextType = null;
    StringWriter sw = new StringWriter ();
    JsonFactory jsonFactory = new JsonFactory ();
    JsonGenerator jsonWriter;

    try {
      if (_messageSystem != null) {
        messageContextType = _messageSystem.getMessageContextType (contextType);
      }

      if (messageContextType == null)
        return "{}";

      jsonWriter = jsonFactory.createGenerator (sw);
      jsonWriter.writeStartObject ();
      jsonWriter.writeStringField ("c_l", _language); // "c_l": _language
      jsonWriter.writeFieldName ("c_a"); // "c_a" :
      jsonWriter.writeStartArray (); // [

      for (String context : contexts) {
        MessageContext messageContext = messageContextType.getContext (context);
        writeContextElement (messageContext, jsonWriter);
      }

      jsonWriter.writeEndArray (); // ]
      jsonWriter.writeEndObject (); // }

      jsonWriter.close ();
      sw.close ();
    } catch (IOException e) {
      ReturnStatus rs = new ReturnStatus ("failed", "Serialization failed: " + e.getMessage ());
      throw new ReturnStatusException (rs);
    }

    return sw.getBuffer ().toString ();
  }

  // / <summary>
  // / Create a JSON String of all the messages.
  // / </summary>
  public String create () throws ReturnStatusException {
    StringWriter sw = new StringWriter ();
    JsonFactory jsonFactory = new JsonFactory ();
    JsonGenerator jsonWriter;

    try {
      if (_messageSystem == null)
        return "{}";

      jsonWriter = jsonFactory.createGenerator (sw);
      jsonWriter.writeStartObject ();

      jsonWriter.writeStringField ("c_l", _language); // "c_l": _language
      jsonWriter.writeFieldName ("c_a"); // "c_a" :
      jsonWriter.writeStartArray (); // [

      // get all the message contexts across all the context types
      List<MessageContext> messageContexts = new ArrayList<MessageContext> ();
      for (MessageContextType contextType : _messageSystem.getContextTypes ()) {
        messageContexts.addAll (contextType.getContexts ());
      }

      for (MessageContext messageContext : messageContexts) {
        writeContextElement (messageContext, jsonWriter);
      }

      jsonWriter.writeEndArray (); // ]
      jsonWriter.writeEndObject (); // }

      jsonWriter.close ();
      sw.close ();
    } catch (IOException e) {
      ReturnStatus rs = new ReturnStatus ("failed", "Serialization failed: " + e.getMessage ());
      throw new ReturnStatusException (rs);
    }

    return sw.getBuffer ().toString ();
  }
}
