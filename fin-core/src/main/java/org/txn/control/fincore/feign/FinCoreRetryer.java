package org.txn.control.fincore.feign;

import feign.RetryableException;
import feign.Retryer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter@RequiredArgsConstructor
public class FinCoreRetryer implements Retryer {

    private final long period;
    private final long maxPeriod;
    private final long maxAttempts;
    private int attempt = 1;

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt >= maxAttempts) {
            throw e;
        }
        if (e.status() == 500) {
            attempt++;
            try {
                long delay = Math.min(period * attempt, maxPeriod);
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw e;
            }
        } else if (e.status() == 400) {
            throw e;
        }
    }

    @Override
    public Retryer clone() {
        return new FinCoreRetryer(period, maxPeriod, maxAttempts);
    }
}
