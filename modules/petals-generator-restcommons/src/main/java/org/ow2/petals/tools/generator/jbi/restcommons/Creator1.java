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
package org.ow2.petals.tools.generator.jbi.restcommons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ow2.petals.tools.generator.commons.AbstractCreator;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.XmlElement;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Creator1 extends AbstractCreator implements Constants {

    public static final String CDK_NS_URL = "http://petals.ow2.org/components/extensions/version-5";

    public static final String CDK_NS = "petalsCDK";

    public static final String REST_NS_URL = "http://petals.ow2.org/components/rest/version-1";

    public static final String REST_NS = "rest";

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<XmlElement> createConsumeComponentElements(Map<String, String> suElements) {
        List<XmlElement> componentElementsConsumer = new ArrayList<XmlElement>();
        XmlElement element = new XmlElement();
        element.setName("rest:address");
        element.setValue(suElements.get(REST_ENDPOINT_ADDRESS));
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
        element.setName("rest:address");
        element.setValue(suElements.get(REST_ENDPOINT_ADDRESS));
        componentElementsProvider.add(element);
        return componentElementsProvider;
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
        return REST_NS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getComponentNSURL() {
        return REST_NS_URL;
    }

    /**
     * {@inheritDoc}
     */
    public String getComponentName() {
        return "petals-bc-rest";
    }

    /**
     * {@inheritDoc}
     */
    public String getComponentVersion() {
        return "1.0";
    }

}
