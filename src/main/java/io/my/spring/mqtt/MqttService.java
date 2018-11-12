package io.my.spring.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

@Slf4j
public class MqttService {

    static class MqttMessageListener implements IMqttMessageListener {

        private Object object;
        private Method method;
        private MqttTopicTemplate template;

        MqttMessageListener(MqttTopicTemplate template, Object object, Method method) {
            this.template = template;
            this.object = object;
            this.method = method;
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Map<String, String> map = template.match(topic);
            Parameter[] parameters = method.getParameters();
            Object[] params = new Object[method.getParameterCount()];
            for (int i=0;i<params.length;i++) {
                String name = parameters[i].getName();
                if (name.equals("topic")) {
                    params[i] = topic;
                } else if (name.equals("message")) {
                    params[i] = message;
                } else {
                    params[i] = map.getOrDefault(parameters[i].getName(), null);
                }
            }
            method.invoke(object, params);
        }
    }

    public MqttService(ApplicationContext context, MqttClient mqttClient) throws MqttException {
        //1. 获取所有MqttController注解的对象
        Map<String, Object> map = context.getBeansWithAnnotation(MqttHandler.class);
        log.info("MqttHandler Found: {}", map.size());
        //2. 得到所有对象的Topic注解的方法
        for (Object object: map.values()) {
            for (Method method: object.getClass().getMethods()) {
                if (method.isAnnotationPresent(MqttTopic.class)) {
                    MqttTopic topic = method.getAnnotation(MqttTopic.class);
                    log.info("Topic Found: {}, Qos: {}", topic.value(), topic.qos());
                    MqttTopicTemplate template = new MqttTopicTemplate(topic.value());
                    mqttClient.subscribe(
                            template.getFilter(),
                            new MqttMessageListener(template, object, method));
                }
            }
        }
    }
}


