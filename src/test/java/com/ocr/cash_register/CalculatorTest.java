package com.ocr.cash_register;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CalculatorTest extends AbstractTests {

    @Autowired
    protected Calculator calculator;

    protected static final Double DOUBLE_48 = Double.valueOf(48);
    protected static final Double DOUBLE_20 = Double.valueOf(20);

    @Test
    public void test0(){
        try {
            CashDrawer orig = cashDrawerFactory.create(FORMAT_68_1);
            CashDrawer req = cashDrawerFactory.create(FORMAT_48);
            Calculator.RequestResult requestResult = calculator.makeChange(orig, req);
            assertEquals(DOUBLE_48, requestResult.getResult().getTotal());
            assertEquals(DOUBLE_20, requestResult.getRemaining().getTotal());

        } catch (Exception e){
            log.error("", e);
            fail();
        }
    }

    @Test
    public void testSearch(){
        try {
            CashDrawer orig = cashDrawerFactory.create(FORMAT_68);
            CashDrawer req = cashDrawerFactory.create(FORMAT_48);
            Calculator.RequestResult requestResult = calculator.makeChange(orig, req);
            assertEquals(DOUBLE_48, requestResult.getResult().getTotal());
            assertEquals(DOUBLE_20, requestResult.getRemaining().getTotal());
            orig = cashDrawerFactory.create(FORMAT_68_1);
            req = cashDrawerFactory.create(FORMAT_48);
            requestResult = calculator.makeChange(orig, req);
            assertEquals(DOUBLE_48, requestResult.getResult().getTotal());
            assertEquals(DOUBLE_20, requestResult.getRemaining().getTotal());

        } catch (Exception e){
            log.error("", e);
            fail();
        }
    }
}
