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
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class MQReceiver {
	private String _queueName = null;
	private String _host = null;
	private long _messageCount = 0;
	private long _timeout = 0;
	
	private Channel _channel = null;
	private QueueingConsumer _consumer = null;
	
	public MQReceiver (String queueName, String host) { 
		_queueName = queueName;
		_host = host;
	}
	
	public MQReceiver (String queueName, String host, long timeout) { 
		_queueName = queueName;
		_host = host;
		_timeout = timeout;
	}
	
	public long GetMessageCount () { return _messageCount; }
	
	public void Initialize() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(_host);
	    Connection connection = factory.newConnection();
	    _channel = connection.createChannel();

	    boolean queue_durable = true;
	    _channel.queueDeclare(_queueName, queue_durable, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    _consumer = new QueueingConsumer(_channel);
	    boolean auto_ack = false;
	    _channel.basicConsume(_queueName, auto_ack, _consumer);
	}

	public String ReceiveOnce() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException, IOException {
		QueueingConsumer.Delivery delivery = _timeout > 0 ? _consumer.nextDelivery(_timeout) : _consumer.nextDelivery();
		if (delivery != null) {
			String message = new String(delivery.getBody());
			System.out.println(" [x] Received " + message.length() + "b");
			_channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

			return message;
		}
		return null;
	}
}
