/**
 * 
 */
package org.petalslink.dsb.jaxws;

/**
 * @author chamerling
 *
 */
public class HelloServiceImpl implements HelloService {

    /* (non-Javadoc)
     * @see org.petalslink.dsb.cxf.HelloService#sayHello(java.lang.String)
     */
    public String sayHello(String in) {
        System.out.println("WS Called");
        return in;
    }

}
