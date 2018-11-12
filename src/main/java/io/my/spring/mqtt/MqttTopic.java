package io.my.spring.mqtt;

import lombok.Getter;

import java.lang.annotation.*;
import java.util.regex.Pattern;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttTopic {
    String value() default "/";
    int qos() default 0;
}


