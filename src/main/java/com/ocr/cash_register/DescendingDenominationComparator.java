package com.ocr.cash_register;

import java.util.Comparator;

/**
 * Comparator that will compare Denomination instances in descending order.
 *
 * @param <T>
 */
public class DescendingDenominationComparator<T extends Denomination> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        return o2.compareTo(o1);
        //return o2.getDenomination().compareTo(o1.getDenomination());
    }
}
