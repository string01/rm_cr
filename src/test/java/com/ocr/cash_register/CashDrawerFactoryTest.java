package com.ocr.cash_register;


import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.contains;

@Slf4j
public class CashDrawerFactoryTest {

    protected static String FORMAT_0 = "1 1 1 1 1";
    protected static Double EXPECTED_0 = 38.0;
    protected static String BAD_FORMAT_0 = "1 1 1 1";
    protected static String BAD_FORMAT_1 = "1 x 1 1 1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFactory0(){
        CashDrawer cashDrawer = null;
        try {
            cashDrawer = createCashDrawer(FORMAT_0);
        } catch (InputFormatException e) {
            log.error("Test fail: ", e);
            fail();
        }
        assertNotNull(cashDrawer);
        assertEquals(EXPECTED_0, cashDrawer.getTotal());
    }

    @Test
    public void testBadInputFormat0() throws InputFormatException {
        expectedException.expectMessage(contains(CashDrawerFactory.INVALID_NUMBER_OF_TOKENS));
        innerBadFormatTest(BAD_FORMAT_0);
    }

    @Test
    public void testBadInputFormat1() throws InputFormatException {
        expectedException.expectMessage(contains(CashDrawerFactory.MUST_BE_A_NUMBER));
        innerBadFormatTest(BAD_FORMAT_1);
    }

    private void innerBadFormatTest(String badFormattedInput) throws InputFormatException {
        expectedException.expect(InputFormatException.class);
        CashDrawer cashDrawer = createCashDrawer(badFormattedInput);
        fail("Did not throw expected exception.");
    }

    private CashDrawer createCashDrawer(String format) throws InputFormatException {
        CashDrawer cashDrawer = new CashDrawerFactory().create(format);
        return cashDrawer;
    }
}
