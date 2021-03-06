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
    /**
     * List of topics in a QName serialized form like : {nsuri}prefix:localPart
     * 
     * @return
     */
    String[] topics() default "";

    String dialect() default "";

    /**
     * Do we need to just get the business message or the complete notification
     * as input Document?
     * 
     * @return
     */
    Mode mode() default Mode.PAYLOAD;

}
