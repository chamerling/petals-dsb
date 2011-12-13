/**
 * 
 */
package org.petalslink.dsb.notification.light;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author chamerling
 *
 */
public class NotificationGenerator {
    
    private static final String TOPIC = "<b:Topic Dialect=\"http://www.w3.org/TR/1999/REC-xpath-19991116\" xmlns:TOPICPREFIX=\"TOPICNS\">TOPICPREFIX:TOPICNAME</b:Topic>";
    
    private static String TEMPLATE;
    
    public static final String generate(String body, Topic topic) {
        String template = getTemplate();
        if (template == null) {
            return null;
        }
        
        if (body != null) {
            template = template.replaceAll("BODY", body);
        } else {
            template = template.replaceAll("BODY", "");
        }
        
        if (topic != null) {
            String temp = TOPIC.replaceAll("TOPICPREFIX", topic.prefix);
            temp = temp.replaceAll("TOPICNS", topic.ns);
            temp = temp.replaceAll("TOPICNAME", topic.name);
            template = template.replaceAll("TOPIC", temp);
        } else {
            template = template.replaceAll("TOPIC", "");
        }
        return template;
    }
    
    private static final synchronized String getTemplate() {
        if (TEMPLATE == null) {
            InputStream is = NotificationGenerator.class.getResourceAsStream("/notify.template");
            if (is == null) {
                return null;
            }
            TEMPLATE = normalize(is);
        }
        return TEMPLATE;
    }
    
    private static String normalize(InputStream instream) {
        BufferedReader in = new BufferedReader(new InputStreamReader(instream));
        StringBuilder result = new StringBuilder();
        String line = null;

        try {
            line = in.readLine();
            while (line != null) {
                String[] tok = line.split("\\s");

                for (int x = 0; x < tok.length; x++) {
                    String token = tok[x];
                    result.append(" " + token);
                }
                line = in.readLine();
            }
        } catch (Exception ex) {
        }
        String rtn = result.toString();
        return rtn;
    }

}
