/**
 * PETALS - PETALS Services Platform. Copyright (c) 2005 EBM Websourcing,
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

package org.ow2.petals.esb.external.protocol.soap.impl.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.BoundedThreadPool;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPListenerImpl;
import org.ow2.petals.esb.external.protocol.soap.impl.servlet.SoapDeploymentServlet;
import org.ow2.petals.esb.kernel.api.ESBException;



public class SoapServer {

	/**
	 * The logger
	 */
	private Logger log = Logger.getLogger(SoapServer.class.getName());

	public static final int HEADER_BUFFER_SIZE = 4096 * 4;


	private static SoapServer instance = null;


	private SoapDeploymentServlet soapDeploymentServlet;

	private SoapServerConfig config;

	private Server server;

	private BoundedThreadPool threadPool;



	private List<SOAPListenerImpl> listeners = new ArrayList<SOAPListenerImpl>();

	/**
	 * Creates a new instance of {@link SoapServer}
	 * 
	 * @param configContext
	 * @param config
	 * @param componentContext
	 * @param logger
	 * @throws AxisFault
	 */
	private SoapServer(final SoapServerConfig config)  {
		this.config = config;

		// jetty threapool configuration
		this.threadPool = new BoundedThreadPool();
		this.threadPool.setName("SoapJettyThreadPool");
		this.threadPool.setMaxThreads(this.config.getJettyThreadMaxPoolSize());
		this.threadPool.setMinThreads(this.config.getJettyThreadMinPoolSize());

		// jetty http connector configuration
		final SelectChannelConnector nioConnector = new SelectChannelConnector();
		nioConnector.setPort(this.config.getPort());

		// If we assign the host, we will only be able to contact server
		// on it. No value or a null one is a wildcard so connection is possible
		// on network interface
		// @see java.net.InetSocketAddress
		if (this.config.isRestrict()) {
			nioConnector.setHost(this.config.getHost());
		}

		nioConnector.setHeaderBufferSize(HEADER_BUFFER_SIZE);
		nioConnector.setStatsOn(false);
		nioConnector.setAcceptors(this.config.getJettyAcceptors());

		this.server = new Server();
		this.server.setConnectors(new Connector[] { nioConnector });
		this.server.setThreadPool(this.threadPool);


	}

	public void createDefaultServlet() throws WSDLException {
		// add welcome servlet
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		// create context handlers
		this.server.setHandler(contexts);
		final Context welcomeContext = new Context(contexts, "/", Context.SESSIONS);
		this.soapDeploymentServlet = new SoapDeploymentServlet(this.config, this.listeners);
		ServletHolder welcomeServletHolder = new ServletHolder(soapDeploymentServlet);
		welcomeServletHolder.setName("WelcomeServlet");
		welcomeServletHolder.setInitOrder(1);
		welcomeContext.addServlet(welcomeServletHolder, "/");
		log.info("Welcome servlet deployed at: " + "http://" + this.config.getHost() + ":" + this.config.getPort() + "/");
		this.log.finest("Welcome servlet deployed at: " + "http://" + this.config.getHost() + ":" + this.config.getPort() + "/");
	}

	public static SoapServer getInstance() throws ESBException {
		try {
			if(instance == null) {
				instance = new SoapServer(new SoapServerConfig());
				instance.createDefaultServlet();
				instance.start();
			}
			if(!instance.getServer().isStarted()) {
				instance.createDefaultServlet();
				instance.start();
			}
		} catch (WSDLException e) {
			throw new ESBException(e);
		}
		return instance;
	}

	public SoapDeploymentServlet getWelcomeServlet() {
		return soapDeploymentServlet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.axis2.transport.TransportListener#start()
	 */
	public void start() throws ESBException   {
		this.log.info("Starting Jetty server...");
		this.log.info("Host : "
				+ (!this.config.isRestrict() ? "*" : this.config.getHost() + " (restricted)")
				+ " / Port : " + this.config.getPort() + " / Jetty Max poolsize : "
				+ this.config.getJettyThreadMaxPoolSize() + " / Jetty Min poolsize : "
				+ this.config.getJettyThreadMinPoolSize() + " / Jetty Acceptors size : "
				+ this.config.getJettyAcceptors());

		try {
			this.server.start();
		} catch (Exception e) {
			throw new ESBException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.axis2.transport.TransportListener#stop()
	 */
	public void stop() throws ESBException  {
		this.log.log(Level.INFO, "Stop and kill Jetty server...");

		// stop jetty server
		try {
			this.listeners = new ArrayList<SOAPListenerImpl>();
			for(Handler h : this.server.getHandlers()) {
				this.server.removeHandler(h);
			}
			for(Handler h : this.server.getChildHandlers()) {
				this.server.removeHandler(h);
			}
//			for(Connector c : this.server.getConnectors()) {
//				this.server.removeConnector(c);
//			}
			this.server.stop();
			//this.server.destroy();
			
		} catch (Exception e) {
			throw new ESBException(e);
		}

	}
	



	public Server getServer() {
		return server;
	}

	public SoapServerConfig getConfig() {
		return config;
	}

}
