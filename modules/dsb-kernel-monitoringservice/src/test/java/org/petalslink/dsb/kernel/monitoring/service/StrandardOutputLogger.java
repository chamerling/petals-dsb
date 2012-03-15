/**
 * 
 */
package org.petalslink.dsb.kernel.monitoring.service;

import org.ow2.petals.kernel.api.log.Logger;

/**
 * @author chamerling
 * 
 */
public class StrandardOutputLogger implements Logger {

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#call()
     */
    public void call() {
        System.out.println("Call");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#call(java.lang.Object)
     */
    public void call(Object msg) {
        System.out.println("Call :" + msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#start()
     */
    public void start() {
        System.out.println("Start");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#start(java.lang.Object)
     */
    public void start(Object msg) {
        System.out.println("Start : " + msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#end()
     */
    public void end() {
        System.out.println("End");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#end(java.lang.Object)
     */
    public void end(Object msg) {
        System.out.println("End : " + msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#debug(java.lang.Object)
     */
    public void debug(Object message) {
        System.out.println("Debug : " + message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#info(java.lang.Object)
     */
    public void info(Object message) {
        System.out.println("Info : " + message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#info(java.lang.Object,
     * java.lang.Throwable)
     */
    public void info(Object message, Throwable error) {
        System.out.println("Info : " + message);
        error.printStackTrace(System.out);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#warning(java.lang.Object)
     */
    public void warning(Object message) {
        System.out.println("Warning : " + message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#warning(java.lang.Object,
     * java.lang.Throwable)
     */
    public void warning(Object message, Throwable throwable) {
        System.out.println("Warning : " + message);
        throwable.printStackTrace(System.out);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#error(java.lang.Object)
     */
    public void error(Object message) {
        System.out.println("Error : " + message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#error(java.lang.Object,
     * java.lang.Throwable)
     */
    public void error(Object message, Throwable throwable) {
        System.out.println("Error : " + message);
        throwable.printStackTrace(System.out);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.kernel.api.log.Logger#getName()
     */
    public String getName() {
        return "foo";
    }

    public java.util.logging.Logger getLogger() {
        // TODO Auto-generated method stub
        return null;
    }

}
