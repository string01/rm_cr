package com.ocr.cash_register;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Partition {

    final int target;

    List<Map<Double, Double>> results = new ArrayList<>();
    ValidatingMap<Double, Double> current;
    boolean currentValid;


    public Partition(int target) {
        this.target = target;
        intialize(0.0);
    }

    public Map<Double, Double> intialize(Double base) {
        current = new ValidatingMap<Double, Double>(Double.valueOf(target));
        Arrays.asList(new Double[]{1.0, 2.0, 5.0, 10.0, 20.0}).forEach(d -> {
            current.put(d, Double.valueOf(0));
        });
        if (base > 0 && current.containsKey(base)) {
            current.replace(base, 1.0);
        }
        currentValid = true;
        return current;
    }

    public void partition(int n) {
        partition(n, n, "", 0.0);
    }

    int depth = 0;
    public void partition(int n, int max, String prefix, Double base) {
        ++depth;
        log.debug("n: {} max: {} prefix: {} base: {}", n, max, prefix, base);
        Double d = current.get(Double.valueOf(max));
        if (d != null) {
            current.replace(Double.valueOf(max), ++d);
        } else {
            currentValid = false;
        }
        if (n == 0) {
            log.debug(prefix);
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
                log.debug("Depth: {}", depth);
                base = Double.valueOf(i);
                currentValid = true;
            }
            partition(n - i, i, prefix + " " + i, base);
            --depth;
        }

        return;
    }

    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        Partition p = new Partition(N);
        p.partition(N);
        p.list();
    }

    private void list() {
        results.forEach(mdd ->{
            log.debug(mdd.toString());
        });
    }

    private class ValidatingMap<K extends Double, V extends Double> extends HashMap<K, V> {
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