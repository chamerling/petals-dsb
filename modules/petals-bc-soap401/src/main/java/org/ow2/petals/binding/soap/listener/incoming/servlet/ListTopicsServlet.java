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
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;
import org.ow2.petals.ws.notification.Subscription;
import org.ow2.petals.ws.notification.WsnManager;

/**
 * Display topics information
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.1
 * 
 */
public class ListTopicsServlet extends HttpServlet {

    private static final String HTML_TOP = "<html><head><title>petals-bc-soap : Topic List</title></head><body><h1>PEtALS BC SOAP</h1>";

    private static final String HTML_BOTTOM = "</body></html>";

    public static final String MAPPING_NAME = "listTopics";

    private final SoapServerConfig config;

    private final WsnManager manager;

    /**
     * 
     */
    private static final long serialVersionUID = 2804814249926503391L;

    /**
     * 
     * @param manager
     * @param config
     */
    public ListTopicsServlet(final WsnManager manager, final SoapServerConfig config) {
        this.manager = manager;
        this.config = config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final ServletOutputStream out = resp.getOutputStream();
        out.write(HTML_TOP.getBytes());

        if (this.config.isProvidesList()) {
            this.printTopics(out);
        } else {
            this.listNotAvailable(out);
        }

        out
                .write(("<p><center><a href='" + this.config.getBaseURL() + "'> - Index -</a></center></p>")
                        .getBytes());
        out.write(HTML_BOTTOM.getBytes());
        out.flush();
        out.close();
    }

    /**
     * Print the topics and their information
     * 
     * @param out
     * @throws IOException
     */
    private void printTopics(final ServletOutputStream out) throws IOException {
        final QName[] topics = this.manager.getTopicExpression();
        out.write("<h2>Topic Service Information</h2>".getBytes());
        out.write("<ul>".getBytes());
        out.write(("<li>Subscription persistance : "
                + (this.manager.getPersistance() == null ? "off" : "on") + "</li>").getBytes());
        out.write("</ul>".getBytes());

        // topics
        out.write("<h2>Available topics</h2>".getBytes());
        if (topics.length > 0) {
            out.write("<ul>".getBytes());
            for (final QName topicName : topics) {
                // Topic topic = manager.getTopic(topicName);
                out.write(("<li>" + topicName.toString() + "</li>").getBytes());
            }
            out.write("</ul>".getBytes());

        } else {
            out.write("No topic".getBytes());
        }

        // subscribers
        out.write("<h2>Notification subscribers</h2>".getBytes());
        final Collection<Subscription> subs = this.manager.getSubscriptions();
        if ((subs != null) && (subs.size() > 0)) {
            for (final Subscription subscription : subs) {
                out.write("<ul>".getBytes());
                out.write(("<li>Consumer EPR : "
                        + subscription.getConsumerEPR().getAddress().toString() + "</li>")
                        .getBytes());
                out.write(("<li>Topic Name : " + subscription.getFilter().getTopicName() + "</li>")
                        .getBytes());
                out.write(("<li>Creation Time : " + subscription.getCreationTime() + "</li>")
                        .getBytes());
                out.write("</ul>".getBytes());
            }
        } else {
            out.write("No subscribers".getBytes());
        }

    }

    /**
     * 
     * @param out
     * @throws IOException
     */
    private void listNotAvailable(final ServletOutputStream out) throws IOException {
        out.write("<h1><font color='red'>The list of topics is not available</font></h1>"
                .getBytes());
        out.write("It must be activated in the SOAP component descriptor...".getBytes());
    }
}
