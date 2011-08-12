/**
 * 
 */
package org.petalslink.dsb.annotations.notification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation to be placed on a method of a {@link NotificationConsumer}
 * annotated class. The target method must accept a DOM document as parameter
 * (which is the notification payload) or be empty.
 * 
 * @author chamerling
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Notify {
    String[] topics() default "";

    String dialect() default "";
}
