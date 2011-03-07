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
package org.ow2.petals.binding.restproxy.in;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ow2.petals.binding.restproxy.HTTPUtils;
import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.message.Message;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RESTProxyService extends AbstractRESTService {

    /**
     * {@inheritDoc}
     */
    public void invoke(Message message) throws EngineException {
        RESTEngineContext context = EngineFactory.getEngine().getComponent(RESTEngineContext.class);
        if (context != null) {
            context.getRestStats().newProxy();
        }
        HttpServletRequest request = message.getContent(HttpServletRequest.class);
        HttpServletResponse response = message.getContent(HttpServletResponse.class);
        String target = (message.get("path") != null) ? message.get("path").toString() : "";
        String proxyPath = (message.get("proxy") != null) ? message.get("proxy").toString() : "";

        String newTarget = HTTPUtils.decode(target, request);
        String path = newTarget.substring(proxyPath.length() + 1, newTarget.length());

        if ((request.getQueryString() != null) && (request.getQueryString().trim().length() > 0)) {
            path = path + "?" + request.getQueryString();
        }

        this.proxify(path, request, response);
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
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void start() throws LifeCycleException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void stop() throws LifeCycleException {
        // TODO Auto-generated method stub

    }

}
