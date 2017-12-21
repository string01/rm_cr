package com.ocr.cash_register;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class Calculator {

    @Autowired
    protected CashDrawerFactory cashDrawerFactory;

    @Deprecated
    public RequestResult makeChangeOld(CashDrawer registerDrawer, CashDrawer requestedDrawer) {

        // check if amt in register is > amt in req.

        // start at top of stack
        // ask for number of that denom
        // registerDrawer can recurse down it's stack to make that amt
        // return a new register with those slots filled. and a drawer with remainder.
        // subtract that amt from requested drawer
        // find next non-zero Accumulator in reqestDrawer
        // ask for amt of that denom
        // return drawer with those slots filled.
        // Recurse down the stack of denominations.
        CashDrawer reqCd = cashDrawerFactory.createEmpty();
        CashDrawer remainCd = cashDrawerFactory.createEmpty();

        for (Accumulator a : registerDrawer.getAccumulators()) {
            Optional<Accumulator> o = requestedDrawer.getAccumulators().stream().filter(x -> a.getDenomination().equals(x.getDenomination())).findFirst();
            if (!o.isPresent()){
                throw new IllegalStateException();
            }

            SubtractionResult requestResult = a.subtract(o.get());
            Accumulator remaining = requestResult.getAccumulator();
            Accumulator amtTaken = requestResult.getAmtTaken();
            reqCd.add(amtTaken);
            remainCd.add(remaining);
        }
        return new RequestResult(reqCd, remainCd);
    }

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
        Optional<Accumulator> oa = registerDrawer.getAccumulators().stream().filter(d -> d.getDenomination().
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
        double d = amt.doubleValue() / a.unitValue();
        SubtractionResult sr = a.subtract(new Accumulator(a.getDenomination(), Integer.valueOf((int) d)));
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
        CashDrawer res = cashDrawerFactory.createEmpty();
        CashDrawer rem = cashDrawerFactory.createEmpty();
        for (Accumulator a: req.getAccumulators()){
            Accumulator ax = cashDrawer.getAccumulatorFor(a.getDenomination());

            if (a.getNumberOfUnits() >= ax.getNumberOfUnits()){
                SubtractionResult sr = ax.subtract(a);
                res.add(sr.getAmtTaken());
                rem.add(sr.getAccumulator());
            }
        }
        return new RequestResult(res, rem);
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
