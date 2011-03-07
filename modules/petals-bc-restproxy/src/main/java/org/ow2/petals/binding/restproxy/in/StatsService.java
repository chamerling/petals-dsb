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

import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.plugins.Service;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class StatsService implements Service {

    public void invoke(Message message) {

        OutputStream os = message.getContent(OutputStream.class);
        Engine engine = EngineFactory.getEngine();
        RESTEngineContext context = engine.getComponent(RESTEngineContext.class);
        if (context == null) {
            // TODO = write error
            return;
        }
        RESTStats stats = context.getRestStats();

        if (os != null) {
            try {
                os.write("<html><title>REST statistics</title><body>".getBytes());
                os.write("<h2>REST statistics</h2>".getBytes());
                os.write("<br><br>".getBytes());
                os.write(("- Get : " + stats.getGetNb()).getBytes());
                os.write("<br>".getBytes());
                os.write(("- Post : " + stats.getPostNb()).getBytes());
                os.write("<br>".getBytes());
                os.write(("- Put : " + stats.getPutNb()).getBytes());
                os.write("<br>".getBytes());
                os.write(("- Delete : " + stats.getDeleteNb()).getBytes());
                os.write("<br>".getBytes());
                os.write(("- Proxy calls : " + stats.getProxyNb()).getBytes());
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
