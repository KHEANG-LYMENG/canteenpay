// RefundPolicy.java
package edu.itc.canteen.core;

import java.time.Duration;
import java.time.Instant;

/**
 * Refund of a purchase cancelled by the stall.
 * Within FULL_REFUND_WINDOW (inclusive): the full charged amount.
 * After that and before LATE_REFUND_WINDOW (exclusive): charged minus LATE_FEE, never below 0.
 * From LATE_REFUND_WINDOW on: nothing.
 * A kiosk failure after the card was charged: always the full charged amount.
 */
public final class RefundPolicy {
    public static final Duration FULL_REFUND_WINDOW = Duration.ofMinutes(15);
    public static final Duration LATE_REFUND_WINDOW = Duration.ofHours(2);
    public static final long LATE_FEE = 500;

    public long refund(Receipt receipt, Instant cancelledAt, boolean kioskFailure) {
        if (kioskFailure) return receipt.charged();
        Duration elapsed = Duration.between(receipt.at(), cancelledAt);
        if (elapsed.isNegative()) throw new IllegalArgumentException("cancelled before purchase");
        if (elapsed.compareTo(FULL_REFUND_WINDOW) <= 0) return receipt.charged();
        if (elapsed.compareTo(LATE_REFUND_WINDOW) < 0) return Math.max(0, receipt.charged() - LATE_FEE);
        return 0;
    }
}