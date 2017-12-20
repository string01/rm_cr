package com.ocr.cash_register;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An immutable virtual cash drawer representation made up of a sorted
 * set of Accumulators. Accumulators are sorted in descending order
 * based on their Denomination.
 */

@Slf4j
public class CashDrawer {

    public static final String ZERO_INPUT_FORMAT = "0 0 0 0 0";
    @Getter
    private SortedSet<Accumulator> accumulators = new TreeSet<Accumulator>();
    private double total = 0.0;

    public CashDrawer(SortedSet<Accumulator> accumulators) {
        this.accumulators = accumulators;
        calculate();
    }

    public Double getTotal() {
        return total;
    }

    private void calculate() {
        total = 0.0;
        accumulators.forEach(a -> {
            total += a.getTotal();
        });
    }

    public CashDrawer add(CashDrawer cashDrawer) {
        SortedSet<Accumulator> newAccumulators = new TreeSet<>(accumulators.comparator());
        Accumulator[] inputAccumulators = cashDrawer.getAccumulators().toArray(new Accumulator[]{});
        CashDrawer cashDrawer1 = new CashDrawerFactory().createZero();
        int idx = 0;
        for (Accumulator a : accumulators) {
            newAccumulators.add(a.add(inputAccumulators[idx++]));
        }
        cashDrawer1.setAccumulators(newAccumulators);
        cashDrawer1.calculate();
        return cashDrawer1;
    }

    public CashDrawer subtract(CashDrawer cashDrawer) {
        SortedSet<Accumulator> newAccumulators = new TreeSet<>(accumulators.comparator());
        Accumulator[] inputAccumulators = cashDrawer.getAccumulators().toArray(new Accumulator[]{});
        CashDrawer resultCashDrawer = new CashDrawerFactory().createZero();
        int idx = 0;
        for (Accumulator a : accumulators) {
            SubtractionResult result = a.subtract(inputAccumulators[idx]);
            if (result.hasRemainder()) {
                pushRemainderDown(inputAccumulators, idx, result.getRemainder(), result.getAccumulator().getDenomination());
            }
            newAccumulators.add(result.getAccumulator());
            ++idx;
            if (idx == inputAccumulators.length) {
                log.debug("Last");
            }
        }
        resultCashDrawer.setAccumulators(newAccumulators);
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

    private void setAccumulators(SortedSet<Accumulator> accumulators) {
        this.accumulators = accumulators;
    }

    public void add(Accumulator a) {
        accumulators.add(a);
        calculate();
    }
}
