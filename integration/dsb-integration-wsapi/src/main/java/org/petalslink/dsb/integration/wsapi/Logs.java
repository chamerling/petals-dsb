/**
 * 
 */
package org.petalslink.dsb.integration.wsapi;

/**
 * @author chamerling
 * 
 */
public class Logs {

    public static final void KO(String pattern, Object... params) {
        System.out.printf("[KO] " + pattern, params);
        System.out.println();
    }

    public static final void OK(String pattern, Object... params) {
        System.out.printf("[OK] " + pattern, params);
        System.out.println();
    }
    
    public static final void INFO(String pattern, Object... params) {
        System.out.printf("[INFO] " + pattern, params);
        System.out.println();
    }

}
