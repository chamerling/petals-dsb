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
package org.petalslink.dsb.transport.soap;

import java.util.logging.Logger;

import org.petalslink.dsb.api.TransportService;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.transport.api.Receiver;
import org.petalslink.dsb.transport.api.Server;

/**
 * A CXF/JAX-WS implementation of the {@link Server}, receive messages and send
 * them to the {@link Receiver} which is the core transport layer.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFServerImpl implements Server {

    private static Logger log = Logger.getLogger(CXFServerImpl.class.getName());

    private Receiver receiver;
    
    private org.petalslink.dsb.commons.service.api.Service server;

    private String host;

    private long port;

    /**
     * 
     */
    public CXFServerImpl(String host, long port) {
        this.host = host;
        this.port = port;
    }

    /**
     * {@inheritDoc}
     */
    // @LifeCycleListener(phase = Phase.START, priority=101)
    public void start() {
        TransportService service = new TransportServiceImpl(this.receiver);
        // TODO = use utility to get address
        String address = "http://" + host + ":" + port;
        this.server = CXFHelper.getService(address, TransportService.class, service);
        this.server.start();
        log.info("The WebService transporter is ready to receive messages at " + address);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (this.server != null) {
            this.server.stop();
        }
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
}
