/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.petalslink.dsb.kernel.pubsub.service.internal.test.MockWithDocument;
import org.petalslink.dsb.kernel.pubsub.service.internal.test.MockWithNParameters;
import org.petalslink.dsb.kernel.pubsub.service.internal.test.MockWithNothing;
import org.petalslink.dsb.kernel.pubsub.service.internal.test.MockWithNotify;
import org.petalslink.dsb.kernel.pubsub.service.internal.test.Report;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class InternalNotificationConsumerWrapperTest extends TestCase {

    static {
        Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
                new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
                new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
                new WsnbModelFactoryImpl());
    }

    public void testInvokeWithDocument() throws Exception {
        NotificationTargetBean bean = new NotificationTargetBean();
        Report report = new Report();

        MockWithDocument mock = new MockWithDocument(report);
        bean.m = mock.getClass().getMethod("mockMe", Document.class);
        bean.target = mock;

        InternalNotificationConsumerWrapper wrapper = new InternalNotificationConsumerWrapper(bean);
        wrapper.notify(getNotify());

        assertTrue(report.ok);
        assertNull(report.e.getMessage(), report.e);
    }

    public void testInvokeWithNothing() throws Exception {
        NotificationTargetBean bean = new NotificationTargetBean();
        Report report = new Report();

        MockWithNothing mock = new MockWithNothing(report);
        bean.m = mock.getClass().getMethod("mockMe", (Class<?>) null);
        bean.target = mock;

        InternalNotificationConsumerWrapper wrapper = new InternalNotificationConsumerWrapper(bean);
        wrapper.notify(getNotify());

        assertTrue(report.ok);
        assertNull(report.e.getMessage(), report.e);
    }

    public void testInvokeWithNotify() throws Exception {
        NotificationTargetBean bean = new NotificationTargetBean();
        Report report = new Report();

        MockWithNotify mock = new MockWithNotify(report);
        bean.m = mock.getClass().getMethod("mockMe", Notify.class);
        bean.target = mock;

        InternalNotificationConsumerWrapper wrapper = new InternalNotificationConsumerWrapper(bean);
        wrapper.notify(getNotify());

        assertTrue(report.ok);
        assertNull(report.e.getMessage(), report.e);
    }

    public void testInvokeWithNParameters() throws Exception {
        NotificationTargetBean bean = new NotificationTargetBean();
        Report report = new Report();

        MockWithNParameters mock = new MockWithNParameters(report);
        bean.m = mock.getClass().getMethod("mockMe", String.class, Document.class);
        bean.target = mock;

        InternalNotificationConsumerWrapper wrapper = new InternalNotificationConsumerWrapper(bean);
        wrapper.notify(getNotify());

        assertTrue(report.ok);
        assertNull(report.e.getMessage(), report.e);
    }

    private Notify getNotify() {
        try {
            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(InternalNotificationConsumerWrapperTest.class
                            .getResourceAsStream("/notify.xml"));
            return RefinedWsnbFactory.getInstance().getWsnbReader().readNotify(doc);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WsnbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
