package io.my.spring.mqtt;

import java.lang.annotation.*;

/**
 *
 * @author John
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttTopic {

    /**
     *
     * @return
     */
    String value() default "/";

    /**
     *
     * @return
     */
    int qos() default 0;
}
