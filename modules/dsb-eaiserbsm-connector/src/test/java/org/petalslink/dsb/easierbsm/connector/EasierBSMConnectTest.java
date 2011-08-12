/**
 * 
 */
package org.petalslink.dsb.easierbsm.connector;

import junit.framework.TestCase;

/**
 * Test the connection with EasierBSM by sending raw reports
 * 
 * @author chamerling
 * 
 */
public class EasierBSMConnectTest extends TestCase {

    public void testSendReport() throws Exception {
//        WSDMAdminClient wsdmAdminClient = new WSDMAdminClientImpl("http://localhost:8085/services/adminExternalEndpoint");
//        int functionalSoapNodePort = 8084;
//
//        Node monitoringBus = null;
//        try {
//            Configuration conf = new ConfigurationImpl(false, "localhost", 9100, new SoapServer(
//                    new SoapServerConfig(8085)));
//            conf.addProperty(Constants.EASYESB_CONNEXION_PROPERTY, "http://localhost:"
//                    + functionalSoapNodePort + "/services/adminExternalEndpoint");
//
//            monitoringBus = this.createMonitoringNode(new QName("http://petals.ow2.org",
//                    "MonitoringBus"), conf);
//            AdminClient adminClient = new AdminClientImpl("http://localhost:"
//                    + functionalSoapNodePort + "/services/adminExternalEndpoint");
//            Holder<String> soapAddress = new Holder<String>(
//                    "http://localhost:9301/StockQuoteEndpoint");
//            adminClient.wrapSoapEndpoint(soapAddress, new URL(
//                    "http://localhost:9301/StockQuoteEndpoint?wsdl").toString(), null);
//
//            // wait the synchronization between monitoring bus and functional
//            // bus
//            List<MonitoringEndpointType> res = wsdmAdminClient.getAllMonitoringEndpoints();
//            System.out.println("getAllMonitoringEndpoints response: " + res);
//            while (res != null && res.size() == 0) {
//                System.out.println("getAllMonitoringEndpoints response: " + res);
//                res = wsdmAdminClient.getAllMonitoringEndpoints();
//            }
//
//            Assert.assertNotNull(res);
//            Assert.assertEquals(1, res.size());
//            Assert.assertEquals(MonitoringEndpointType.class.getName(), res.get(0).getClass()
//                    .getName());
//            MonitoringEndpointType met = (MonitoringEndpointType) res.get(0);
//            Assert.assertEquals("StockQuoteSoap_WSDMMonitoring", met.getName().getLocalPart());
//
//            Thread.sleep(15000);
//
//        } finally {
//            if (monitoringBus != null) {
//                monitoringBus.stop();
//            }
//        }

    }

    // private Node createMonitoringNode(QName name, Configuration conf) throws
    // ESBException {
    // ESBFactory factory = new ESBWSDMFactoryImpl();
    // Node node = factory.createNode(name, conf);
    // return node;
    // }

}
