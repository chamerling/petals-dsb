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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.ow2.petals.binding.restproxy.in.RESTEngineContext.Consume;
import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.plugins.Service;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ListService implements Service {

    public void invoke(Message message) {
        OutputStream os = message.getContent(OutputStream.class);
        Engine engine = EngineFactory.getEngine();
        RESTEngineContext context = engine.getComponent(RESTEngineContext.class);
        if (context == null) {
            // TODO = write error
            return;
        }
        Map<String, Consume> consumes = context.getRestConsumers();

        if (os != null) {
            try {
                os.write("<html><title>REST Services Information</title><body>".getBytes());

                os.write("<h2>Information</h2>".getBytes());
                os.write(("<b>Port</b> : " + context.getPort()).getBytes());
                os.write(("<br><b>Proxy Path</b> : " + context.getProxyPath()).getBytes());
                os.write(("<br><b>Services Path</b> : " + context.getServicePath()).getBytes());

                os.write("<h2>Services</h2>".getBytes());
                os.write("<br><br>".getBytes());
                os
                        .write("<table border='1'><tr><td>REST Service Name</td><td>JBI ServiceName</td><td>JBI Interface Name</td><td>JBI Endpoint Name</td></tr>"
                                .getBytes());
                for (String serviceName : consumes.keySet()) {
                    os.write("<tr>".getBytes());
                    os.write(("<td><a href='" + context.getServicePath() + "/" + serviceName + "'>"
                            + serviceName + "</a></td>").getBytes());
                    Consume consume = consumes.get(serviceName);
                    if (consume != null) {
                        os.write(("<td>" + consume.getServiceName() + "</td>").getBytes());
                        os.write(("<td>" + consume.getInterfaceName() + "</td>").getBytes());
                        os.write(("<td>" + consume.getEndpointName() + "</td>").getBytes());
                    } else {
                        os.write("<td>-</td><td>-</td><td>-</td>".getBytes());
                    }
                    os.write("</tr>".getBytes());
                }
                os.write("</table>".getBytes());

                os.write("</body></html>".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        org.ow2.petals.binding.restproxy.IOUtils.flushAndCose(os);
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
