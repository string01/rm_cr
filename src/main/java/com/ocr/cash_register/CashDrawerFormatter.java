package com.ocr.cash_register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper class to create a formatted input from a discrete set of strings.
 * Also take a CashDrawer instance and format the output.
 */
@Component
@Slf4j
public class CashDrawerFormatter {
    
    public String fromInput(String d20, String d10, String d5, String d2, String d1) {
        StringBuilder sb = new StringBuilder();
        sb.append(d20).append(" ").append(d10).append(" ").append(d5).append(" ").append(d2).append(" ").append(d1);
        String fmt = sb.toString();
        return fmt;
    }
    
    public String toOutput(CashDrawer cashDrawer) {
        return toOutput(cashDrawer, false);
    }
    
    public String toOutput(CashDrawer cashDrawer, boolean raw) {
        StringBuilder sb = new StringBuilder();
        if (!raw) {
            sb.append("$").append(cashDrawer.getTotal().longValue()).append(" ");
        }
        for (Accumulator a : cashDrawer.getAccumulators().values()) {
            sb.append(a.getNumberOfUnits()).append(" ");
        }
        log.debug("Output: {}", sb.toString());
        return sb.toString().trim();
    }
}
