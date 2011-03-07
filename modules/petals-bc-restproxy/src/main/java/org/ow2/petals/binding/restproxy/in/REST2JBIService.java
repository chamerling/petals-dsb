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
public class REST2JBIService extends AbstractRESTService {

    /**
     * 
     */
    public REST2JBIService() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(Message message) throws EngineException {
        // get the engine
        RESTEngineContext restEngineContext = EngineFactory.getEngine().getComponent(
                RESTEngineContext.class);

        HttpServletRequest request = message.getContent(HttpServletRequest.class);
        HttpServletResponse response = message.getContent(HttpServletResponse.class);

        if ((request == null) || (response == null)) {
            throw new EngineException(
                    "Can not find valid HTTP contents (HTTServletResquest or HTTPServletResponse");
        }

        this.updateStats(request, restEngineContext);

        String path = (message.get("path") != null) ? message.get("path").toString() : "";
        String servicePath = (message.get("services") != null) ? message.get("services").toString()
                : "";

        String tmp = HTTPUtils.decode(path, request);

        if (servicePath.endsWith("/")) {
            servicePath = servicePath.substring(0, servicePath.length() - 1);
        }
        tmp = tmp.substring(servicePath.length() + 1, tmp.length());
        if (tmp.startsWith("/") && (tmp.length() > 1)) {
            tmp = tmp.substring(1, tmp.length());
        }

        String serviceName = null;
        String resourcePath = null;
        if (tmp.indexOf('/') >= 0) {
            // get the first slash
            serviceName = tmp.substring(0, tmp.indexOf('/'));
            if (tmp.length() > tmp.indexOf('/')) {
                // get the resource if only it is defined...
                resourcePath = tmp.substring(tmp.indexOf('/') + 1, tmp.length());
            }
        } else {
            serviceName = tmp;
            resourcePath = "";
        }

        if ((request.getQueryString() != null) && (request.getQueryString().trim().length() > 0)) {
            resourcePath = resourcePath + "?" + request.getQueryString();
        }

        this.invoke(serviceName.trim(), resourcePath.trim(), request, response);
    }

    /**
     * @param request
     * @param restEngineContext
     */
    private void updateStats(HttpServletRequest request, RESTEngineContext restEngineContext) {
        String httpMethod = request.getMethod();
        if ("POST".equalsIgnoreCase(httpMethod)) {
            restEngineContext.getRestStats().newPost();
        } else if ("GET".equalsIgnoreCase(httpMethod)) {
            restEngineContext.getRestStats().newGet();
        } else if ("PUT".equalsIgnoreCase(httpMethod)) {
            restEngineContext.getRestStats().newPut();
        } else if ("DELETE".equalsIgnoreCase(httpMethod)) {
            restEngineContext.getRestStats().newDelete();
        } else if ("HEAD".equalsIgnoreCase(httpMethod)) {
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
