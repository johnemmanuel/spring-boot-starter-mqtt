package io.my.spring.mqtt;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 * @author John
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MqttHandler {

    /**
     *
     * @return
     */
    String value() default "";

}

