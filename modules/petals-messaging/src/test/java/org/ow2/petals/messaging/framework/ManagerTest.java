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
package org.ow2.petals.messaging.framework;

import junit.framework.TestCase;

import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.plugins.Job;
import org.ow2.petals.messaging.framework.plugins.job.JobManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ManagerTest extends TestCase {

    int start = 0;

    int stop = 0;

    int init = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        this.start = 0;

        this.stop = 0;

        this.init = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        this.start = 0;

        this.stop = 0;

        this.init = 0;
    }

    /**
     * 
     */
    public void testStart() {

        Engine engine = EngineFactory.getEngine();

        JobManager jobManager = new JobManager();
        jobManager.add("sayHello", new Job() {
            public void stop() throws LifeCycleException {
                ManagerTest.this.stop++;
            }

            public void start() throws LifeCycleException {
                ManagerTest.this.start++;

            }

            public void init() throws LifeCycleException {
                ManagerTest.this.init++;

            }

            public String getName() {
                // TODO Auto-generated method stub
                return "sayHello";
            }

            public STATE getState() {
                // TODO Auto-generated method stub
                return null;
            }
        });

        try {
            engine.addComponent(JobManager.class, jobManager);
        } catch (EngineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            engine.start();
        } catch (LifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(1, this.start);
        assertEquals(0, this.stop);
        try {
            engine.stop();
        } catch (LifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(1, this.stop);

        JobManager manager = engine.getComponent(JobManager.class);
        assertNotNull(manager);
        assertEquals(1, manager.size());
    }
}
