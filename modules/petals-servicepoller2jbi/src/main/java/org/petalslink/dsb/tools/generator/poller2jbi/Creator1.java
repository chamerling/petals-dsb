/**
 * 
 */
package org.petalslink.dsb.tools.generator.poller2jbi;

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

    public static final String COMPONENT_NS_URL = "http://petals.ow2.org/components/servicepoller/version-1";

    public static final String COMPONENT_NS = "servicepoller";

    public String getComponentName() {
        return "petals-se-servicepoller";
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
