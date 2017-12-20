package com.ocr.cash_register;

import lombok.Data;

/**
 * Result of incrementing and Accumulator by a given amount
 * and the remaining amount that couldn't fit in the given
 * accumulator's denomination.
 */
@Data
public class IncrementResult {
    private final Accumulator accumulator;
    private final int remainder;
}
