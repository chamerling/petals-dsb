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

import com.ebmwebsourcing.commons.jbi.sugenerator.beans.SuBean;
import com.ebmwebsourcing.commons.jbi.sugenerator.beans.XmlElement;
import com.ebmwebsourcing.commons.jbi.sugenerator.cdk.BasicBuilderForCdkElements;
import com.ebmwebsourcing.commons.jbi.sugenerator.cdk.SuMode;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Creator3 {
	public static final String CDK_NS_URL = "http://petals.ow2.org/components/extensions/version-4.0";

	public static final String CDK_NS = "petalsCDK";

	public static final String SOAP_NS_URL = "http://petals.ow2.org/components/soap/version-3.1";

	public static final String SOAP_NS = "soap";

	public static SuBean createSuBeanProvide(String interfaceName,
			String interfaceNameNsUrl, String serviceName,
			String serviceNameNsUrl, String endpointName, String linkType,
			String suType, String componentName, String componentVersion,
			String namespaces, String operation, String timeout,
			List<XmlElement> componentElements, String wsdlFile) {
		SuBean suBean = new SuBean();

		// Basic data.
		suBean.setBc(true);
		suBean.setConsume(false);

		suBean.setInterfaceName(interfaceName);
		suBean.setInterfaceNamespaceUri(interfaceNameNsUrl);
		suBean.setServiceName(serviceName);
		suBean.setServiceNamespaceUri(serviceNameNsUrl);
		suBean.setEndpointName(endpointName);

		suBean.setLinkType(linkType);
		suBean.setSuType(suType);
		suBean.setComponentName(componentName);
		suBean.setComponentVersion(componentVersion);

		// Name spaces.
		suBean.addNamespace(SOAP_NS, SOAP_NS_URL);
		suBean.addNamespace(CDK_NS, CDK_NS_URL);

		// Complex data.
		ArrayList<XmlElement> cdkElements = BasicBuilderForCdkElements
				.getCdkElements30(SuMode.provide, wsdlFile, timeout, operation,
						null);
		suBean.cdkElements.addAll(cdkElements);
		suBean.specificElements.addAll(componentElements);
		return suBean;
	}

	public static SuBean createSUBeanConsume(String interfaceName,
			String interfaceNameNsUrl, String serviceName,
			String serviceNameNsUrl, String endpointName, String linkType,
			String suType, String componentName, String componentVersion,
			String namespaces, String operation, String timeout,
			List<XmlElement> componentElements) {
		SuBean suBean = new SuBean();

		// Basic data.
		suBean.setBc(true);
		suBean.setConsume(true);

		suBean.setInterfaceName(interfaceName);
		suBean.setInterfaceNamespaceUri(interfaceNameNsUrl);
		suBean.setServiceName(serviceName);
		suBean.setServiceNamespaceUri(serviceNameNsUrl);
		suBean.setEndpointName(endpointName);

		suBean.setLinkType(linkType);
		suBean.setSuType(suType);
		suBean.setComponentName(componentName);
		suBean.setComponentVersion(componentVersion);

		// Name spaces.
		suBean.addNamespace(SOAP_NS, SOAP_NS_URL);
		suBean.addNamespace(CDK_NS, CDK_NS_URL);

		// Complex data.
		ArrayList<XmlElement> cdkElements = BasicBuilderForCdkElements
				.getCdkElements30(SuMode.consume, null, timeout, operation,
						"InOut");
		suBean.cdkElements.addAll(cdkElements);
		suBean.specificElements.addAll(componentElements);
		return suBean;
	}
}
