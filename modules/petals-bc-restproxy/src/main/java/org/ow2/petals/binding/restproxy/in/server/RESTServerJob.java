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
package org.ow2.petals.binding.restproxy.in.server;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.ow2.petals.binding.restproxy.Constants;
import org.ow2.petals.binding.restproxy.in.RESTEngineContext;
import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.plugins.Job;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RESTServerJob implements Job {

    private org.mortbay.jetty.Server jettyServer;

    /**
     * 
     */
    public RESTServerJob() {
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "RESTServerJob based on Jetty";
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        try {
            this.jettyServer.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        try {
            this.jettyServer.stop();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public STATE getState() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws LifeCycleException {
        Engine e = EngineFactory.getEngine();
        RESTEngineContext restEngineContext = e.getComponent(RESTEngineContext.class);
        int port = Constants.DEFAULT_PORT;
        if (restEngineContext != null) {
            port = restEngineContext.getPort();
        }
        this.jettyServer = new Server(port);
        // let's add the servlet which have a reference to the engine
        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        this.jettyServer.setHandler(contexts);

        // create the axis context
        final Context restContext = new Context(contexts, "/", Context.SESSIONS);

        // create axis servlet holder
        final ServletHolder restServlet = new ServletHolder(new RESTServlet());
        restServlet.setName("RESTServlet");
        restServlet.setInitOrder(1);
        restContext.addServlet(restServlet, "/*");
    }

}
