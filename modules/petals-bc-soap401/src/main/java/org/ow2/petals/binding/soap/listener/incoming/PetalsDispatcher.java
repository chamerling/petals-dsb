/**
 * PETALS - PETALS Services Platform. Copyright (c) 2006 EBM Websourcing,
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
 * $Id: Axis2BCListener.java 154 19 avr. 2006 wjoseph $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.incoming;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.DeliveryChannel;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.engine.AbstractDispatcher;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.su.AbstractServiceUnitManager;

/**
 * Dispatcher for JBI container. This dispatcher is used to return the service
 * that has been registered on SU deployment.
 * 
 * @version $Rev: 250 $ $Date: 2006-04-21 14:20:57 +0200 (ven, 21 avr 2006) $
 * @since Petals 1.0
 * @author alouis,wjoseph,chamerling - EBM Websourcing
 * 
 */
public class PetalsDispatcher extends AbstractDispatcher implements AddressingConstants {

    public static final String NAME = "PetalsDispatcher";

    private static final long serialVersionUID = 10983L;

    protected AbstractServiceUnitManager bindingSUM;

    protected DeliveryChannel channel;

    protected AbstractComponent component;

    protected AxisOperation jbiOperation;

    protected AxisOperation notifyOperation;

    protected PetalsReceiver petalsReceiver;

    protected AxisService jbiService;

    protected Logger log = null;

    /**
     * Called by Axis Engine to find the operation. TODO : try to retrieve the
     * service description of the JBI service engine and search this description
     * for an operation with the same name
     * 
     * @param service
     *            The service for which we search the operation
     * @param messageContext
     *            Current MessageContext
     * @return Returns an AxisOperation if found in the service description file
     *         or else null.
     * @throws AxisFault
     */
    @Override
    public AxisOperation findOperation(final AxisService service,
            final MessageContext messageContext) throws AxisFault {
        return this.jbiOperation;
    }

    /**
     * Called by Axis Engine to find the service. Asks the JBI container if
     * there is a service registered with the name of the computed called
     * service.
     * 
     * @param messageContext
     *            Current Messagecontext
     * @return Returns an AxisService if found on the JBI container or else
     *         null.
     * @throws AxisFault
     */
    @Override
    public AxisService findService(final MessageContext messageContext) throws AxisFault {
        AxisService axisService = null;
        String serviceName = null;

        // Get Service name using endpoint
        final EndpointReference toEPR = messageContext.getTo();
        this.log.log(Level.FINE,
                "PetalsDispatcher - Checking for Service using target endpoint address : "
                        + toEPR.getAddress());
        final String filePart = toEPR.getAddress();
        serviceName = this.extractWebServiceName(filePart);

        // Retrieve the JBI ServiceEndpoint related to the request serviceName
        // Consumes consumes = bindingSUM.getConsumesFromAddress(serviceName);
        // if (consumes != null) {
        // try to find if the axisService has already been registered
        axisService = messageContext.getConfigurationContext().getAxisConfiguration().getService(
                serviceName);
        // }

        if (axisService == null) {
            throw new AxisFault("Service '" + serviceName + "' not found in SOAPBC context");
        }

        return axisService;
    }

    /**
     * Init the PetalsDispatcher after it has been created by Axis.
     * 
     * @param componentContext
     * @param channel
     * @param bindingSUM
     * @param log
     */
    public void init(final AbstractComponent component, final DeliveryChannel channel,
            final AbstractServiceUnitManager bindingSUM, final PetalsReceiver petalsReceiver,
            final Logger log) {
        this.bindingSUM = bindingSUM;
        this.component = component;
        this.log = log;
        this.channel = channel;
        this.jbiService = new AxisService();
        this.petalsReceiver = petalsReceiver;

        // create default operations
        this.jbiOperation = new InOutAxisOperation(PetalsAxisService.GENERIC_OPERATION_NAME);
        this.jbiOperation.setMessageReceiver(this.petalsReceiver);
        this.jbiService.addOperation(this.jbiOperation);

        // create an operation for the notifications
        this.notifyOperation = new InOutAxisOperation(PetalsAxisService.NOTIFY_OPERATION_NAME);
        this.notifyOperation.setMessageReceiver(this.petalsReceiver);
        this.jbiService.addOperation(this.notifyOperation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.axis2.engine.AbstractDispatcher#initDispatcher()
     */
    @Override
    public void initDispatcher() {
        this.init(new HandlerDescription(NAME));
    }

    /**
     * Parse request URL to catch the service name:
     * ...server/services/[SERVICENAME]?method=...
     * 
     * @param serviceName
     * @return
     */
    protected String extractWebServiceName(final String serviceName) {
        int indexOfOperation = serviceName.indexOf('?');
        indexOfOperation = (indexOfOperation > 0) ? indexOfOperation : serviceName.length();

        int indexOfService = serviceName.lastIndexOf('/');
        indexOfService = (indexOfService > 0) ? indexOfService + 1 : 0;

        return serviceName.substring(indexOfService, indexOfOperation);
    }
}
