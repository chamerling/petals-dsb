package org.petalslink.dsb.wsn;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import junit.framework.TestCase;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.dsb.wsn.api.NotificationProducerServiceStr;
import org.petalslink.dsb.wsn.cxf.CustomInInterceptor;
import org.petalslink.dsb.wsn.cxf.NotificationProducerServiceClientStrImpl;
import org.petalslink.dsb.wsn.cxf.WebServiceNotificationTransporterStr;
import org.petalslink.dsb.wsn.service.NotificationConsumerServiceServiceStr;
import org.petalslink.dsb.wsn.service.NotificationProducerServiceServiceStr;
import org.petalslink.dsb.wsn.utils.Adapters;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.addressing.datatypes.api.refinedabstraction.RefinedWsaFactory;
import com.ebmwebsourcing.wsstar.addressing.datatypes.api.utils.WsaException;
import com.ebmwebsourcing.wsstar.addressing.datatypes.impl.impl.WsaModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.FilterType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.TopicExpressionType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.common.utils.WsstarCommonUtils;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.WsrfrpConstants;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.abstraction.TopicNamespaceType;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.refinedabstraction.RefinedWstopFactory;
import com.ebmwebsourcing.wsstar.topics.datatypes.api.utils.WstopException;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsnb.services.transport.ITransporterForWsnbPublisher;

public class ClientServerStringTest extends TestCase {

