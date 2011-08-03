/**
 * 
 */
package org.petalslink.dsb.annotations.notification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notification to be placed on any Notification consumer at the kernel level.
 * The Notification Center will detect all the kernel technical services which
 * are annotated with this annotation and so will register them as notification
 * consumer for the given topics. For now the technical services may also
 * implement the interface which allow the notification center to send
 * notification to them...
 * 
 * @author chamerling
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface NotificationConsumer {

    String[] topic() default "";

    String dialect() default "";

}
