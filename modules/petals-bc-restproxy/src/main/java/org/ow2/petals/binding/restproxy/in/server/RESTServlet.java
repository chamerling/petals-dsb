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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ow2.petals.binding.restproxy.in.RESTEngineContext;
import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.plugins.service.ServiceManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RESTServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -6034679393820388640L;

    /**
     * 
     */
    public RESTServlet() {
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Message message = new MessageImpl();
        message.setContent(HttpServletRequest.class, request);
        message.setContent(OutputStream.class, response.getOutputStream());
        message.setContent(InputStream.class, request.getInputStream());
        message.setContent(HttpServletResponse.class, response);

        RESTEngineContext context = EngineFactory.getEngine().getComponent(RESTEngineContext.class);
        if (context != null) {
            message.put("services", context.getServicePath());
            message.put("proxy", context.getProxyPath());
        }

        // path is used by the engine
        message.put("path", request.getPathInfo());

        try {
            // get the service plugin
            ServiceManager serviceManager = EngineFactory.getEngine().getComponent(
                    ServiceManager.class);
            if (null != serviceManager) {
                serviceManager.invoke(message);
            } else {
                // TODO = Throw error!
            }
        } catch (EngineException e) {
            e.printStackTrace();
        }

    }

}
