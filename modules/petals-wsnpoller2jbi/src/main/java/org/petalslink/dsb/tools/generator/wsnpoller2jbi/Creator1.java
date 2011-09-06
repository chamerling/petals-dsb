/**
 * 
 */
package org.petalslink.dsb.tools.generator.wsnpoller2jbi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ow2.petals.tools.generator.commons.AbstractCreator;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.XmlElement;

/**
 * @author chamerling
 * 
 */
public class Creator1 extends AbstractCreator {

    public static final String CDK_NS_URL = "http://petals.ow2.org/components/extensions/version-5";

    public static final String CDK_NS = "petalsCDK";

    public static final String COMPONENT_NS_URL = "http://petals.ow2.org/components/wsnpoller/version-1";

    public static final String COMPONENT_NS = "wsnpoller";

    public String getComponentName() {
        return "dsb-wsnpoller-jbise";
    }

    public String getComponentVersion() {
        return "1.0";
    }

    @Override
    protected String getCDKNSURL() {
        return CDK_NS_URL;
    }

    @Override
    protected String getCDKNS() {
        return CDK_NS;
    }

    @Override
    protected String getComponentNS() {
        return COMPONENT_NS;
    }

    @Override
    protected String getComponentNSURL() {
        return COMPONENT_NS_URL;
    }

    @Override
    protected List<XmlElement> createConsumeComponentElements(Map<String, String> suElements) {
        List<XmlElement> componentElementsConsumer = new ArrayList<XmlElement>();
        XmlElement element = new XmlElement();

        if (suElements.get(Constants.RESPONSE_INTERFACE) != null) {
            element.setName(COMPONENT_NS + ":interface");
            element.setValue(suElements.get(Constants.RESPONSE_INTERFACE));
            componentElementsConsumer.add(element);
        }

        if (suElements.get(Constants.RESPONSE_ENDPOINT) != null) {
            element = new XmlElement();
            element.setName(COMPONENT_NS + ":endpoint");
            element.setValue(suElements.get(Constants.RESPONSE_ENDPOINT));
            componentElementsConsumer.add(element);
        }

        if (suElements.get(Constants.RESPONSE_SERVICE) != null) {
            element = new XmlElement();
            element.setName(COMPONENT_NS + ":service");
            element.setValue(suElements.get(Constants.RESPONSE_SERVICE));
            componentElementsConsumer.add(element);
        }

        if (suElements.get(Constants.RESPONSE_OPERATION) != null) {
            element = new XmlElement();
            element.setName(COMPONENT_NS + ":operation");
            element.setValue(suElements.get(Constants.RESPONSE_OPERATION));
            componentElementsConsumer.add(element);
        }

        if (suElements.get(Constants.INPUT_FILE) != null) {
            element = new XmlElement();
            element.setName(COMPONENT_NS + ":inputFile");
            element.setValue(suElements.get(Constants.INPUT_FILE));
            componentElementsConsumer.add(element);
        }

        if (suElements.get(Constants.TOPIC_NAME) != null) {
            element = new XmlElement();
            element.setName(COMPONENT_NS + ":" + Constants.TOPIC_NAME);
            element.setValue(suElements.get(Constants.TOPIC_NAME));
            componentElementsConsumer.add(element);
        }
        
        if (suElements.get(Constants.TOPIC_PREFIX) != null) {
            element = new XmlElement();
            element.setName(COMPONENT_NS + ":" + Constants.TOPIC_PREFIX);
            element.setValue(suElements.get(Constants.TOPIC_PREFIX));
            componentElementsConsumer.add(element);
        }
        
        if (suElements.get(Constants.TOPIC_URI) != null) {
            element = new XmlElement();
            element.setName(COMPONENT_NS + ":" + Constants.TOPIC_URI);
            element.setValue(suElements.get(Constants.TOPIC_URI));
            componentElementsConsumer.add(element);
        }

        element = new XmlElement();
        element.setName(COMPONENT_NS + ":cron");
        element.setValue(suElements.get(Constants.CRON_EXPRESSION));
        componentElementsConsumer.add(element);

        return componentElementsConsumer;
    }

    @Override
    protected List<XmlElement> createProvideComponentElements(Map<String, String> suElements) {
        // No provide mode
        return null;
    }

}
