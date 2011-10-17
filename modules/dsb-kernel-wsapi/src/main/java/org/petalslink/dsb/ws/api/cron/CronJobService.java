/**
 * 
 */
package org.petalslink.dsb.ws.api.cron;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author chamerling
 *
 */
@WebService
public interface CronJobService {

    @WebMethod
    List<CronJobBean> get();
}
