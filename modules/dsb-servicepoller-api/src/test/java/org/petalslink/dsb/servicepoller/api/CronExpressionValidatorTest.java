/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import java.text.ParseException;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class CronExpressionValidatorTest extends TestCase {

    public void testEverySecond() {
        try {
            CronExpressionValidator.validateExpression("* * * * * ?");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
    }

    public void testNull() {
        try {
            CronExpressionValidator.validateExpression(null);
            fail();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    public void testComplex() throws Exception {

        try {
            CronExpressionValidator
                    .validateExpression("0/5 14,18,3-39,52 * ? JAN,MAR,SEP MON-FRI 2002-2010");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
    }

}
