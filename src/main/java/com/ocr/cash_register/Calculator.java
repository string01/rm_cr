package com.ocr.cash_register;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Class to encapsulate the core calculation methods for the CashRegister.
 */
@Component
@Slf4j
public class Calculator {

    @Autowired
    protected CashDrawerFactory cashDrawerFactory;


    public RequestResult makeChange(CashDrawer registerDrawer, CashDrawer requestedDrawer) {
        Double amt = requestedDrawer.getTotal();
        return search(registerDrawer, amt, Denomination.TWENTY);
    }

    private RequestResult search(CashDrawer registerDrawer, final Double amt, Denomination startingDenomination) {
        CashDrawer reqCd = cashDrawerFactory.createEmpty();
        CashDrawer remainCd = cashDrawerFactory.createEmpty();
        if (searchInner(registerDrawer, amt, startingDenomination, reqCd, remainCd)) {
            // XXX DL ?
            return null;
        }
        log.debug("Done");
        return new RequestResult(reqCd, remainCd);
    }

    private boolean searchInner(CashDrawer registerDrawer, Double amt, Denomination startingDenomination, CashDrawer reqCd, CashDrawer remainCd) {
        double resAmt = amt;
        Optional<Accumulator> oa = registerDrawer.getAccumulators().values().stream().filter(d -> d.getDenomination().
                equals(startingDenomination)).findFirst();

        if (!oa.isPresent()){
            return true; // XXX DL I think this happens when we hit Denomination.ONE and it is empty.
            // XXX Need to trap this condition.
        }
        Accumulator a = oa.get();
        if (a.getDenomination().equals(Denomination.ZERO)){
            return true;
        }
        // get diff from amt next multiple of a units
        double d = amt / a.unitValue();
        SubtractionResult sr = a.subtract(new Accumulator(a.getDenomination(), (int) d));
        Accumulator amtTaken = sr.getAmtTaken();
        Accumulator remaining = sr.getAccumulator();
        reqCd.add(amtTaken);
        remainCd.add(remaining);
        resAmt -= amtTaken.getTotal();
        if (resAmt > 0){
            searchInner(registerDrawer, resAmt, a.getDenomination().getNextLowest(), reqCd, remainCd);
        }
        return false;
    }

    public RequestResult subsetMatch(CashDrawer cashDrawer, CashDrawer req){
        CashDrawer change = cashDrawerFactory.createEmpty();
        CashDrawer remaining = cashDrawerFactory.createEmpty();
        for (Accumulator a: req.getAccumulators().values()){
            Accumulator ax = cashDrawer.getAccumulatorFor(a.getDenomination());
            if (ax.getNumberOfUnits() >= a.getNumberOfUnits()){
                SubtractionResult sr = ax.subtract(a);
                change.add(sr.getAmtTaken());
                remaining.add(sr.getAccumulator());
            } else {
                change.add(Accumulator.createEmpty(a));
                remaining.add(Accumulator.createEmpty(a));
            }
        }
        return new RequestResult(change, remaining);
    }

    @Data
    class RequestResult {
        private CashDrawer result;
        private CashDrawer remaining;

        public RequestResult(CashDrawer result, CashDrawer remaining) {
            this.remaining = remaining;
            this.result = result;
        }
    }
}
