// Transaction.java
package edu.itc.canteen.core;

import java.time.Instant;

/**
 * One money movement of the day, as the settlement sees it.
 * TOP_UP: amount = riel paid by the holder, fee = channel fee (credited = amount - fee), stallId = null.
 * PURCHASE: amount = charged to the card, subsidy = riel the institute owes the stall, stallId = the stall.
 * REFUND: amount = riel credited back to the card, stallId = the stall that cancelled.
 */
public record Transaction(Kind kind, Instant at, String cardNumber, String stallId, long amount, long subsidy, long fee) {
    public enum Kind { TOP_UP, PURCHASE, REFUND }

    public static Transaction topUp(Instant at, String card, TopUpPolicy.TopUp t) {
        return new Transaction(Kind.TOP_UP, at, card, null, t.amount(), 0, t.fee());
    }

    public static Transaction purchase(Receipt r) {
        return new Transaction(Kind.PURCHASE, r.at(), r.cardNumber(), r.stallId(), r.charged(), r.subsidy(), 0);
    }

    public static Transaction refund(Instant at, Receipt r, long refunded) {
        return new Transaction(Kind.REFUND, at, r.cardNumber(), r.stallId(), refunded, 0, 0);
    }
}