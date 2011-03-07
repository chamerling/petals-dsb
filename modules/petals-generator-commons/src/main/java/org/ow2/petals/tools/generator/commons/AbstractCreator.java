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
package org.ow2.petals.tools.generator.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.XmlElement;
import com.ebmwebsourcing.commons.jbi.sugenerator.cdk.BasicBuilderForCdkElements;
import com.ebmwebsourcing.commons.jbi.sugenerator.cdk.SuMode;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public abstract class AbstractCreator implements Creator {

    protected abstract String getCDKNSURL();

    protected abstract String getCDKNS();

    protected abstract String getComponentNS();

    protected abstract String getComponentNSURL();

    /**
     * 
     */
    public AbstractCreator() {
    }

    public SuBean createSUConsume(Map<String, String> suElements) {
        SuBean suBean = new SuBean();

        // Basic data.
        suBean.setBc(true);
        suBean.setConsume(true);

        suBean.setInterfaceName(suElements.get(INTERFACE));
        suBean.setInterfaceNamespaceUri(suElements.get(INTERFACE_NS));
        suBean.setServiceName(suElements.get(SERVICE));
        suBean.setServiceNamespaceUri(suElements.get(SERVICE_NS));
        suBean.setEndpointName(suElements.get(ENDPOINT));

        suBean.setLinkType(suElements.get(LINK_TYPE));
        suBean.setSuType(suElements.get(SU_TYPE));
        suBean.setComponentName(this.getComponentName());
        suBean.setComponentVersion(this.getComponentVersion());

        // Name spaces.
        suBean.addNamespace(this.getComponentNS(), this.getComponentNSURL());
        suBean.addNamespace(this.getCDKNS(), this.getCDKNSURL());

        List<XmlElement> componentElements = this.createConsumeComponentElements(suElements);

        // Complex data.
        ArrayList<XmlElement> cdkElements = BasicBuilderForCdkElements.getCdkElements30(
                SuMode.consume, null, suElements.get(TIMEOUT), suElements.get(OPERATION), "InOut");
        suBean.cdkElements.addAll(cdkElements);
        suBean.specificElements.addAll(componentElements);
        return suBean;
    }

    /**
     * @param suElements
     * @return
     */
    protected abstract List<XmlElement> createConsumeComponentElements(
            Map<String, String> suElements);

    public SuBean createSUProvide(Map<String, String> suElements) {
        SuBean suBean = new SuBean();

        // Basic data.
        suBean.setBc(true);
        suBean.setConsume(false);

        suBean.setInterfaceName(suElements.get(INTERFACE));
        suBean.setInterfaceNamespaceUri(suElements.get(INTERFACE_NS));
        suBean.setServiceName(suElements.get(SERVICE));
        suBean.setServiceNamespaceUri(suElements.get(SERVICE_NS));
        suBean.setEndpointName(suElements.get(ENDPOINT));

        suBean.setLinkType(suElements.get(LINK_TYPE));
        suBean.setSuType(suElements.get(SU_TYPE));
        suBean.setComponentName(this.getComponentName());
        suBean.setComponentVersion(this.getComponentVersion());

        // Name spaces.
        suBean.addNamespace(this.getComponentNS(), this.getComponentNSURL());
        suBean.addNamespace(this.getCDKNS(), this.getCDKNSURL());

        List<XmlElement> componentElements = this.createProvideComponentElements(suElements);

        // Complex data.
        ArrayList<XmlElement> cdkElements = BasicBuilderForCdkElements.getCdkElements30(
                SuMode.provide, suElements.get(WSDLFILE), suElements.get(TIMEOUT), suElements
                        .get(OPERATION), null);

        // FIXME = this is temporary since not supported by
        // BasicBuilderForCdkElements
        if (suElements.get(WSDLFILE) == null) {
            XmlElement wsdlElement = new XmlElement();
            wsdlElement.setName("petalsCDK:wsdl");
            wsdlElement.setNillable(true);
            cdkElements.add(wsdlElement);
        }

        suBean.cdkElements.addAll(cdkElements);
        suBean.specificElements.addAll(componentElements);
        return suBean;
    }

    /**
     * @param suElements
     * @return
     */
    protected abstract List<XmlElement> createProvideComponentElements(
            Map<String, String> suElements);

}
