/**
 * 
 */
package org.petalslink.dsb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chamerling
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LifeCycleListener {

    Phase phase() default Phase.START;

    int priority() default 10;
}
