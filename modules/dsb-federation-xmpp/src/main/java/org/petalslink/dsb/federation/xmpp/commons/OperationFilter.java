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
package org.petalslink.dsb.federation.xmpp.commons;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * Filter incoming packets depending on the operation value
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class OperationFilter implements PacketFilter {

    Set<String> values;

    private static Log logger = LogFactory.getLog(OperationFilter.class);

    /**
     * 
     */
    public OperationFilter() {
        this.values = new HashSet<String>();
    }

    /**
     * 
     */
    public OperationFilter(String[] operations) {
        this();
        for (String string : operations) {
            this.values.add(string);
        }
    }

    public OperationFilter(Class<?> clazz) {
        this();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (logger.isInfoEnabled()) {
                logger.info("Adding authorized operation " + method.getName());
            }
            this.values.add(method.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean accept(Packet packet) {
        if (!(packet instanceof Message)) {
            return false;
        } else {
            Message m = (Message) packet;
            if (logger.isDebugEnabled()) {
                logger.debug("Got a message in operation filter " + m.toXML());
            }
            return m.getType().equals(Message.Type.chat) && this.isValidOperation(m);
        }
    }

    /**
     * @param action
     * @return
     */
    private boolean isValidOperation(Message message) {
        String action = Adapter.getAction(message);
        if (logger.isDebugEnabled()) {
            logger.debug("Incoming operation is '" + action + "' and authorized are '"
                    + this.values + "'");
        }
        return (action != null) && this.values.contains(action);
    }

    /**
     * @return the values
     */
    public Set<String> getValues() {
        return this.values;
    }

}
