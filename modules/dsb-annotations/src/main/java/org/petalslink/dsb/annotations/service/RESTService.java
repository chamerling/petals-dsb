/**
 * 
 */
package org.petalslink.dsb.annotations.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation will be used by the REST service manager to retrieve
 * components in the framework and so to expose them...
 * 
 * @author chamerling
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface RESTService {

}
