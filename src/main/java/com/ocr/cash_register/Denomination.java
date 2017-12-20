package com.ocr.cash_register;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

/**
 * Represent a monetary denomination.
 * Also holds the next lowest denomination for scanning.
 */
@Data
public class Denomination implements Comparable<Denomination> {

    private final Double multiplier;
    private final Denomination nextLowest;

    public Denomination(Double multiplier, Denomination nextLowest) {
        this.multiplier = multiplier;
        this.nextLowest = nextLowest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Denomination that = (Denomination) o;

        return new EqualsBuilder()
                .append(multiplier, that.multiplier)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(multiplier)
                .toHashCode();
    }

    @Override
    public int compareTo(Denomination o) {
        return multiplier.compareTo(o.multiplier);
    }

    public BigDecimal multiplier() {
        return BigDecimal.valueOf(multiplier);
    }

    public BigDecimal multiply(BigDecimal amt) {
        return multiplier().multiply(amt);
    }

    public static Denomination ZERO = new Denomination(0.0, Denomination.ZERO);
    public static Denomination ONE = new Denomination(1.0, Denomination.ZERO);
    public static Denomination TWO = new Denomination(2.0, Denomination.ONE);
    public static Denomination FIVE = new Denomination(5.0, Denomination.TWO);
    public static Denomination TEN = new Denomination(10.0, Denomination.FIVE);
    public static Denomination TWENTY = new Denomination(20.0, Denomination.TEN);
}
