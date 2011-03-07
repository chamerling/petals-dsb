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
package org.petalslink.dsb.kernel.information;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.tools.ws.WebServiceManager;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.DSBConfigurationService;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = HttpServer.class) })
public class JettyHttpServerImpl implements HttpServer {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = DSBConfigurationService.class)
    private DSBConfigurationService configurationService;

    @Requires(name = "wsmanager", signature = WebServiceManager.class)
    private WebServiceManager webServiceManager;

    private Server server;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void startServer() {
        System.out.println("######### STARTING ON = " + this.configurationService.getWebAppPort());
        this.server = new Server(this.configurationService.getWebAppPort());
        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        this.server.setHandler(contexts);

        // create the axis context
        final Context restContext = new Context(contexts, "/", Context.SESSIONS);

        // create axis servlet holder
        final ServletHolder restServlet = new ServletHolder(
                new IndexServlet(this.webServiceManager));
        restServlet.setName("IndexServlet");
        restServlet.setInitOrder(1);
        restContext.addServlet(restServlet, "/*");
        try {
            this.server.start();
        } catch (Exception e) {
            this.log.warning(e.getMessage());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stopServer() {
        if (this.server != null) {
            try {
                this.server.stop();
            } catch (Exception e) {
                this.log.warning(e.getMessage());
            }
        }

    }

}
