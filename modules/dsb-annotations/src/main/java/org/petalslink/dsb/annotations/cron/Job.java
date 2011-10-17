/**
 * 
 */
package org.petalslink.dsb.annotations.cron;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author chamerling
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Job {

    String id() default "";

    long period() default 60;

    long delay() default 60;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
