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

import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import AIR.Common.Helpers.CaseInsensitiveMap;

public class MessageContext
{
  private final HashMap<Integer, Message>   _messagesLookupById = new HashMap<Integer, Message> ();
  private final CaseInsensitiveMap<Message> _messagesLookup     = new CaseInsensitiveMap<Message> ();
  private final MessageContextType          _contextType;
  private final String                      _name;

  @JsonProperty ("ContextType")
  public MessageContextType getContextType () {
    return _contextType;
  }

  @JsonProperty ("Name")
  public String getName () {
    return _name;
  }

  public MessageContext (MessageContextType contextType, String contextName) {
    _contextType = contextType;
    _name = contextName;
  }

  public Message addMessage (int messageId, String messageKey) {
    if (StringUtils.isEmpty (messageKey))
      return null;
    messageKey = messageKey.trim ();

    // check if message key already exists
    if (_messagesLookup.containsKey (messageKey)) {
      // remove existing message
      _messagesLookupById.remove (_messagesLookup.get (messageKey).getMessageId ());
      _messagesLookup.remove (messageKey);
    }

    Message message = new Message (this, messageId, messageKey);
    _messagesLookupById.put (messageId, message);
    _messagesLookup.put (messageKey, message);

    return message;
  }

  public List<Message> getMessages () {
    // TODO, Review if consistent with Ravi handling
    List<Message> tmpList = new ArrayList<Message> (_messagesLookupById.values ());
    return tmpList;

    // return _messagesLookupById.values().toList();
  }

  public Message getMessageById (int messageId) {

    Message message;
    message = _messagesLookupById.get (messageId);
    return message;
  }

  public Message getMessage (String defaultMessage) {
    if (StringUtils.isEmpty (defaultMessage))
      return null;
    defaultMessage = defaultMessage.trim ();

    Message message;
    message = _messagesLookup.get (defaultMessage);
    return message;
  }

  @Override
  public String toString () {
    return _name;
  }

}
