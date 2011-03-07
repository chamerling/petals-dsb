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

package org.ow2.petals.binding.soapproxy.listener.incoming.jetty;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.util.StringUtil;

/**
 * A hanlder to replace the Jetty one in order to provide better message...
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.1
 * 
 */
public class ErrorHandler extends org.mortbay.jetty.handler.ErrorHandler {

    /**
     * 
     * @param showStacks
     */
    public ErrorHandler(final boolean showStacks) {
        super();
        this.setShowStacks(showStacks);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.jetty.handler.ErrorHandler#writeErrorPageMessage(javax.servlet.http.HttpServletRequest,
     *      java.io.Writer, int, java.lang.String, java.lang.String)
     */
    @Override
    protected void writeErrorPageMessage(final HttpServletRequest request, final Writer writer,
            final int code, final String message, final String uri) throws IOException {
        writer.write("<h1>PEtALS BC SOAP</h1>");
        writer.write("<p>");
        writer.write("There are no WebService available at this URI : " + uri);
        writer.write("</p>");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.jetty.handler.ErrorHandler#writeErrorPageBody(javax.servlet.http.HttpServletRequest,
     *      java.io.Writer, int, java.lang.String, boolean)
     */
    @Override
    protected void writeErrorPageBody(final HttpServletRequest request, final Writer writer,
            final int code, final String message, final boolean showStacks) throws IOException {
        String uri = request.getRequestURI();
        if (uri != null) {
            uri = StringUtil.replace(uri, "&", "&amp;");
            uri = StringUtil.replace(uri, "<", "&lt;");
            uri = StringUtil.replace(uri, ">", "&gt;");
        }

        this.writeErrorPageMessage(request, writer, code, message, uri);
        if (showStacks) {
            this.writeErrorPageStacks(request, writer);
        }

        writer
                .write("<p><i><small><a href=\"http://petals.ow2.org/\">Powered by PEtALS SOAP Binding Component</a></small></i></p>");
        for (int i = 0; i < 20; i++) {
            writer.write("<br/>                                                \n");
        }
    }

}
