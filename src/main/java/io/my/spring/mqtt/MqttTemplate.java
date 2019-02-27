package io.my.spring.mqtt;

import lombok.Getter;

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

/**
 *
 * @author John
 */

public class MqttTemplate {

    private static final Pattern PATTERN_PARAMETER = Pattern.compile("\\{(.+?)\\}");
    private static final Pattern PATTERN_PATH = Pattern.compile("<(.+?)>$");

    @Getter
    private final String template;
    @Getter
    private String filter;
    @Getter
    private boolean valid = false;
    private String pathParameter = null;
    private Pattern matchPattern;
    private final List<String> SINGLE_PARAMETERS = new ArrayList<>();

    /**
     *
     * @param template
     */
    public MqttTemplate(String template) {
        this.template = template;
        buildFilter();
        buildMatchPattern();
        validateFilter();
    }

    /**
     *
     * @param topic
     * @return
     */
    public Map<String, String> match(String topic) {
        Matcher matcher = matchPattern.matcher(topic);
        if (matcher.matches()) {
            Map<String, String> result = new HashMap<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println(matcher.group(i));
                if (i - 1 < SINGLE_PARAMETERS.size()) {
                    result.put(SINGLE_PARAMETERS.get(i - 1), matcher.group(i));
                } else {
                    result.put(pathParameter, matcher.group(i));
                }
            }
            return result;
        } else {
            return null;
        }
    }

    private void validateFilter() {
        //@TODO Validate filter
        valid = true;
    }

    private void buildFilter() {
        Matcher paramMatcher = PATTERN_PARAMETER.matcher(template);
        StringBuffer filterBuffer = new StringBuffer();
        while (paramMatcher.find()) {
            paramMatcher.appendReplacement(filterBuffer, "+");
            SINGLE_PARAMETERS.add(paramMatcher.group(1));
        }

        paramMatcher.appendTail(filterBuffer);
        Matcher pathMatcher = PATTERN_PATH.matcher(filterBuffer.toString());

        filterBuffer = new StringBuffer();
        while (pathMatcher.find()) {
            pathMatcher.appendReplacement(filterBuffer, "#");
            pathParameter = pathMatcher.group(1);
        }
        pathMatcher.appendTail(filterBuffer);
        filter = filterBuffer.toString();
    }

    private void buildMatchPattern() {
        Matcher paramMatcher = PATTERN_PARAMETER.matcher(template);
        StringBuffer sbPattern = new StringBuffer();
        while (paramMatcher.find()) {
            paramMatcher.appendReplacement(sbPattern, "(.+?)");
        }

        paramMatcher.appendTail(sbPattern);
        Matcher pathMatcher = PATTERN_PATH.matcher(sbPattern.toString());
        sbPattern = new StringBuffer();
        while (pathMatcher.find()) {
            pathMatcher.appendReplacement(sbPattern, "(.+?)");
        }
        pathMatcher.appendTail(sbPattern);
        matchPattern = Pattern.compile(sbPattern.toString());
    }
}
