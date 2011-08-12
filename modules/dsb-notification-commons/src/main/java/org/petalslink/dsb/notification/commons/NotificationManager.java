/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.api.utils.SOAUtil;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.WstopConstants;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicNamespaceType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicSetType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.refinedabstraction.RefinedWstopFactory;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.utils.WstopException;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.TopicTypeImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.NotificationProducerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.engines.SubscriptionManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.topic.TopicsManagerEngine;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.topic.WstConstants;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class NotificationManager {

    public static final String EBM_RESOURCEIDS_PREFIX = "ebm";

    public static final String EBM_RESOURCEIDS_NAMESPACE_URI = "http://petals.ow2.org/ebmwebsourcing/specific/ResourceIds";

    private static final String EBM_TOPICS_EXTENSION_NAMESPACE_URI = "http://org.ow2.petals/ebmwebsourcing/specific/topicExtension";

    public static final QName SUPPORTED_QNAME_ATTR = new QName(EBM_TOPICS_EXTENSION_NAMESPACE_URI,
            "supported", EBM_RESOURCEIDS_PREFIX);

    private static Logger logger = Logger.getLogger(NotificationManager.class.getName());

    private TopicNamespaceType topicNamespace;

    private TopicSetType topicSet;

    private TopicsManagerEngine topicsManagerEngine;

    private SubscriptionManagerEngine subscriptionManagerEngine;

    private NotificationProducerEngine notificationProducerEngine;

    private QName serviceName;

    private QName interfaceName;

    private String endpointName;

    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    /**
	 * 
	 */
    public NotificationManager(URL topicNamespaces, List<String> supportedTopics,
            QName serviceName, QName interfaceName, String endpointName) {
        this.endpointName = endpointName;
        this.serviceName = serviceName;
        this.interfaceName = interfaceName;

        Document docTopicNs = null;
        try {
            docTopicNs = SOAUtil.getInstance().getDocumentBuilderFactory().newDocumentBuilder()
                    .parse(topicNamespaces.openStream());

            this.topicNamespace = RefinedWstopFactory.getInstance().getWstopReader()
                    .readTopicNamespaceType(docTopicNs);
            this.topicSet = this.createTopicSetFromTopicNamespace(topicNamespace, supportedTopics);

            final org.w3c.dom.Document topicSetDom = RefinedWstopFactory.getInstance()
                    .getWstopWriter().writeTopicSetTypeAsDOM(topicSet);

            System.out.println("topicSetDom = " + XMLPrettyPrinter.prettyPrint(topicSetDom));

        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WstopException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotificationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.topicsManagerEngine = new TopicsManagerEngine();
        this.subscriptionManagerEngine = new SubscriptionManagerEngine(logger);

        this.subscriptionManagerEngine.setSubscriptionsManagerEdp(getEndpointName());
        this.subscriptionManagerEngine.setSubscriptionsManagerInterface(getInterfaceName());
        this.subscriptionManagerEngine.setSubscriptionsManagerService(getServiceName());

        // TODO : move to to a dedicated component?
        try {
            this.notificationProducerEngine = new NotificationProducerEngine(logger,
                    getTopicsManagerEngine(), getSubscriptionManagerEngine(), true, getTopicSet(),
                    getTopicNamespace(), "wsn", null);
        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    private QName getServiceName() {
        return this.serviceName;
    }

    /**
     * @return
     */
    private QName getInterfaceName() {
        return this.interfaceName;
    }

    /**
     * @return
     */
    private String getEndpointName() {
        return this.endpointName;
    }

    public TopicSetType createTopicSetFromTopicNamespace(final TopicNamespaceType topicns,
            final List<String> topics) throws NotificationException {
        TopicSetType res = null;

        TopicNamespaceType topicNS = null;

        org.w3c.dom.Document domDocument = null;
        try {
            domDocument = RefinedWstopFactory.getInstance().getWstopWriter()
                    .writeTopicNamespaceTypeAsDOM(topicns);
        } catch (WstopException e) {
            throw new NotificationException(e);
        }

        // convert dom to jdom
        final DOMBuilder builder = new DOMBuilder();
        final org.jdom.Document jdomDocument = builder.build(domDocument);

        this.addSupportedTopicAttr(jdomDocument.getRootElement().getChildren(), topics);

        // convert jdom to dom
        final DOMOutputter converter = new DOMOutputter();
        try {
            domDocument = converter.output(jdomDocument);
        } catch (final JDOMException e) {
            throw new NotificationException(e);
        }

        // convert dom to topicSet
        try {
            topicNS = RefinedWstopFactory.getInstance().getWstopReader()
                    .readTopicNamespaceType(domDocument);
        } catch (WstopException e) {
            throw new NotificationException(e);
        }

        res = this.createTopicSetFromSupportedTopicNamespace(topicNS);

        return res;
    }

    public TopicSetType createTopicSetFromSupportedTopicNamespace(final TopicNamespaceType topicns)
            throws NotificationException {
        TopicSetType res = null;

        final Namespace tns = Namespace.getNamespace("tns", topicns.getNamespace().toString());
        final org.jdom.Element root = this.createEmptyTopicSet();
        final org.jdom.Document doc = new org.jdom.Document(root);
        this.createTopicSetTree((List) topicns.getTopics(), root, tns, true);

        // convert jdom to dom
        org.w3c.dom.Document domDocument = null;
        final DOMOutputter converter = new DOMOutputter();
        try {
            domDocument = converter.output(doc);
        } catch (final JDOMException e) {
            throw new NotificationException(e);
        }

        // convert dom to topicSet
        try {
            res = RefinedWstopFactory.getInstance().getWstopReader().readTopicSetType(domDocument);
        } catch (WstopException e) {
            throw new NotificationException(e);
        }

        return res;
    }

    private org.jdom.Element createEmptyTopicSet() {
        final Namespace wstop = Namespace.getNamespace(WstConstants.PREFIX,
                WstConstants.NAMESPACE_URI);
        final Namespace xsi = Namespace.getNamespace(WstConstants.XML_SCHEMA_PREFIX,
                WstConstants.XML_SCHEMA_NAMESPACE);

        final org.jdom.Element root = new org.jdom.Element(
                WstConstants.TOPIC_SET_QNAME.getLocalPart(), wstop);
        root.addNamespaceDeclaration(xsi);
        root.setAttribute("schemaLocation",
                "http://docs.oasis-open.org/wsn/t-1 http://docs.oasis-open.org/wsn/t-1.xsd", xsi);
        return root;
    }

    private void addSupportedTopicAttr(final List<org.jdom.Element> children,
            final List<String> topics) {
        for (final org.jdom.Element child : children) {
            if (child.getName().equals(WstopConstants.TOPIC_QNAME.getLocalPart())
                    && child.getNamespaceURI().equals(WstopConstants.TOPIC_QNAME.getNamespaceURI())) {
                if (topics.contains(child.getAttribute("name").getValue())) {
                    child.setAttribute(SUPPORTED_QNAME_ATTR.getLocalPart(), "true", Namespace
                            .getNamespace(SUPPORTED_QNAME_ATTR.getPrefix(),
                                    SUPPORTED_QNAME_ATTR.getNamespaceURI()));
                }
                if (child.getChildren() != null && child.getChildren().size() > 0) {
                    this.addSupportedTopicAttr(child.getChildren(), topics);
                }
            }
        }
    }

    private void createTopicSetTree(final List<TopicType> topics, final org.jdom.Element root,
            final Namespace tns, final boolean first) {

        final Namespace wstop = Namespace.getNamespace("wstop",
                "http://docs.oasis-open.org/wsn/t-1");

        for (final TopicType topic : topics) {
            org.jdom.Element childTopic = null;
            if (first) {
                childTopic = new org.jdom.Element(topic.getName(), tns);
            } else {
                childTopic = new org.jdom.Element(topic.getName());
            }
            if (isTopicSupported(topic) != null && isTopicSupported(topic) == true) {
                childTopic.setAttribute("topic", "true", wstop);
                root.addContent(childTopic);
            }
            if (topic.getTopics() != null && topic.getTopics().size() > 0) {
                this.createTopicSetTree(topic.getTopics(), childTopic, tns, false);
            }
        }
    }

    public static Boolean isTopicSupported(TopicType topic) {
        Boolean res = null;
        com.ebmwebsourcing.wsstar.jaxb.notification.topics.TopicType model = TopicTypeImpl
                .toJaxbModel(topic);
        final String value = model.getOtherAttributes().get(SUPPORTED_QNAME_ATTR);
        if (value != null) {
            res = Boolean.valueOf(value);
        }
        return res;
    }

    /**
     * @return the topicNamespace
     */
    public TopicNamespaceType getTopicNamespace() {
        return topicNamespace;
    }

    /**
     * @return the topicSet
     */
    public TopicSetType getTopicSet() {
        return topicSet;
    }

    /**
     * @return the topicsManagerEngine
     */
    public TopicsManagerEngine getTopicsManagerEngine() {
        return topicsManagerEngine;
    }

    /**
     * @return the subscriptionManagerEngine
     */
    public SubscriptionManagerEngine getSubscriptionManagerEngine() {
        return subscriptionManagerEngine;
    }

    /**
     * @return the notificationProducerEngine
     */
    public NotificationProducerEngine getNotificationProducerEngine() {
        return notificationProducerEngine;
    }

}
