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
 * $Id: Router.java,v 1.2 2006/03/17 10:24:27 alouis Exp $
 * -------------------------------------------------------------------------
 */
package org.petalslink.dsb.kernel.monitoring.router;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.petalslink.dsb.kernel.monitoring.router.MonitoringModuleImpl.ExchangeContext;


/**
 * @author aruffie - EBM WebSourcing
 *
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = MonitoringStorageService.class))
public class MonitoringStorageServiceImpl implements MonitoringStorageService{

    private final Map<String, ExchangeContext> storage = Collections.synchronizedMap(new HashMap<String,ExchangeContext>());

    public Map<String, ExchangeContext> getStorage() {
        return this.storage;
    }
    
}
