package com.ocr.cash_register;

import lombok.extern.slf4j.Slf4j;

import java.util.*;


/**
 * A class to calculate the possible combinations of
 * a given set of Denominations to make up a a target amount.
 * Based on the Partition algo.
 */
@Slf4j
public class Partition {

    private final int target;

    private List<Map<Double, Integer>> results = new ArrayList<>();
    private ValidatingMap<Double, Integer> current;
    private boolean currentValid;
    private int depth = 0;


    public Partition(int target) {
        this.target = target;
        intialize(0.0);
    }

    /**
     * XXX DL FIXME. Should be using Denomination class to populate this.
     * @param base The 'base' unit on which we are working for this iteration.
     * @return A Map of Denomination multiplier by number of units.
     */
    private Map<Double, Integer> intialize(Double base) {
        current = new ValidatingMap<>((double) target);
        Arrays.asList(new Double[]{1.0, 2.0, 5.0, 10.0, 20.0}).forEach(d -> {
            current.put(d, Integer.valueOf(0));
        });
        if (base > 0 && current.containsKey(base)) {
            current.replace(base, 1);
        }
        currentValid = true;
        return current;
    }

    public List<Map<Double, Integer>> partition() {
        partition(target, target, "", 0.0);
        return results;
    }

    public void partition(int n, int max, String prefix, Double base) {
        ++depth;
        // log.debug("n: {} max: {} prefix: {} base: {}", n, max, prefix, base);
        Integer d = current.get((double) max);
        if (d != null) {
            current.replace((double) max, ++d);
        } else {
            currentValid = false;
        }
        if (n == 0) {
            // log.debug(prefix);
            if (currentValid) {
                if (current.isValidTotal()) {
                    results.add(current);
                }
            }
            intialize(base);
            return;
        }

        for (int i = Math.min(max, n); i >= 1; i--) {
            if (depth == 1){
                // log.debug("Depth: {}", depth);
                base = (double) i;
                currentValid = true;
            }
            partition(n - i, i, prefix + " " + i, base);
            --depth;
        }
    }

    private void list() {
        results.forEach(mdd ->{
            log.debug(mdd.toString());
        });
    }

    private class ValidatingMap<K extends Double, V extends Integer> extends HashMap<K, V> {
        private Double total = Double.valueOf(0);
        private final Double target;

        public ValidatingMap(Double target) {
            this.target = target;
        }

        @Override
        public V replace(K key, V value) {
            V val = super.put(key, value);
            return val;
        }

        public boolean isValidTotal() {
            entrySet().forEach(dd -> {
                total += Double.valueOf(dd.getKey()) * Double.valueOf(dd.getValue());
            });
            return total.equals(target);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder ("Mapped: ").append("\n");
            entrySet().forEach(dd ->{
                sb.append(dd.getKey()).append(" cnt: ").append(dd.getValue()).append("\n");
            });
            return sb.toString();
        }
    }
}