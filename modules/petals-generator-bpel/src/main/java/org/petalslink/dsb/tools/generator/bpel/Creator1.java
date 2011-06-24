/**
 * 
 */
package org.petalslink.dsb.tools.generator.bpel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ow2.petals.tools.generator.commons.AbstractCreator;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.XmlElement;

/**
 * @author chamerling
 * 
 */
public class Creator1 extends AbstractCreator implements Constants {
    public static final String CDK_NS_URL = "http://petals.ow2.org/components/extensions/version-5";

    public static final String CDK_NS = "petalsCDK";

    public static final String BPEL_NS_URL = "http://petals.ow2.org/components/bpel/version-1.0";

    public static final String BPEL_NS = "bpel";

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.Creator#getComponentName()
     */
    public String getComponentName() {
        return "petals-se-bpel";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.Creator#getComponentVersion()
     */
    public String getComponentVersion() {
        return "1.0";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.AbstractCreator#
     * createConsumeComponentElements(java.util.Map)
     */
    @Override
    protected List<XmlElement> createConsumeComponentElements(Map<String, String> map) {
        // consumes nothing...
        return new ArrayList<XmlElement>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.AbstractCreator#
     * createProvideComponentElements(java.util.Map)
     */
    @Override
    protected List<XmlElement> createProvideComponentElements(Map<String, String> suElements) {
        List<XmlElement> result = new ArrayList<XmlElement>();
        XmlElement element = new XmlElement();

        if (suElements.get(Constants.BPEL_FILE) != null) {
            element = new XmlElement();
            element.setName(BPEL_NS + ":bpel");
            element.setValue(suElements.get(Constants.BPEL_FILE));
            result.add(element);
        }
        
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.AbstractCreator#getCDKNS()
     */
    @Override
    protected String getCDKNS() {
        return CDK_NS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.tools.generator.commons.AbstractCreator#getCDKNSURL()
     */
    @Override
    protected String getCDKNSURL() {
        return CDK_NS_URL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.generator.commons.AbstractCreator#getComponentNS()
     */
    @Override
    protected String getComponentNS() {
        return BPEL_NS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.tools.generator.commons.AbstractCreator#getComponentNSURL
     * ()
     */
    @Override
    protected String getComponentNSURL() {
        return BPEL_NS_URL;
    }

}
