package com.ocr.cash_register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Create CashDrawer instances given either the money format
 * i.e. ##20 #$10 #$5 #$2 #41
 * or from a given amount.
 */
@Component
@Slf4j
public class CashDrawerFactory {
    
    public final static String MUST_BE_A_NUMBER = " must be a number.";
    private final static String FORMAT_ZERO = "0 0 0 0 0";
    private final static int EXACT_TOKENS = 5;
    public final static String INVALID_NUMBER_OF_TOKENS = "Invalid number of tokens. Needs to be: " + EXACT_TOKENS;
    
    public CashDrawer create(String moneyFormat) throws InputFormatException {
        CashDrawer cashDrawer = new CashDrawer(createAccumulators(moneyFormat));
        return cashDrawer;
    }
    
    public CashDrawer create(Double amt) {
        try {
            CashDrawer cashDrawer = createFor(amt);
            return cashDrawer;
        } catch (Exception ex) {
            log.error("", ex);
            throw new IllegalStateException(ex);
        }
    }
    
    public CashDrawer create(Map<Double, Integer> map) {
        CashDrawer cashDrawer = createEmpty();
        map.entrySet().forEach(dd -> {
            cashDrawer.add(Accumulator.create(dd.getKey(), dd.getValue()));
            
        });
        return cashDrawer;
    }
    
    public CashDrawer createEmpty() {
        try {
            return create(FORMAT_ZERO);
        } catch (InputFormatException e) {
            // XXX This should never happen.
            throw new IllegalStateException(e);
        }
    }
    
    private CashDrawer createFor(Double amtx) {
        CashDrawer cashDrawer = createEmpty();
        
        for (Denomination denomination : Denomination.getDenominations()) {
            amtx = createAccumulator(cashDrawer, amtx, denomination);
        }
        return cashDrawer;
    }
    
    private Double createAccumulator(CashDrawer cashDrawer, Double amt, Denomination denomination) {
        Double amtx = amt;
        amt = Math.floor(amt / denomination.multiplier());
        if (amt.intValue() >= 1) {
            cashDrawer.add(Accumulator.create(denomination.multiplier(), amt.intValue()));
            amtx = amtx - (denomination.multiply(amt));
        }
        return amtx;
    }
    
    private ConcurrentSkipListMap<Denomination, Accumulator> createAccumulators(String moneyFormat) throws InputFormatException {
        
        StringTokenizer stringTokenizer = new StringTokenizer(moneyFormat, " ");
        if (stringTokenizer.countTokens() != EXACT_TOKENS) {
            throw new InputFormatException(INVALID_NUMBER_OF_TOKENS);
        }
        ConcurrentSkipListMap<Denomination, Accumulator> accumulators = createAccumulators();
        
        int pos = 0;
        while (stringTokenizer.hasMoreTokens()) {
            Accumulator accumulator = createAccumulator(pos, stringTokenizer.nextToken());
            accumulators.put(accumulator.getDenomination(), accumulator);
            ++pos;
        }
        return accumulators;
    }
    
    private ConcurrentSkipListMap<Denomination, Accumulator> createAccumulators() {
        ConcurrentSkipListMap<Denomination, Accumulator> accumulators = new ConcurrentSkipListMap<>(new DescendingDenominationComparator());
        return accumulators;
    }
    
    private Accumulator createAccumulator(int pos, String s) throws InputFormatException {
        try {
            Denomination[] denominations = Denomination.getDenominations();
            Accumulator accumulator = new Accumulator(denominations[pos], Integer.parseInt(s));
            return accumulator;
        } catch (NumberFormatException e) {
            throw new InputFormatException("Position: " + pos + MUST_BE_A_NUMBER, e);
        }
    }
}
