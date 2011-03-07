/*
 * PETALS: PETALS Services Platform
 * Copyright (C) 2007 EBM WebSourcing
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA.
 *
 * Initial developer(s): Adrien LOUIS
 * --------------------------------------------------------------------------
 * $Id:
 * --------------------------------------------------------------------------
 */

package org.ow2.petals.monitoring.router;

import java.net.URI;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange.Role;
import javax.xml.namespace.QName;

/**
 * @version $Rev: 617 $ $Date: 2006-06-19 17:28:41 +0200 (lun, 19 jun 2006) $
 * @since Petals 1.1
 * @author alouis
 *
 * This class represent the state of an exchange at a moment
 */
public class ExchangeStateCapture {

    public long time;

    public String messageType;

    public ExchangeStatus status;

    public String component;

    public String container;

    public Role role;

    public String content;

    public QName serviceName;

    public String endpointName;

    public QName operation;

    public URI mep;

    public Exception exception;

}
