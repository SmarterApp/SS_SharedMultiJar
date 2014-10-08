/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package TDS.MessageQueue;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class MQSender {
	
	private String _queueName = null;
	private String _host = null;
	
	public MQSender (String queueName, String host) { 
		_queueName = queueName;
		_host = host;
	}
	
	public void Send(String message) throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(_host);
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    boolean queue_durable = true;
	    channel.queueDeclare(_queueName, queue_durable, false, false, null);
	    
		sendOnce(channel, message);
	    
	    channel.close();
	    connection.close();
	}
	
	public void sendOnce(Channel channel, String message) throws IOException {
		channel.basicPublish("", _queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
	    System.out.println(" [x] Sent " + message.length() + "b");
	    
	}
}
