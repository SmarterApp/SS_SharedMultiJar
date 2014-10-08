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

import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import AIR.Common.Helpers.CaseInsensitiveMap;

public class MessageContextType
{

  private final CaseInsensitiveMap<MessageContext> _messageContexts = new CaseInsensitiveMap<MessageContext> ();
  private final MessageSystem                      _messageSystem;
  private final ContextType                        _contextType;

  @JsonProperty ("System")
  public MessageSystem getMessageSystem () {
    return _messageSystem;
  }

  @JsonProperty ("Type")
  public ContextType getContextType () {
    return _contextType;
  }

  public MessageContextType (MessageSystem messageSystem, ContextType contextType) {
    _messageSystem = messageSystem;
    _contextType = contextType;
  }

  public MessageContext AddContext (String context) {
    if (StringUtils.isEmpty (context))
      return null;
    context = context.trim ();

    MessageContext messageContext = new MessageContext (this, context);
    _messageContexts.put (context.trim (), messageContext);
    return messageContext;
  }

  public List<MessageContext> getContexts () {
    // TODO CReview how Ravi handles tolist() conversions
    List<MessageContext> tmpList = new ArrayList<MessageContext> (_messageContexts.values ());
    return tmpList;
    // return _messageContexts.values ().toList();
  }

  public MessageContext getContext (String context) {
    if (StringUtils.isEmpty (context))
      return null;
    context = context.trim ();

    MessageContext messageContext;
    messageContext = _messageContexts.get (context);
    // _messageContexts.TryGetValue(context, out messageContext);
    return messageContext;
  }

  @Override
  public String toString () {
    return _contextType.toString ();
  }

}
