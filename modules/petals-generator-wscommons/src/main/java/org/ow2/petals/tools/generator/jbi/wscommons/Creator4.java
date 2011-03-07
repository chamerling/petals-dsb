/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.tools.generator.jbi.wscommons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ow2.petals.tools.generator.commons.AbstractCreator;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.XmlElement;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Creator4 extends AbstractCreator implements Constants {
    public static final String CDK_NS_URL = "http://petals.ow2.org/components/extensions/version-5";

    public static final String CDK_NS = "petalsCDK";

    public static final String SOAP_NS_URL = "http://petals.ow2.org/components/soap/version-4";

    public static final String SOAP_NS = "soap";

    /**
     * 
     */
    public Creator4() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCDKNS() {
        return CDK_NS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCDKNSURL() {
        return CDK_NS_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getComponentNS() {
        return SOAP_NS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getComponentNSURL() {
        return SOAP_NS_URL;
    }

    /**
     * {@inheritDoc}
     */
    public String getComponentName() {
        return "petals-bc-soap";
    }

    /**
     * {@inheritDoc}
     */
    public String getComponentVersion() {
        return "4.0";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<XmlElement> createConsumeComponentElements(Map<String, String> suElements) {
        List<XmlElement> componentElementsConsumer = new ArrayList<XmlElement>();
        XmlElement element = new XmlElement();
        element.setName("soap:address");
        element.setValue(suElements.get(SOAP_ENDPOINT_ADDRESS));
        componentElementsConsumer.add(element);

        element = new XmlElement();
        element.setName("soap:remove-root");
        element.setValue("false");
        componentElementsConsumer.add(element);

        element = new XmlElement();
        element.setName("soap:mode");
        element.setValue("SOAP");
        componentElementsConsumer.add(element);
        return componentElementsConsumer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<XmlElement> createProvideComponentElements(Map<String, String> suElements) {
        List<XmlElement> componentElementsProvider = new ArrayList<XmlElement>();

        XmlElement element = new XmlElement();
        element.setName("soap:address");
        element.setValue(suElements.get(SOAP_ENDPOINT_ADDRESS));
        componentElementsProvider.add(element);

        element = new XmlElement();
        element.setName("soap:soap-version");
        String version = "11";
        element.setValue(version);
        componentElementsProvider.add(element);

        element = new XmlElement();
        element.setName("soap:add-root");
        element.setValue("false");
        componentElementsProvider.add(element);

        element = new XmlElement();
        element.setName("soap:chunked-mode");
        element.setValue("false");
        componentElementsProvider.add(element);

        element = new XmlElement();
        element.setName("soap:cleanup-transport");
        element.setValue("true");
        componentElementsProvider.add(element);

        element = new XmlElement();
        element.setName("soap:mode");
        element.setValue("SOAP");
        componentElementsProvider.add(element);

        return componentElementsProvider;
    }

}
