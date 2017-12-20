package com.ocr.cash_register;

import lombok.Data;

/**
 * Result of subtracting one Accumulator from another
 * and the remainder.
 */
@Data
public class SubtractionResult {
    private final Accumulator accumulator;
    private final int remainder;
    private final Accumulator amtTaken;

    public SubtractionResult(Accumulator result, int remainder, Accumulator amtTaken) {
        this.accumulator = result;
        this.remainder = remainder;
        this.amtTaken = amtTaken;
    }

    public boolean hasRemainder() {
        return remainder != 0;
    }
}
