package com.ocr.cash_register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
    @Autowired
    CashDrawerFormatter cashDrawerFormatter;

    public CashDrawer create(String moneyFormat) throws InputFormatException {
        CashDrawer cashDrawer = new CashDrawer(createAccumulators(moneyFormat));
        return cashDrawer;
    }

    public CashDrawer create(BigDecimal amt) {
        try {
            CashDrawer cashDrawer = create(createFormatFor(amt));
            return cashDrawer;
        } catch (Exception ex) {
            log.error("", ex);
            throw new IllegalStateException(ex);
        }
    }

    public CashDrawer create(Map<Double, Integer> map){
        CashDrawer cashDrawer = createEmpty();
        map.entrySet().forEach(dd -> {
            cashDrawer.add(Accumulator.create(dd.getKey(), dd.getValue()));

        });
        return cashDrawer;
    }

    public CashDrawer createZero() {
        try {
            return create(FORMAT_ZERO);
        } catch (InputFormatException e) {
            // XXX This should never happen.
            throw new IllegalStateException(e);
        }
    }

    public CashDrawer createEmpty() {
        CashDrawer cashDrawer = new CashDrawer(createAccumulators());
        return cashDrawer;
    }

    /**
     * This creates a format string from a BigDecimal.
     * XXX DL really should refactor this to use Double to avoid the ugliness that
     * is BigDecimal.
     * @param amtx
     * @return
     */
    private String createFormatFor(BigDecimal amtx) {
        BigDecimal amt20 = amtx.divide((Denomination.TWENTY.multiplier()).setScale(0, RoundingMode.DOWN));
        BigDecimal amt20x = amtx.divide(Denomination.TWENTY.multiplier());
        BigDecimal amt30y = amt20x.setScale(0, RoundingMode.DOWN);
        if (amt20.intValue() >= 1) {
            amt20 = amt20.setScale(0, RoundingMode.DOWN);
            amtx = amtx.subtract(Denomination.TWENTY.multiply(amt20));
        } else {
            amt20 = BigDecimal.ZERO;
        }
        BigDecimal amt10 = amtx.divide(Denomination.TEN.multiplier());
        if (amt10.intValue() >= 1) {
            amt10 = amt10.setScale(0, RoundingMode.DOWN);
            amtx = amtx.subtract(Denomination.TEN.multiply(amt10));
        } else {
            amt10 = BigDecimal.ZERO;
        }
        BigDecimal amt5 = amtx.divide(Denomination.FIVE.multiplier());
        if (amt5.intValue() >= 1) {
            amt5 = amt5.setScale(0, RoundingMode.DOWN);
            amtx = amtx.subtract(Denomination.FIVE.multiply(amt5));
        } else {
            amt5 = BigDecimal.ZERO;
        }
        BigDecimal amt2 = amtx.divide(Denomination.TWO.multiplier());
        if (amt2.intValue() >= 1) {
            amt2 = amt2.setScale(0, RoundingMode.DOWN);
            amtx = amtx.subtract(Denomination.TWO.multiply(amt2));
        } else {
            amt2 = BigDecimal.ZERO;
        }
        BigDecimal amt1 = amtx.divide(Denomination.ONE.multiplier());
        if (amt1.intValue() >= 1) {
            amt1 = amt1.setScale(0, RoundingMode.DOWN);
            amtx = amtx.subtract(Denomination.ONE.multiply(amt1));
        } else {
            amt1 = BigDecimal.ZERO;
        }
        return cashDrawerFormatter.fromInput(amt20.toString(),
                amt10.toString(),
                amt5.toString(),
                amt2.toString(),
                amt1.toString());
    }


    private SortedSet<Accumulator> createAccumulators(String moneyFormat) throws InputFormatException {

        StringTokenizer stringTokenizer = new StringTokenizer(moneyFormat, " ");
        if (stringTokenizer.countTokens() != EXACT_TOKENS) {
            throw new InputFormatException(INVALID_NUMBER_OF_TOKENS);
        }
        SortedSet<Accumulator> accumulators = createAccumulators();

        int pos = 0;
        while (stringTokenizer.hasMoreTokens()) {
            accumulators.add(createAccumulator(pos, stringTokenizer.nextToken()));
            ++pos;
        }
        return accumulators;
    }

    private SortedSet<Accumulator> createAccumulators() {
        SortedSet<Accumulator> accumulators = new TreeSet<Accumulator>(new DescendingDenominationComparator());
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
