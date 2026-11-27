// PurchaseRejectedException.java
package edu.itc.canteen.core;

/** A purchase that the rules refuse. The reason says which rule. */
public final class PurchaseRejectedException extends RuntimeException {
    public enum Reason { CARD_NOT_ACTIVE, EMPTY_BASKET, DAILY_LIMIT, INSUFFICIENT_BALANCE }

    private final Reason reason;

    public PurchaseRejectedException(Reason reason, String message) {
        super(reason + ": " + message);
        this.reason = reason;
    }

    public Reason reason() { return reason; }
}