package io.my.spring.mqtt;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttTopic {
    String value() default "/";
    int qos() default 0;
}
