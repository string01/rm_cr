package com.ocr.cash_register;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * An immutable virtual cash drawer representation made up of a sorted
 * set of Accumulators. Accumulators are sorted in descending order
 * based on their Denomination.
 */

@Slf4j
public class CashDrawer {
    
    public static final String ZERO_INPUT_FORMAT = "0 0 0 0 0";
    @Getter
    private ConcurrentSkipListMap<Denomination, Accumulator> accumulators = new ConcurrentSkipListMap<>();
    
    private double total = 0.0;
    
    public CashDrawer(ConcurrentSkipListMap<Denomination, Accumulator> accumulators) {
        this.accumulators = accumulators;
        calculate();
    }
    
    public Double getTotal() {
        return total;
    }
    
    private void calculate() {
        total = 0.0;
        accumulators.values().forEach(a -> {
            total += a.getTotal();
        });
    }
    
    public CashDrawer add(CashDrawer cashDrawer) {
        ConcurrentSkipListMap<Denomination, Accumulator> newAccumulators = new ConcurrentSkipListMap<>(accumulators.comparator());
        Accumulator[] inputAccumulators = cashDrawer.getAccumulators().values().toArray(new Accumulator[]{});
        CashDrawer cashDrawer1 = new CashDrawerFactory().createEmpty();
        int idx = 0;
        for (Accumulator a : accumulators.values()) {
            newAccumulators.putIfAbsent(a.getDenomination(), a.add(inputAccumulators[idx++]));
        }
        cashDrawer1.setAccumulators(newAccumulators);
        cashDrawer1.calculate();
        return cashDrawer1;
    }
    
    public CashDrawer subtract(CashDrawer cashDrawer) {
        // ConcurrentSkipListMap<Denomination, Accumulator> newAccumulators = new ConcurrentSkipListMap<>(accumulators.comparator());
        Accumulator[] inputAccumulators = cashDrawer.getAccumulators().values().toArray(new Accumulator[]{});
        CashDrawer resultCashDrawer = new CashDrawerFactory().createEmpty();
        int idx = 0;
        for (Accumulator a : accumulators.values()) {
            SubtractionResult result = a.subtract(inputAccumulators[idx]);
            if (result.hasRemainder()) {
                pushRemainderDown(inputAccumulators,
                        idx, result.getRemainder(), result.getAccumulator().getDenomination());
            }
            resultCashDrawer.accumulators.replace(a.getDenomination(), result.getAccumulator());
            ++idx;
            if (idx == inputAccumulators.length) {
                log.debug("Last");
            }
        }
        resultCashDrawer.calculate();
        return resultCashDrawer;
    }
    
    private void pushRemainderDown(Accumulator[] inputAccumulators, int idx, int remainder, Denomination d) {
        int nextIdx = idx + 1;
        if (nextIdx >= (inputAccumulators.length - 1)) {
            return;
        }
        Accumulator next = inputAccumulators[nextIdx];
        log.debug("ac: {} next: {} rem: {}" + next, inputAccumulators[idx], remainder);
        IncrementResult ir = next.increment(remainder, d);
        inputAccumulators[nextIdx] = ir.getAccumulator();
        if (ir.getRemainder() > 0) {
            pushRemainderDown(inputAccumulators,
                    nextIdx,
                    ir.getRemainder(),
                    ir.getAccumulator().getDenomination());
        }
    }
    
    private void setAccumulators(ConcurrentSkipListMap<Denomination, Accumulator> accumulators) {
        this.accumulators = accumulators;
    }
    
    public void add(Accumulator a) {
        accumulators.replace(a.getDenomination(), a);
        calculate();
    }
    
    public Accumulator getAccumulatorFor(Denomination denomination) {
        Optional<Accumulator> oa = getAccumulators().values().stream().filter(a -> a.getDenomination().equals(denomination)).findFirst();
        if (oa.isPresent()) {
            return oa.get();
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("$").append(total).append("  ");
        accumulators.values().forEach(a -> {
            sb.append(a.toStringTrunc()).append(" ");
        });
        return sb.toString();
    }
}
