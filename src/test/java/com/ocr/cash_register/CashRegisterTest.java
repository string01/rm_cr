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
            cashRegister.put(createCashDrawer(FORMAT_0));
            assertEquals(EXPECTED_DOUBLE_0, cashRegister.getCashDrawer().getTotal());
            cashRegister.put(createCashDrawer(FORMAT_0));
            assertEquals(EXPECTED_DOUBLE_1, cashRegister.getCashDrawer().getTotal());
        } catch (InputFormatException e) {
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testTake() {
        assertNotNull(cashRegister);
        try {
            CashDrawer cashDrawer = cashRegister.getCashDrawer();
            cashRegister.take(createCashDrawer(FORMAT_0));
            cashDrawer = cashRegister.getCashDrawer();
            assertEquals(EXPECTED_DOUBLE_0, cashDrawer.getTotal());
            cashRegister.put(createCashDrawer(FORMAT_68));
            cashRegister.take(createCashDrawer(FORMAT_68));
            cashDrawer = cashRegister.getCashDrawer();
            assertEquals(EXPECTED_DOUBLE_0, cashDrawer.getTotal());
        } catch (InputFormatException | InsufficientFundsException e) {
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testChange() throws InvalidChangeException {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            String out = createOutFormat();
            assertEquals(OUTPUT_0, out);
            cashRegister.put(createCashDrawer(FORMAT_68));
            out = createOutFormat();
            assertEquals(OUTPUT_68, out);
            cashRegister.put(createCashDrawer(FORMAT_60));
            out = createOutFormat();
            assertEquals(OUTPUT_128, out);
            cashRegister.take(createCashDrawer(FORMAT_85));
            out = createOutFormat();
            assertEquals(OUTPUT_43, out);
        } catch (Exception e) {
            log.error("", e);
            fail();
        }
        try {
            expectedException.expect(InvalidChangeException.class);
            CashDrawer cashDrawer = cashRegister.change(11);
            assertEquals(Double.valueOf(32), cashDrawer.getTotal());
        } catch (InsufficientFundsException e) {
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testChange6848() throws InvalidChangeException {
        try {
            assertNotNull(cashRegister);
            cashRegister.clear();
            String out = createOutFormat();
            assertEquals(OUTPUT_0, out);
            cashRegister.put(createCashDrawer(FORMAT_68));
            out = createOutFormat();
            assertEquals(OUTPUT_68, out);
            CashDrawer cashDrawer = cashRegister.change(48);
            assertEquals(Double.valueOf(20),cashDrawer.getTotal());
        } catch (Exception e) {
            log.error("", e);
            fail();
        }
        try {
            // At this point, there is a combination that will make up
            // 11 in change. However, the Calculator algo will not find it.
            expectedException.expect(InvalidChangeException.class);
            CashDrawer cashDrawer = cashRegister.change(11);
            assertEquals(Double.valueOf(9), cashDrawer.getTotal());
            cashDrawer = cashRegister.change(11);
        } catch (InsufficientFundsException e) {
            log.error("", e);
            fail();
        }

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
