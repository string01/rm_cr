package com.ocr.cash_register;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Basic CashRegister implementation.
 */
@Component
@Slf4j
public class CashRegister {

    @Autowired
    private CashDrawerFormatter cashDrawerFormatter;

    @Autowired
    private CashDrawerFactory cashDrawerFactory;

    @Autowired
    private Calculator calculator;

    @Getter
    private CashDrawer cashDrawer;
    
    @Getter
    private CashDrawer lastTaken;

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
        lastTaken = cashDrawerFactory.createEmpty();
        if (cashOut.getTotal() > cashDrawer.getTotal()) {
            throw new InsufficientFundsException();
        }
        lastTaken = cashOut;
        CashDrawer resultCashDrawer = cashDrawer.subtract(cashOut);
        cashDrawer = resultCashDrawer;
    }

    public CashDrawer change(int amt) throws InvalidChangeException, InsufficientFundsException {
        Double amtRequested = Double.valueOf(amt);

        if (amt > cashDrawer.getTotal()){
            throw new InsufficientFundsException();
        }

        CashDrawer amtCd = cashDrawerFactory.create(amtRequested);

        Calculator.RequestResult requestResult = calculator.makeChange(this.cashDrawer, amtCd);

        CashDrawer resultCashDrawer1 = requestResult.getResult();
        boolean found = false;
        if (!resultCashDrawer1.getTotal().equals(amtRequested)) {
            // Here we kick in an algo for alternative combinations.
            List<Map<Double, Integer>> possibleCombinations = new Partition(amt).partition();
            for (Map<Double, Integer> cd : possibleCombinations){
                requestResult = calculator.subsetMatch(cashDrawer, cashDrawerFactory.create(cd));
                resultCashDrawer1 = requestResult.getResult();
                if(resultCashDrawer1.getTotal().equals(amtRequested)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new InvalidChangeException("Can not make correct change.");
            }
        }
        take(resultCashDrawer1);
        return cashDrawer;
    }


    public void clear() throws InputFormatException {
        cashDrawer = new CashDrawerFactory().createEmpty();
    }

    public Double getTotal() {
        return cashDrawer.getTotal();
    }
}