    public void testSubscribe() throws WsnbException, WstopException, URISyntaxException,
            InterruptedException {
        String notificationConsumerAddress = "http://localhost:8787/dsb/wsn/NotificationConsumerService";
        String notificationConsumerAddress2 = "http://localhost:8787/dsb/wsn/NotificationConsumerService2";

        Logger logger = Logger.getLogger(ClientServerStringTest.class.getCanonicalName());

        // A. init services
        Wsnb4ServUtils.initModelFactories(new WsaModelFactoryImpl(), new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());

        String producerAddress = "http://localhost:8989/dsb/wsn/NotificationProducer";

        NotificationProducerServiceStr client = new NotificationProducerServiceClientStrImpl(
                producerAddress);

        // B. create the service which will receive notifications
        INotificationConsumer notificationConsumer = new INotificationConsumer() {
            public void notify(Notify request) throws WsnbException {
                System.out.println("Got a notify message on final subscriber : ");
                System.out.println("--> " + request);
            }
        };
        NotificationConsumerServiceServiceStr consumerService = new NotificationConsumerServiceServiceStr(
                notificationConsumer);
        JaxWsServerFactoryBean serviceFactory = new JaxWsServerFactoryBean();
        // serviceFactory.setDataBinding(new JAXBDataBinding(new
        // WSNContext(WsnbJAXBContext.getInstance())));
        serviceFactory.setAddress(notificationConsumerAddress);
        serviceFactory.setServiceBean(consumerService);
        serviceFactory
                .setServiceClass(org.petalslink.dsb.wsn.api.NotificationConsumerServiceStr.class);
        Server consumerServiceServer = serviceFactory.create();
        
        serviceFactory = new JaxWsServerFactoryBean();
        // serviceFactory.setDataBinding(new JAXBDataBinding(new
        // WSNContext(WsnbJAXBContext.getInstance())));
        serviceFactory.setAddress(notificationConsumerAddress2);
        serviceFactory.setServiceBean(consumerService);
        serviceFactory
                .setServiceClass(org.petalslink.dsb.wsn.api.NotificationConsumerServiceStr.class);
        Server consumerServiceServer2 = serviceFactory.create();

        // C. Create core service which will manage notifications and
        // subscriptions
        // + TODO : Create topic set from Java API and not from Stream... Here
        // it is just for testing purposes...
        InputStream supportedTopicsConfig = ClientServerStringTest.class
                .getResourceAsStream("/SupportedTopicsSet.xml");
        String nsPrefix = "wsn-prod";
        TopicNamespaceType topicNS = RefinedWstopFactory.getInstance().createTopicNamespaceType(
                new URI("http://com.ebmwebsourcing.com/wsn/producer-sample"));
        topicNS.setFinal(true);
        topicNS.setName("WsnProducerTopicNamespace");
        TopicNamespaceType.Topic topicForTopicSetChange = RefinedWstopFactory.getInstance()
                .createTopicNamespaceTypeTopic("TopicSet");
        topicForTopicSetChange.setFinal(true);
        topicForTopicSetChange
                .addMessageType(WsrfrpConstants.RESOURCE_PROPERTY_VALUE_CHANGE_NOTIFICATION_QNAME);
        topicNS.addTopic(topicForTopicSetChange);
        String persistanceFolder = null;
        ITransporterForWsnbPublisher transporter = new WebServiceNotificationTransporterStr();
        org.petalslink.dsb.wsn.impl.NotificationProducerService coreProducerService = new org.petalslink.dsb.wsn.impl.NotificationProducerService(
                logger, supportedTopicsConfig, true, topicNS, nsPrefix, persistanceFolder,
                transporter);
        NotificationProducerServiceServiceStr serviceBean = new NotificationProducerServiceServiceStr(
                coreProducerService);

        // + CXF server...
        serviceFactory = new JaxWsServerFactoryBean();
        serviceFactory.setAddress(producerAddress);
        serviceFactory.setServiceBean(serviceBean);
        serviceFactory.setServiceClass(NotificationProducerServiceStr.class);
        serviceFactory.getInInterceptors().add(
                new LoggingInInterceptor(new PrintWriter(System.out)));
        serviceFactory.getOutInterceptors().add(
                new LoggingOutInterceptor(new PrintWriter(System.out)));
        serviceFactory.getInInterceptors()
                .add(new CustomInInterceptor(new PrintWriter(System.out)));
        Server notificationProducerServiceServer = serviceFactory.create();

        // Thread.sleep(100000L);

        // invoke
        // create client to subscribe to notifier

        // load the subscribe from XML file to go quickly...

        String request = createSubscribe(notificationConsumerAddress);
        String response = client.subscribe(request);
        System.out.println("SUBSCRIBE RESPONSE #1 = " + response);
        
        request = createSubscribe(notificationConsumerAddress2);
        response = client.subscribe(request);
        System.out.println("SUBSCRIBE RESPONSE #2 = " + response);

        System.out.println("LET'S NOTIFY...");
        Document notify = Adapters.fromStreamToDocument(this.getClass().getResourceAsStream(
                "/Notify.xml"));
        // System.out.println(" *** xml file (request) imported :\n" +
        // WsnUtils.prettyPrint(request) + "\n");
        Notify reqObj = Wsnb4ServUtils.getWsnbReader().readNotify(notify);
        // this.properties.put(this.NOTIFY_TO_FORWARD,this.wsnbConsumerService.notify(reqObj));
        coreProducerService.notifyNewSituation(reqObj);

        // stop all the servers
        // TODO

    }

    private String createSubscribe(String address) {
        String result = null;

        try {
            com.ebmwebsourcing.wsstar.addressing.datatypes.api.abstraction.EndpointReferenceType consumerRef = RefinedWsaFactory
                    .getInstance().createEndpointReferenceType(new URI(address));
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
            // topicExpression.setContent("rootTopic2/*/childChildTopic1[@wstop:topic='true']");
            topicExpression.setContent("tns2:rootTopic2/*/childChildTopic1[@wstop:topic='true']");

            filter.addTopicExpression(topicExpression);
            subscribe.setFilter(filter);
            subscribe.setInitialTerminationTime(DatatypeFactory.newInstance().newDuration(10000L));
            System.out.println("SUBSCRIBE on CLIENT : ");
            Document doc = Wsnb4ServUtils.getWsnbWriter().writeSubscribeAsDOM(subscribe);
            result = WsstarCommonUtils.prettyPrint(doc);
            System.out.println(result);
            System.out.println("/SUBSCRIBE on CLIENT");

        } catch (WsaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

}
