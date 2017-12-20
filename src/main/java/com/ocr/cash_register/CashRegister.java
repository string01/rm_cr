package com.ocr.cash_register;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Basic CashRegister implementation.
 */
@Component
@Data
@Slf4j
public class CashRegister {

    @Autowired
    private CashDrawerFormatter cashDrawerFormatter;

    @Autowired
    private CashDrawerFactory cashDrawerFactory;

    @Autowired
    private Calculator calculator;

    private CashDrawer cashDrawer;

    @PostConstruct
    public void init() {
        try {
            cashDrawer = new CashDrawerFactory().create(CashDrawer.ZERO_INPUT_FORMAT);
        } catch (InputFormatException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public void put(CashDrawer cashDrawer) {
        this.cashDrawer = this.cashDrawer.add(cashDrawer);
    }

    public void take(CashDrawer cashOut) throws InsufficientFundsException {
        if (cashOut.getTotal() > this.cashDrawer.getTotal()) {
            throw new InsufficientFundsException();
        }
        CashDrawer resultCashDrawer = this.cashDrawer.subtract(cashOut);
        this.cashDrawer = resultCashDrawer;
    }

    public CashDrawer change(int amt) throws InvalidChangeException, InsufficientFundsException {
        Double amtRequested = Double.valueOf(amt);
        CashDrawer amtCd = cashDrawerFactory.create(new BigDecimal(amtRequested));
        Calculator.RequestResult requestResult = calculator.makeChangeSearchMode(this.cashDrawer, amtCd);

        CashDrawer resultCashDrawer1 = requestResult.getResult();
        if (!resultCashDrawer1.getTotal().equals(amtRequested)) {
            // Here's where I would need to kick in an algo for alternative
            // combinations to make up 11 etc.
            throw new InvalidChangeException("Can not make correct change.");
        }
        take(resultCashDrawer1);
        return cashDrawer;
    }


    public void clear() throws InputFormatException {
        this.cashDrawer = new CashDrawerFactory().createZero();
    }
}
