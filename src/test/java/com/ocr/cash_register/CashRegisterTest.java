package com.ocr.cash_register;


import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@SpringBootTest
public class CashRegisterTest extends AbstractTests {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testPut() {
        assertNotNull(cashRegister);
        try {
            cashRegister.clear();
            cashRegister.put(createCashDrawer(FORMAT_38));
            assertEquals(EXPECTED_DOUBLE_38, cashRegister.getCashDrawer().getTotal());
            cashRegister.put(createCashDrawer(FORMAT_38));
            assertEquals(EXPECTED_DOUBLE_1, cashRegister.getCashDrawer().getTotal());
        } catch (InputFormatException e) {
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testTake() throws InsufficientFundsException {
        assertNotNull(cashRegister);
        try {
            cashRegister.clear();
            cashRegister.put(createCashDrawer(FORMAT_38));
            cashRegister.take(createCashDrawer(FORMAT_38));
            CashDrawer cashDrawer = cashRegister.getCashDrawer();
            assertEquals(EXPECTED_DOUBLE_0, cashDrawer.getTotal());
            cashRegister.put(createCashDrawer(FORMAT_68));
            cashRegister.take(createCashDrawer(FORMAT_68));
            cashDrawer = cashRegister.getCashDrawer();
            assertEquals(EXPECTED_DOUBLE_0, cashDrawer.getTotal());
            expectedException.expect(InsufficientFundsException.class);
            cashRegister.take(createCashDrawer(FORMAT_68));
        } catch (InputFormatException e) {
            log.error("", e);
            fail();
        }
    }

    /**
     * @throws InvalidChangeException
     */
    @Test
    public void testChangeSimple() {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            String out = createOutFormat();
            assertEquals(OUTPUT_0, out);
            cashRegister.put(createCashDrawer(FORMAT_68));
            assertEquals(EXPECTED_DOUBLE_68, cashRegister.getTotal());
            cashRegister.put(createCashDrawer(FORMAT_60));
            assertEquals(EXPECTED_DOUBLE_128, cashRegister.getTotal());
            cashRegister.take(createCashDrawer(FORMAT_85));
            assertEquals(EXPECTED_DOUBLE_43, cashRegister.getTotal());
            CashDrawer cashDrawer = cashRegister.change(11);
            assertEquals(EXPECTED_DOUBLE_32, cashDrawer.getTotal());
        } catch (Exception e) {
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testInvalidChange() throws InvalidChangeException {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            String out = createOutFormat();
            assertEquals(OUTPUT_0, out);
            cashRegister.put(createCashDrawer(FORMAT_22));
            assertEquals(EXPECTED_DOUBLE_22, cashRegister.getTotal());
            expectedException.expect(InvalidChangeException.class);
            CashDrawer cashDrawer = cashRegister.change(11);
            assertEquals(EXPECTED_DOUBLE_11, cashDrawer.getTotal());
        } catch (InsufficientFundsException | InputFormatException e) {
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testChange6848() throws InsufficientFundsException {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            String out = createOutFormat();
            assertEquals(OUTPUT_0, out);
            cashRegister.put(createCashDrawer(FORMAT_68));
            out = createOutFormat();
            assertEquals(OUTPUT_68, out);
            CashDrawer cashDrawer = cashRegister.change(48);
            assertEquals(EXPECTED_DOUBLE_20,cashDrawer.getTotal());
        } catch (Exception e) {
            log.error("", e);
            fail();
        }
        try {
            CashDrawer cashDrawer = cashRegister.change(11);
            assertEquals(EXPECTED_DOUBLE_9, cashDrawer.getTotal());
            expectedException.expect(InsufficientFundsException.class);
            cashDrawer = cashRegister.change(11);
        } catch (InvalidChangeException e) {
            log.error("", e);
            fail();
        }
    }

    /**
     * This is explicit to the use-case in the requirements.
     * XXX DL Expand to test edge cases.
     * @throws InsufficientFundsException
     */
    @Test
    public void testChange11() throws InsufficientFundsException {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            assertEquals(Double.valueOf(0.0), cashRegister.getTotal());
            cashRegister.put(createCashDrawer("1 0 3 4 0"));
            String out = createOutFormat();
            assertEquals(OUTPUT_43, out);
            CashDrawer cashDrawer = cashRegister.change(11);
            assertEquals(EXPECTED_DOUBLE_32,cashDrawer.getTotal());
            cashDrawer = cashRegister.change(12);
            assertEquals(Double.valueOf(EXPECTED_DOUBLE_20), cashDrawer.getTotal());
        } catch (Exception e) {
            log.error("", e);
            fail();
        }
        /*
        try {
            expectedException.expect(InsufficientFundsException.class);
            cashDrawer = cashRegister.change(11);
        } catch (InvalidChangeException e) {
            log.error("", e);
            fail();
        }
        */

    }

    @Test
    public void testInsufficientFunds() throws InsufficientFundsException {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            String out = createOutFormat();
            assertEquals(OUTPUT_0, out);
            cashRegister.put(createCashDrawer(FORMAT_68));
            out = createOutFormat();
            assertEquals(OUTPUT_68, out);
            expectedException.expect(InsufficientFundsException.class);
            cashRegister.take(createCashDrawer(FORMAT_100));
        } catch (InputFormatException e) {
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testChange5() {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            String out = createOutFormat();
            assertEquals(OUTPUT_0, out);
            cashRegister.put(createCashDrawer(FORMAT_5));
            out = createOutFormat();
            assertEquals(OUTPUT_5, out);
            cashRegister.take(createCashDrawer(FORMAT_5));
            out = createOutFormat();
            assertEquals(OUTPUT_0, out);
        } catch (Exception e) {
            log.error("", e);
            fail();
        }
    }


    private CashDrawer createCashDrawer(String format) throws InputFormatException {
        CashDrawer cashDrawer = new CashDrawerFactory().create(format);
        return cashDrawer;
    }
}
