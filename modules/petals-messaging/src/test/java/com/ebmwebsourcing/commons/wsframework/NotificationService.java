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

import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.plugins.Handler;
import org.ow2.petals.messaging.framework.plugins.HandlerException;
import org.ow2.petals.messaging.framework.plugins.Job;
import org.ow2.petals.messaging.framework.plugins.handler.HandlerManager;
import org.ow2.petals.messaging.framework.plugins.job.JobManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class NotificationService {

    public static void main(String[] args) throws Exception {
        Engine engine = EngineFactory.getEngine();

        Handler handler = new Handler() {
            public void handle(Message data) throws HandlerException {
                System.out.println("This is how I handle data : " + data);
                String s = data.getContent(String.class);
                if (s != null) {
                    System.out.println("This is the data = " + s);
                }
            }

            /**
             * {@inheritDoc}
             */
            public String getName() {
                return "handler";
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

        Handler iHandler = new Handler() {

            public String getName() {
                return null;
            }

            public void handle(Message message) throws HandlerException {
                System.out.println("The integer handler");
                Integer i = message.getContent(Integer.class);
                System.out.println(i);
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

        HandlerManager manager = EngineFactory.getEngine().getComponent(HandlerManager.class);
        if (manager == null) {
            manager = new HandlerManager();
            EngineFactory.getEngine().addComponent(HandlerManager.class, manager);
        }

        manager.addHandler(String.class, handler);
        manager.addHandler(Integer.class, iHandler);

        JobManager jobManager = EngineFactory.getEngine().getComponent(JobManager.class);
        if (jobManager == null) {
            jobManager = new JobManager();
            EngineFactory.getEngine().addComponent(JobManager.class, jobManager);
        }

        jobManager.add("foo", new Job() {
            public void stop() {
                System.out.println("Stop Job");
            }

            public void start() {
                System.out.println("Start Job");
            }

            /**
             * {@inheritDoc}
             */
            public String getName() {
                return "service1";
            }

            public STATE getState() {
                // TODO Auto-generated method stub
                return null;
            }

            public void init() throws LifeCycleException {
                // TODO Auto-generated method stub

            }

        });

        Message message = new MessageImpl();
        message.setContent(String.class, "DATATATATATA");

        Message m = new MessageImpl();
        m.setContent(Integer.class, 1);

        engine.start();
        System.out.println("Handing String...");

        HandlerManager handlerManager = engine.getComponent(HandlerManager.class);
        handlerManager.handle(message);
        System.out.println("Handling Integer");
        engine.getComponent(HandlerManager.class).handle(m);
        engine.stop();

    }
}
