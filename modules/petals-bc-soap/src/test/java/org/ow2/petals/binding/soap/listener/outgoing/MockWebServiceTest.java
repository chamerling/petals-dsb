/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.outgoing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;
import com.ebmwebsourcing.easycommons.logger.Level;
import com.ebmwebsourcing.easycommons.logger.TestHandler;

/**
 * @author aruffie
 * 
 */
public class MockWebServiceTest {

    private static final TestHandler testHandler = new TestHandler();

    private final static String FIRST_URL = "http://localhost:8085/UnitTest/MockWebService";

    private final static String POST_METHOD = "POST";

    private final static String GET_METHOD = "GET";

    private MockWebService mws;

    static {
    }

    @BeforeClass
    public static void beforeClass() {
        MockWebService.logger.addHandler(testHandler);
    }

    @AfterClass
    public static void afterClass() {
        MockWebService.logger.removeHandler(testHandler);
    }

    @Before
    public void before() {
        try {
            mws = new MockWebService(new URL(FIRST_URL));
        } catch (MalformedURLException mue) {
            throw new UncheckedException(mue);
        }
        mws.start();
    }

    @After
    public void after() {
        if (mws.isStarted()) {
            mws.stop();
        }
        testHandler.clearRecords();
    }

    private static final void assertLogRecordsEquals(List<LogRecord> l1, List<LogRecord> l2) {
        assertEquals(l1.size(), l2.size());
        for (int i = 0; i < l1.size(); ++i) {
            assertEquals(l1.get(i).getLevel(), l2.get(i).getLevel());
            assertEquals(l1.get(i).getMessage(), l2.get(i).getMessage());
            assertArrayEquals(l1.get(i).getParameters(), l2.get(i).getParameters());
        }
    }

    @Test
    public void testIsStarted() throws Exception {
        assertTrue(mws.isStarted());
        LogRecord expectedLogRecord = new LogRecord(Level.INFO, String.format(
                MockWebService.STARTED_MOCK_WEB_SERVICE_LOG_MSG, FIRST_URL));
        assertLogRecordsEquals(Collections.singletonList(expectedLogRecord),
                testHandler.getAllRecords());
    }

    @Test
    public void testStartWhenAlreadyStarted() throws Exception {
        mws.start();
        assertTrue(mws.isStarted());
        // only 1 log trace should be there!
        LogRecord expectedLogRecord = new LogRecord(Level.INFO, String.format(
                MockWebService.STARTED_MOCK_WEB_SERVICE_LOG_MSG, FIRST_URL));
        assertLogRecordsEquals(Collections.singletonList(expectedLogRecord),
                testHandler.getAllRecords());
    }

    @Test
    public void testStop() throws Exception {
        testHandler.clearRecords();
        mws.stop();
        assertFalse(mws.isStarted());
        // only 1 log trace should be there!
        LogRecord expectedLogRecord = new LogRecord(Level.INFO, String.format(
                MockWebService.STOPPED_MOCK_WEB_SERVICE_LOG_MSG, FIRST_URL));
        assertLogRecordsEquals(Collections.singletonList(expectedLogRecord),
                testHandler.getAllRecords());
    }

    private final void testLogOnRequestSent(String method, String url, String expectedInfoLogMessage)
            throws Exception {
        testHandler.clearRecords();
        sendRequest(new URL(url), method);
        LogRecord expectedLogRecord = new LogRecord(Level.INFO, expectedInfoLogMessage);
        assertLogRecordsEquals(Collections.singletonList(expectedLogRecord),
                testHandler.getAllRecords());

    }

    private final void testNoLogOnRequestSent(String method, String url) throws Exception {
        testHandler.clearRecords();
        sendRequest(new URL(url), method);
        assertLogRecordsEquals(Collections.<LogRecord>emptyList(),
                testHandler.getAllRecords());

    }

    @Test
    public void testGetMockStarted() throws Exception {
        testLogOnRequestSent(GET_METHOD, FIRST_URL,
                String.format(MockWebService.RECEIVED_GET_REQUEST_LOG_MSG, FIRST_URL));
    }

    @Test
    public void testPostMockStarted() throws Exception {
        testLogOnRequestSent(POST_METHOD, FIRST_URL,
                String.format(MockWebService.RECEIVED_POST_REQUEST_LOG_MSG, FIRST_URL));

    }

    @Test(expected = ConnectException.class)
    public void testGetMockStopped() throws Exception {
        mws.stop();
        testNoLogOnRequestSent(GET_METHOD, FIRST_URL);
    }

    @Test(expected = ConnectException.class)
    public void testPostMockStopped() throws Exception {
        mws.stop();
        testNoLogOnRequestSent(POST_METHOD, FIRST_URL);
    }

    private String sendRequest(final URL url, final String method) throws IOException {
        BufferedReader in = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            // Get response if one is expected
            InputStream is = (InputStream) conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            final StringBuilder responseBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseBuilder.append(inputLine);
            }
            return responseBuilder.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
