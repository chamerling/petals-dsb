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
package org.petalslink.dsb.kernel.webapp;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.api.DSBConfigurationService;
import org.petalslink.dsb.kernel.api.webapp.WebAppServer;
import org.petalslink.dsb.webapp.api.DSBManagement;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = WebAppServer.class) })
public class JettyWebAppServerImpl implements WebAppServer {

    public static final String WEBAPP_NAME = "dsb-webapp";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "dsbmanagement", signature = DSBManagement.class, contingency = Contingency.OPTIONAL)
    private DSBManagement dsbManagement;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "dsbconfiguration", signature = DSBConfigurationService.class)
    private DSBConfigurationService dsbConfigurationService;

    private Server server;

    @LifeCycle(on = LifeCycleType.START)
    protected void startComponent() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stopCoponent() {
        this.log.debug("Stopping...");

        this.stop();
        this.server = null;
    }

    /**
     * {@inheritDoc}
     */
    @LifeCycleListener(phase = Phase.START)
    public void start() throws DSBException {
        if (isStarted()) {
            this.log.info("Server is already started...");
            return;
        }

        // get the webapps path
        File webappdir = new File(this.configurationService.getContainerConfiguration()
                .getRootDirectoryPath(), "webapps");

        File[] webapps = webappdir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().endsWith(".war");
            }
        });

        this.server = new Server(this.dsbConfigurationService.getWebAppPort());
        Set<String> webappsName = new HashSet<String>(webapps.length);
        final ContextHandlerCollection contexts = new ContextHandlerCollection();

        for (File webapp : webapps) {
            
            String webappName = webapp.getName();
            if (webappName.endsWith(".war")) {
                webappName = webappName.substring(0, webappName.length() - 4);
            }
            
            WebAppContext context = new WebAppContext();
            context.setContextPath("/" + webappName);
            context.setWar(webapp.getAbsolutePath());
            File tmp = new File(this.configurationService.getContainerConfiguration()
                    .getRootDirectoryPath(), "work");
            File workPath = new File(tmp, webappName);
            File tmpDir = new  File(workPath, webappName);
            tmpDir.mkdirs();
            context.setTempDirectory(tmpDir);
            context.setExtractWAR(true);

            // set DSBManagement in the context, will be used or not
            // TODO : Set a DSB API...
            DSBManagement management = new ProxyManagement(this.dsbManagement);
            try {
                WebAppClassLoader classloader = new WebAppClassLoader(management.getClass()
                        .getClassLoader(), context, this.logger);
                context.setClassLoader(classloader);
            } catch (IOException e1) {
            }
            context.getServletContext().setAttribute("dsbmanagement", management);
            context.setAttribute("dsbmanagement", management);
            // this.server.setHandler(context);
            contexts.addHandler(context);
            webappsName.add(webappName);
        }
        
        // add a webapp for listing all the webapps under /dsb
        final Context welcomeContext = new Context(contexts, "/dsb", Context.SESSIONS);
        final ServletHolder welcomeServlet = new ServletHolder(new ListServlet(webappsName));
        welcomeServlet.setName("DSBWebappsListServlet");
        welcomeServlet.setInitOrder(1);
        welcomeContext.addServlet(welcomeServlet, "/*");
        
        this.server.setHandler(contexts);
        try {
            this.server.start();
        } catch (Throwable e) {
            this.log.warning(e.getMessage());
        }
        this.log.info("The DSB Web application is available at http://localhost:"
                + this.dsbConfigurationService.getWebAppPort() + "/dsb/");
    }

    /**
     * @return
     */
    private boolean isStarted() {
        return (server != null && server.isStarted());
    }

    /**
     * {@inheritDoc}
     */
    @LifeCycleListener(phase = Phase.STOP)
    public void stop() {
        if (this.server != null) {
            try {
                this.server.stop();
            } catch (Exception e) {
                this.log.warning(e.getMessage());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.api.webapp.WebAppServer#getWebAppNames()
     */
    public List<String> getWebAppNames() {
        return Arrays.asList(new String[] { WEBAPP_NAME });
    }
}
