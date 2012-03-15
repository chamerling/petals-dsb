/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.outgoing;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;

public class MockWebService {

    static final String STARTED_MOCK_WEB_SERVICE_LOG_MSG = "Started mock web service on '%s'.";

    static final String STOPPED_MOCK_WEB_SERVICE_LOG_MSG = "Stopped mock web service on '%s'.";

    static final String RECEIVED_GET_REQUEST_LOG_MSG = "Received GET request on '%s'.";

    static final String RECEIVED_POST_REQUEST_LOG_MSG = "Received POST request on '%s'.";

    static final Logger logger = Logger.getLogger(MockWebService.class.getName());

    public static final int HEADER_BUFFER_SIZE = 4096 * 4;

    private final URL serviceUrl;

    private final MockWebServiceServlet mockWebServiceServlet;

    private final int port;

    private final String host;

    private final Server server;

    private boolean isStarted;

    public MockWebService(final URL serviceUrl) {
        this.serviceUrl = serviceUrl;

        // jetty http connector configuration
        final String[] splittedURLParts = serviceUrl.toString().split("/");
        this.port = serviceUrl.getPort();
        this.host = serviceUrl.getHost();
        final String servletPath = serviceUrl
                .toString()
                .replace(
                        serviceUrl.getProtocol() + "://" + serviceUrl.getHost() + ":"
                                + serviceUrl.getPort(), "")
                .replace("/" + splittedURLParts[splittedURLParts.length - 1], "");

        final SelectChannelConnector nioConnector = new SelectChannelConnector();
        nioConnector.setPort(this.port);
        nioConnector.setHost(this.host);

        this.server = new Server();
        this.server.setConnectors(new Connector[] { nioConnector });

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        this.server.setHandler(contexts);
        final Context mockWebServiceContext = new Context(contexts, servletPath, Context.SESSIONS);
        this.mockWebServiceServlet = new MockWebServiceServlet();
        ServletHolder mockWebServiceServletHolder = new ServletHolder(mockWebServiceServlet);

        mockWebServiceServletHolder.setName(splittedURLParts[splittedURLParts.length - 1]);
        mockWebServiceContext.addServlet(mockWebServiceServletHolder, "/");

        this.isStarted = false;
    }

    public final boolean isStarted() {
        return isStarted;
    }

    public final void start() {
        if (isStarted)
            return;
        try {
            this.server.start();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
        logger.info(String.format(STARTED_MOCK_WEB_SERVICE_LOG_MSG, String.valueOf(serviceUrl)));
        isStarted = true;
    }

    public final void stop() {

        for (Handler h : this.server.getHandlers()) {
            this.server.removeHandler(h);
        }
        for (Handler h : this.server.getChildHandlers()) {
            this.server.removeHandler(h);
        }
        try {
            this.server.stop();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
        logger.info(String.format(STOPPED_MOCK_WEB_SERVICE_LOG_MSG, String.valueOf(serviceUrl)));
        isStarted = false;
    }

    public void onGet(HttpServletRequest req, HttpServletResponse resp) {
    }

    public void onPost(HttpServletRequest req, HttpServletResponse resp) {
    }

    private class MockWebServiceServlet extends HttpServlet {

        private static final long serialVersionUID = 3069362997017015761L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            logger.info(String.format(RECEIVED_GET_REQUEST_LOG_MSG, serviceUrl));
            mockGet(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            logger.info(String.format(RECEIVED_POST_REQUEST_LOG_MSG, serviceUrl));

            mockPost(req, resp);
        }

        private final void mockGet(HttpServletRequest req, HttpServletResponse resp) {
            resp.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
            resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
            resp.setDateHeader("Expires", 0); // prevents caching at the proxy
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            onGet(req, resp);
        }

        private final void mockPost(HttpServletRequest req, HttpServletResponse resp) {
            mockGet(req, resp);
            onPost(req, resp);
        }

    }
}
