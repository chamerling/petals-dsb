/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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

package org.ow2.petals.binding.soapproxy.listener.outgoing;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.RobustOutOnlyAxisOperation;
import org.ow2.petals.binding.soapproxy.listener.incoming.PetalsAxisService;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.util.UtilFactory;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Axis2Utils {

    /**
     * Counter used to generate the service client name.
     */
    private static AtomicLong serviceCounter = new AtomicLong();

    /**
     * The configuration context. Reuse always the same in all the
     * {@link ServiceClient}s.
     */
    private static ConfigurationContext axisCtx = null;

    static {
        try {
            axisCtx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null,
                    null);
        } catch (final AxisFault ae) {
            System.err.println("WARNING : Error while creating the Axis Configuration Context");
        }
    }

    /**
     * Creates a new instance of {@link Axis2Utils}
     * 
     */
    private Axis2Utils() {
    }

    /**
     * Create a ServiceClient with an AxisService set with the good operation
     * 
     * @param mep
     *            the message exchange pattern used. Non null
     * @param operation
     *            the target operation QName. Non null
     * @return a ServiceClient. Not null.
     * @throws HandlingException
     */
    public static ServiceClient createServiceClient(final URI mep, final QName operation)
            throws MessagingException {
        ServiceClient client = null;

        final AxisService service = new AxisService(
                PetalsAxisService.OUTGOING_SERVICE_CLIENT_PREFIX
                        + UtilFactory.getIdUtil().createId() + serviceCounter.incrementAndGet());

        AxisOperation axisOperation = null;
        if (MEPConstants.IN_ONLY_PATTERN.equals(mep)) {
            axisOperation = new OutOnlyAxisOperation(operation);
        } else if (MEPConstants.ROBUST_IN_ONLY_PATTERN.equals(mep)) {
            axisOperation = new RobustOutOnlyAxisOperation(operation);
        } else if (MEPConstants.IN_OPTIONAL_OUT_PATTERN.equals(mep)
                || MEPConstants.IN_OUT_PATTERN.equals(mep)) {
            axisOperation = new OutInAxisOperation(operation);
        }
        service.addOperation(axisOperation);

        try {
            client = new ServiceClient(axisCtx, service);
        } catch (final AxisFault e) {
            throw new MessagingException("Can't create ServiceClient", e);
        }
        return client;
    }
}
