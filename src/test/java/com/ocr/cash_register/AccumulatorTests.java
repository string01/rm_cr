package com.ocr.cash_register;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccumulatorTests {

    @Test
    public void test(){
        Accumulator accumulator1 = new Accumulator(Denomination.FIVE, 1);
        IncrementResult result1 = accumulator1.increment(2, Denomination.TEN);
        assertEquals(5, result1.getAccumulator().getNumberOfUnits());

    }
}
