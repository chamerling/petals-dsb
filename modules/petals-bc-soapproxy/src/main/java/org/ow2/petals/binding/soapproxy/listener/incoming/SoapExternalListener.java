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

package org.ow2.petals.binding.soapproxy.listener.incoming;

import org.ow2.petals.binding.soapproxy.SoapComponent;
import org.ow2.petals.component.framework.listener.AbstractExternalListener;

/**
 * The SOAP external listener. TODO : To be refactored with
 * {@link PetalsReceiver}
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class SoapExternalListener extends AbstractExternalListener {

    private SoapExternalListenerManager externalListenerManager;

    /*
     * (non-Javadoc)
     * @see org.ow2.petals.component.framework.listener.AbstractListener#init()
     */
    @Override
    public void init() {
        this.externalListenerManager = ((SoapComponent) this.getComponent())
                .getExternalListenerManager();
    }

    /**
     * As there is a single SOAP listener for all incoming soap requests, this
     * method only references the given address. External calls for non
     * registered addresses will be ignored.
     */
    @Override
    public void start() {
    }

    /**
     * unreference the given address. After address removal, it will be
     * impossible to contact service from outside.
     */
    @Override
    public void stop() {
    }

}
