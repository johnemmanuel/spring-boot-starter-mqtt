package io.my.spring.mqtt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("com.mqtt")
public class MqttProperties {
    @Getter
    @Setter
    private String broker;
    @Getter
    @Setter
    private String clientId;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private int timeout;
    @Getter
    @Setter
    private int keepalive;
    @Getter
    @Setter
    private boolean cleanSession;
    @Getter
    @Setter
    private boolean autoReconnect;
}
