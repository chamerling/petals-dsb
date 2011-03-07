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
package com.ebmwebsourcing.commons.wsframework;

import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.plugins.Handler;
import org.ow2.petals.messaging.framework.plugins.HandlerException;
import org.ow2.petals.messaging.framework.plugins.handler.HandlerManager;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class HandlerTest extends TestCase {

	Engine engine;

	AtomicLong count;

	Handler handler;

    HandlerManager handlerManager;

	/**
	 * @param name
	 */
	public HandlerTest(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		this.count = new AtomicLong(0);
        this.engine = EngineFactory.getEngine();

        this.handlerManager = new HandlerManager();
        this.engine.addComponent(HandlerManager.class, this.handlerManager);

		this.handler = new Handler() {
			public void handle(Message message) throws HandlerException {
				HandlerTest.this.count.incrementAndGet();
			}

			public String getName() {
				return null;
			}

            public STATE getState() {
                // TODO Auto-generated method stub
                return null;
            }

            public void init() throws LifeCycleException {
                // TODO Auto-generated method stub

            }

            public void start() throws LifeCycleException {
                // TODO Auto-generated method stub

            }

            public void stop() throws LifeCycleException {
                // TODO Auto-generated method stub

            }
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		this.engine.stop();
		this.count = null;
		this.engine = null;
	}

	public void testNoHandler() {
		try {
            this.engine.start();
        } catch (LifeCycleException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            this.engine.getComponent(HandlerManager.class).handle(null);
		} catch (EngineException e) {
			fail(e.getMessage());
		}
		assertEquals(0, this.count.longValue());
	}

	/**
	 * 
	 */
	public void testOneHandlerBadMessage() {
		try {
            this.engine.getComponent(HandlerManager.class).addHandler(String.class, this.handler);
		} catch (EngineException e) {
			fail(e.getMessage());
		}
        try {
            this.engine.start();
        } catch (LifeCycleException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

		try {
            this.engine.getComponent(HandlerManager.class).handle(null);
		} catch (EngineException e) {
			fail(e.getMessage());
		}

		assertEquals(0, this.count.longValue());
	}

	public void testOneHandlerOneMessage() throws Exception {
		try {
            this.engine.getComponent(HandlerManager.class).addHandler(String.class, this.handler);
		} catch (EngineException e) {
			fail(e.getMessage());
		}
		this.engine.start();

		Message m = new MessageImpl();
		m.setContent(String.class, "This is the message content");

		try {
            this.engine.getComponent(HandlerManager.class).handle(m);
		} catch (EngineException e) {
			fail(e.getMessage());
		}

		assertEquals(1, this.count.longValue());
	}

	public void testTwoHandlersOneMessage() throws Exception {
		try {
            this.engine.getComponent(HandlerManager.class).addHandler(String.class, this.handler);
            this.engine.getComponent(HandlerManager.class).addHandler(String.class, this.handler);
		} catch (EngineException e) {
			fail(e.getMessage());
		}
		this.engine.start();

		Message m = new MessageImpl();
		m.setContent(String.class, "This is the message content");

		try {
            this.engine.getComponent(HandlerManager.class).handle(m);
		} catch (EngineException e) {
			fail(e.getMessage());
		}

		assertEquals(2, this.count.longValue());
	}

	public void testTwoHandlersTwoMessages() throws Exception {
		try {
            this.engine.getComponent(HandlerManager.class).addHandler(String.class, this.handler);
            this.engine.getComponent(HandlerManager.class).addHandler(String.class, this.handler);
		} catch (EngineException e) {
			fail(e.getMessage());
		}
		this.engine.start();

		Message m = new MessageImpl();
		m.setContent(String.class, "This is the message content");

		try {
            this.engine.getComponent(HandlerManager.class).handle(m);
		} catch (EngineException e) {
			fail(e.getMessage());
		}

		assertEquals(2, this.count.longValue());

		// will fire no handlers

		m = new MessageImpl();
		m.setContent(Integer.class, 1);

		try {
            this.engine.getComponent(HandlerManager.class).handle(m);
		} catch (EngineException e) {
			fail(e.getMessage());
		}

		assertEquals(2, this.count.longValue());

	}

	public void testTwoHandlersTwoMessages2() throws Exception {
		try {
            this.engine.getComponent(HandlerManager.class).addHandler(String.class, this.handler);
            this.engine.getComponent(HandlerManager.class).addHandler(Integer.class, this.handler);
		} catch (EngineException e) {
			fail(e.getMessage());
		}
		this.engine.start();

		Message m = new MessageImpl();
		m.setContent(String.class, "This is the message content");

		try {
            this.engine.getComponent(HandlerManager.class).handle(m);
		} catch (EngineException e) {
			fail(e.getMessage());
		}

		assertEquals(1, this.count.longValue());

		m = new MessageImpl();
		m.setContent(Integer.class, 1);

		try {
            this.engine.getComponent(HandlerManager.class).handle(m);
		} catch (EngineException e) {
			fail(e.getMessage());
		}
		assertEquals(2, this.count.longValue());

	}


}
