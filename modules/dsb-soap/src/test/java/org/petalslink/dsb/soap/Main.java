/**
 * 
 */
package org.petalslink.dsb.soap;

import java.util.concurrent.TimeUnit;

import org.petalslink.dsb.cxf.CXFHelper;

/**
 * @author chamerling
 *
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        
        CXFHelper.getServiceFromFinalURL("http://localhost:9998/foo/Service", HelloService.class, new HelloService() {
            public String sayHello() {
                // TODO Auto-generated method stub
                return "hello";
            }
        }).start();
        
        TimeUnit.HOURS.sleep(1);
        
    }

}
