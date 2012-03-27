/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
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

package org.ow2.petals.binding.soap.listener.incoming.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.ow2.petals.binding.soap.SoapConstants;
import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;
import org.ow2.petals.binding.soap.listener.incoming.jetty.ServerStats;

/**
 * A servlet which displays basic information. It replaces the default 404
 * pages.
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.1
 * 
 */
public class WelcomeServlet extends HttpServlet {

	private transient final SoapServerConfig config;

	private transient final ServerStats stats;

	private static final String HTML_TITLE = "<html><head><title>Welcome SOAP Binding Component</title></head><body>";

	private static final long serialVersionUID = 1614281322239677571L;

	public WelcomeServlet(final SoapServerConfig config, final ServerStats stats) {
		this.config = config;
		this.stats = stats;
	}

	/**
     * 
     */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
		// Redirection (forward) requested ? Proceed...
		String redirect = this.config.getRedirect(req.getRequestURI());
		if(redirect != null) {
			// Example: redirect="/petals/services/myWebService"
			if(! redirect.startsWith("/")) redirect = "/" + redirect;
			int pos = redirect.substring(1).indexOf("/"); // The 2nd '/' (in the example, before "services")
			String ctxname = redirect.substring(0, pos+1); // In the example: "/petals"
			ServletContext otherContext = getServletContext().getContext(ctxname);
			// In the example: forward to "/services/myWebService"
			otherContext.getRequestDispatcher(redirect.substring(pos+1)).forward(req, resp);
			return; // forward done !
		}
		
		// No (forward) redirection: display SOAP BC information.
		
		final ServletOutputStream out = resp.getOutputStream();
		out.write(HTML_TITLE.getBytes());
		out.write("<h1>Petals BC SOAP</h1>".getBytes());
		out.write("<h2>Component Configuration</h2>".getBytes());
		out.write("<ul>".getBytes());
        out.write(("<li>" + this.config.getHostToDisplay() + "</li>").getBytes());
		out.write(("<li>HTTP Port :                " + this.config.getHttpPort() + "</li>").getBytes());
		if(this.config.isHttpsEnabled()) {
		    out.write(("<li>HTTPS Port :                " + this.config.getHttpsPort() + "</li>").getBytes());
		}
		out.write(("<li>Jetty Acceptors :     " + this.config.getJettyAcceptors() + "</li>").getBytes());
		out.write(("<li>Jetty Max pool size : " + this.config.getJettyThreadMaxPoolSize() + "</li>").getBytes());
		out.write(("<li>Jetty Min pool size : " + this.config.getJettyThreadMinPoolSize() + "</li>").getBytes());
		out.write(("<li>Services Context :    " + this.config.getServicesContext() + "</li>").getBytes());
		out.write("</ul>".getBytes());

		out.write("<h2>Web Services information</h2>".getBytes());
		out.write("<ul>".getBytes());
		out.write("<li>Services List : ".getBytes());
		final String path = "/" + this.config.getServicesContext() + "/" + this.config.getServicesMapping() + "/" + SoapConstants.Component.MAPPING_NAME;
		final String link = "<a href='" + path + "'>" + path + "</a>";
		out.write(link.getBytes());
		out.write("</li>".getBytes());
		out.write("</ul>".getBytes());

		out.write("<h2>Server Stats</h2>".getBytes());
		out.write("<ul>".getBytes());
		out.write(("<li>Start time : " + new SimpleDateFormat().format(new Date(stats.getStartTime())) + "</li>").getBytes());
		out.write(("<li>Jetty Server version : " + Server.getVersion() + "</li>").getBytes());
		out.write(("<li>Incoming WS GET requests : " + stats.getGetRequests() + "</li>").getBytes());
		out.write(("<li>Incoming WS POST requests : " + stats.getPostRequests() + "</li>").getBytes());
		out.write("</ul>".getBytes());

		out.write("</body></html>".getBytes());

		out.flush();
		out.close();
	}
	
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
