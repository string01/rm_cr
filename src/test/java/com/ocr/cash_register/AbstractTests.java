package com.ocr.cash_register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

public abstract class AbstractTests extends AbstractJUnit4SpringContextTests implements TestConst {

    @Autowired
    protected CashRegister cashRegister;
    @Autowired
    protected CashDrawerFormatter cashDrawerFormatter;
    @Autowired
    protected CashDrawerFactory cashDrawerFactory;

    protected String createOutFormat() {
        return cashDrawerFormatter.toOutput(cashRegister.getCashDrawer());
    }
}
