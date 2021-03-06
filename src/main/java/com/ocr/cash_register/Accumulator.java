package com.ocr.cash_register;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * An immutable accumulator of units for a given denomination.
 */
@Data
@Slf4j
public class Accumulator {

    @Getter
    private final Denomination denomination;
    @Getter
    private final double total;
    @Getter
    private int numberOfUnits;

    public Accumulator(Denomination denomination) {
        this(denomination, 0);
    }

    public Accumulator(Denomination denomination, Integer nUnits) {
        this.denomination = denomination;
        this.numberOfUnits = nUnits;
        this.total = nUnits * denomination.getMultiplier();
    }

    /**
     * Factory method for creating an Accumulator with a given
     * multiplier and numberOfUnits.
     */
    public static Accumulator create(Double mulitplier, Integer numberOfUnits) {
        return new Accumulator(Denomination.create(mulitplier), numberOfUnits);
    }
    
    public static Accumulator createEmpty(Denomination denomination){
        return new Accumulator(denomination);
    }
    
    public static Accumulator createEmpty(Accumulator a) {
        return createEmpty(a.denomination);
    }

    public Accumulator add(Accumulator accumulator) {
        int t = accumulator.numberOfUnits;
        t = this.numberOfUnits + t;
        return new Accumulator(denomination, t);
    }

    public SubtractionResult subtract(Accumulator request) {
        int remainder = 0;
        int amtTaken = request.numberOfUnits;
        int result = this.numberOfUnits - request.numberOfUnits;
        if (result < 0) {
            remainder = Math.abs(result);
            amtTaken = (request.numberOfUnits - remainder);
            result = numberOfUnits - amtTaken;
        }
        SubtractionResult sr = new SubtractionResult(new Accumulator(denomination, result), remainder, new Accumulator(denomination, amtTaken));
        return sr;
    }
    
    public IncrementResult increment(int numberOfUnits, Denomination denomination) {
        if (numberOfUnits == 0){
            return new IncrementResult(this, 0);
        }
        BigDecimal inputDenominationMultiplier = BigDecimal.valueOf(denomination.multiplier());
        BigDecimal denominationMultiplier = BigDecimal.valueOf(this.denomination.multiplier());
        BigDecimal d = inputDenominationMultiplier
                .divide(denominationMultiplier)
                .multiply(BigDecimal.valueOf(numberOfUnits));
        BigDecimal r = inputDenominationMultiplier.remainder(denominationMultiplier);
        Accumulator a = new Accumulator(this.denomination, this.numberOfUnits + d.intValue());
        return new IncrementResult(a, r.intValue());
    }

    public Double unitValue(){
        return 1 * denomination.getMultiplier();
    }

    @Override
    public String toString() {
        return "Accumulator{" +
                " n: " + numberOfUnits +
                ", d:"  + denomination.getMultiplier() +
                ", t: "   + total +
                '}';
    }

    public String toStringTrunc() {
        return "" + numberOfUnits;
    }
    
}
