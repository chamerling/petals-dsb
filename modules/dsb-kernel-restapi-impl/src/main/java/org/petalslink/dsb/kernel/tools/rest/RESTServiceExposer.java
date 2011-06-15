/**
 * 
 */
package org.petalslink.dsb.kernel.tools.rest;

import java.util.Set;

/**
 * @author chamerling
 *
 */
public interface RESTServiceExposer {
    
    Set<RESTServiceInformationBean> expose(Set<RESTServiceInformationBean> services);

}
