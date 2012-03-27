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
 * SoapExternalListener.java - Initial Developper : ofabre
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.incoming;

import java.util.logging.Level;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.ow2.petals.binding.soap.SoapComponent;
import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.listener.AbstractExternalListener;

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.SOAP_EXTERNAL_LISTENER_SERVICE_PARAM;

/**
 * The SOAP external listener.
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class SoapExternalListener extends AbstractExternalListener {

    private SoapExternalListenerManager externalListenerManager;

    private String serviceName = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.listener.AbstractListener#init()
     */
    @Override
    public void init() {
        this.externalListenerManager = ((SoapComponent) getComponent())
                .getExternalListenerManager();
        this.serviceName = SUPropertiesHelper.getServiceName(getExtensions());

        // allow address
        if (this.serviceName == null) {
            this.serviceName = SUPropertiesHelper.getAddress(getExtensions());
        }
    }

    /**
     * As there is a single SOAP listener for all incoming soap requests, this
     * method only references the given address. External calls for non
     * registered addresses will be ignored.
     * 
     * @throws PEtALSCDKException
     */
    @Override
    public void start() throws PEtALSCDKException {
        this.getLogger().log(Level.FINE, "Starting listening on " + this.serviceName);
        this.externalListenerManager.getAddresses().add(this.serviceName);

        ConfigurationContext axisConfiguration = ((SoapComponent) getComponent()).getSoapContext()
                .getAxis2ConfigurationContext();
        AxisConfiguration axisConf = axisConfiguration.getAxisConfiguration();

        try {
            AxisService axisService = axisConf.getService(this.serviceName);
            Parameter soapExternalListenerParam = new Parameter(SOAP_EXTERNAL_LISTENER_SERVICE_PARAM, this);
            axisService.addParameter(soapExternalListenerParam);
        } catch (AxisFault af) {
            throw new PEtALSCDKException(af);
        }
    }

    /**
     * Unreference the given address. After address removal, it will be
     * impossible to contact service from outside.
     */
    @Override
    public void stop() {
        this.getLogger().log(Level.FINE, "Stopping listening on " + this.serviceName);
        this.externalListenerManager.getAddresses().remove(this.serviceName);
    }

}
