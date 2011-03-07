/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package com.ebmwebsourcing.commons.wsframework.messaging;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.ow2.petals.messaging.framework.message.Callback;
import org.ow2.petals.messaging.framework.message.Client;
import org.ow2.petals.messaging.framework.message.Constants;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.MessagingEngine;
import org.ow2.petals.messaging.framework.message.MessagingEngineImpl;
import org.ow2.petals.messaging.framework.message.MessagingException;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class MessagingEngineTest extends TestCase {

	private AtomicLong count;

	private Client defaultClient;

	/**
	 * @param name
	 */
	public MessagingEngineTest(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.count = new AtomicLong(0);

		this.defaultClient = new Client() {

			public void send(Message in, Callback callback)
					throws MessagingException {
				MessagingEngineTest.this.count.incrementAndGet();
				System.out.println("Sending a message in asynchronous way");
			}

			public Message send(Message in) throws MessagingException {
				MessagingEngineTest.this.count.incrementAndGet();
				System.out
						.println("Sending a message and retruning a fake response...");
				Message result = new MessageImpl();
				result.setContent(String.class, "This is the message content");
				return result;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.count = null;
	}

	public void testSendNoProtocol() {
		MessagingEngine engine = new MessagingEngineImpl();
		Message message = new MessageImpl();

		try {
			engine.send(message);
			fail();
		} catch (MessagingException e) {
		}
	}

	public void testSendNoClient() {
		MessagingEngine engine = new MessagingEngineImpl();
		Message message = new MessageImpl();
		message.put(Constants.PROTOCOL, "http");

		try {
			engine.send(message);
		} catch (MessagingException e) {
		}

		assertEquals(0, this.count.get());
	}

	public void testSend() throws Exception {
		MessagingEngine engine = new MessagingEngineImpl();
		engine.addClient("http", this.defaultClient);
		Message message = new MessageImpl();
		message.put(Constants.PROTOCOL, "http");

		try {
			engine.send(message);
		} catch (MessagingException e) {
			fail();
		}

		assertEquals(1, this.count.get());
	}



	public void testSendAsync() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		MessagingEngine engine = new MessagingEngineImpl();
		engine.addClient("http", this.defaultClient);
		Message message = new MessageImpl();
		message.put(Constants.PROTOCOL, "http");

		try {
			Callback callback = new Callback() {

				public void onMessage(Message message) {
					System.out.println("Got a response!");
					try {
						System.out.println("Waiting 2000 ms...");
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					latch.countDown();
				}

				public void onError(MessagingException messagingException) {
					System.out.println("Got an error");
					try {
						System.out.println("Waiting 2000 ms...");
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					latch.countDown();
				}
			};

			engine.send(message, callback);
		} catch (MessagingException e) {
			fail(e.getMessage());
		}

		System.out.println("Waiting for the response...");
		latch.await();
		System.out.println("Task is complete!");

		assertEquals(1, this.count.get());
	}

}
