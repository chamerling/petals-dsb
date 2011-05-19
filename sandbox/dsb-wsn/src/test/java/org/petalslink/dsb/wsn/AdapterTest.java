package org.petalslink.dsb.wsn;

import java.net.URI;

import javax.xml.bind.Marshaller;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.addressing.datatypes.api.refinedabstraction.RefinedWsaFactory;
import com.ebmwebsourcing.wsstar.addressing.datatypes.impl.impl.WsaModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.FilterType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.TopicExpressionType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.WsnbJAXBContext;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.SubscribeImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.common.utils.WsstarCommonUtils;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

public class AdapterTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        // A. init services
        Wsnb4ServUtils.initModelFactories(new WsaModelFactoryImpl(), new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    public void testFromModel2JAXB() throws Exception {
        com.ebmwebsourcing.wsstar.addressing.datatypes.api.abstraction.EndpointReferenceType consumerRef = RefinedWsaFactory
                .getInstance().createEndpointReferenceType(new URI("http://petalslink.com/foo"));
        com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribe = RefinedWsnbFactory
                .getInstance().createSubscribe(consumerRef);
        FilterType filter = RefinedWsnbFactory.getInstance().createFilterType();

        // FIXME : Where are the dialect constants???
        TopicExpressionType topicExpression = RefinedWsnbFactory.getInstance()
                .createTopicExpressionType(
                        new URI("http://docs.oasis-open.org/wsn/t-1/TopicExpression/Full"));

        // Est on obligé de mettre le prefix dans le content? ie il faut
        // creer
        // le topic namespace avec le bon prefix, on ne peut pas avoir
        // quelque
        // chose d'automatique?
        topicExpression.addTopicNamespace("tns2", new URI(
                "http://petals.ow2.org/topicNamespace/MyOtherTopicNamespaceSample"));
        topicExpression.setContent("tns2:rootTopic2/*/childChildTopic1[@wstop:topic='true']");
        filter.addTopicExpression(topicExpression);
        subscribe.setFilter(filter);

        Document doc = Wsnb4ServUtils.getWsnbWriter().writeSubscribeAsDOM(subscribe);
        System.out.println(WsstarCommonUtils.prettyPrint(doc));

        // get the JAXB model
        Subscribe jaxb = SubscribeImpl.toJaxbModel(subscribe);
        // print JAXB
        WsnbJAXBContext context = WsnbJAXBContext.getInstance();
        Marshaller marshaller = context.createWSNotificationMarshaller();
        marshaller.marshal(jaxb, System.out);

    }

}
