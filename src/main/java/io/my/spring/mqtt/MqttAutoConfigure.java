package io.my.spring.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MqttService.class)
@EnableConfigurationProperties(MqttProperties.class)
public class MqttAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public MqttClient mqttClient(MqttProperties properties) throws MqttException {
        MqttClient mqttClient = new MqttClient(
                properties.getBroker(),
                properties.getClientId(),
                new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(properties.isCleanSession());
        connOpts.setUserName(properties.getUsername());
        connOpts.setPassword(properties.getPassword().toCharArray());
        connOpts.setConnectionTimeout(properties.getTimeout());
        connOpts.setKeepAliveInterval(properties.getKeepalive());
        connOpts.setAutomaticReconnect(properties.isAutoReconnect());
        mqttClient.connect(connOpts);
        return mqttClient;
    }

    @Bean
    public MqttService mqttService(ApplicationContext context, MqttClient mqttClient) throws MqttException {
        return new MqttService(context, mqttClient);
    }
}
