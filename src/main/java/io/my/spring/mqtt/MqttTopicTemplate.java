package io.my.spring.mqtt;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttSubscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
/topic1/{param1}/{param2} match /topic1/+/+
/topic2/<path1> math /topic2/#
*/
public class MqttTopicTemplate {

    @Getter
    private String template;
    @Getter
    private String filter;
    @Getter
    private boolean isValid;
    @Getter
    private List<String> singleParams = new ArrayList<>();
    @Getter
    private String pathParam = null;
    @Getter
    private Pattern matchPattern;

    public MqttTopicTemplate(String template) {
        this.template = template;
        buildMqttFilter();
        buildMatchPattern();
        checkFilterValid();
    }

    public Map<String, String> match(String topic) {
        Matcher matcher = matchPattern.matcher(topic);
        if (matcher.matches()) {
            Map<String, String> result = new HashMap<>();
            for (int i=1;i<=matcher.groupCount();i++) {
                System.out.println(matcher.group(i));
                if (i-1<singleParams.size()) {
                    result.put(singleParams.get(i-1), matcher.group(i));
                } else {
                    result.put(pathParam, matcher.group(i));
                }
            }
            return result;
        } else {
            return null;
        }
    }

    static private Pattern paramPattern = Pattern.compile("\\{(.+?)\\}");
    static private Pattern pathPattern = Pattern.compile("<(.+?)>$");

    private void checkFilterValid() {
        //TODO add validate code
        isValid = true;
    }

    private void buildMatchPattern() {
        Matcher paramMatcher = paramPattern.matcher(template);
        StringBuffer sbPattern = new StringBuffer();
        while (paramMatcher.find()) {
            paramMatcher.appendReplacement(sbPattern, "(.+?)");
        }
        paramMatcher.appendTail(sbPattern);
        Matcher pathMatcher = pathPattern.matcher(sbPattern.toString());
        sbPattern = new StringBuffer();
        while (pathMatcher.find()) {
            pathMatcher.appendReplacement(sbPattern, "(.+?)");
        }
        pathMatcher.appendTail(sbPattern);
        matchPattern = Pattern.compile(sbPattern.toString());
    }

    private void buildMqttFilter() {
        Matcher paramMatcher = paramPattern.matcher(template);
        StringBuffer sbFilter = new StringBuffer();
        while (paramMatcher.find()) {
            paramMatcher.appendReplacement(sbFilter, "+");
            singleParams.add(paramMatcher.group(1));
        }
        paramMatcher.appendTail(sbFilter);
        Matcher pathMatcher = pathPattern.matcher(sbFilter.toString());
        sbFilter = new StringBuffer();
        while (pathMatcher.find()) {
            pathMatcher.appendReplacement(sbFilter, "#");
            pathParam = pathMatcher.group(1);
        }
        pathMatcher.appendTail(sbFilter);
        filter = sbFilter.toString();
    }
}
