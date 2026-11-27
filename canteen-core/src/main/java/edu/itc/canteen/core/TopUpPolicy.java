// TopUpPolicy.java
package edu.itc.canteen.core;

/**
 * Top-up rules. Amounts are whole riel, multiples of 100, between MIN_AMOUNT and MAX_AMOUNT.
 * The fee depends on the channel; the wallet is credited with amount minus fee.
 */
public final class TopUpPolicy {
    public static final long MIN_AMOUNT = 2_000;
    public static final long MAX_AMOUNT = 400_000;
    public static final long STEP = 100;
    public static final long PARENT_APP_FEE_CAP = 2_000;

    public record TopUp(Channel channel, long amount, long fee, long credited) {}

    /**
     * KHQR: 0.5 % of the amount, rounded up to the next 100 riel.
     * CASH_DESK: no fee.
     * PARENT_APP: 1 % of the amount, rounded down to 100 riel, capped at PARENT_APP_FEE_CAP.
     */
    public long fee(Channel channel, long amount) {
        if (channel == null) throw new IllegalArgumentException("channel");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        return switch (channel) {
            case KHQR -> {
                long raw = Math.ceilDiv(amount, 200);           // 0.5 % = amount / 200
                yield Math.ceilDiv(raw, 100) * 100;             // up to the next 100
            }
            case CASH_DESK -> 0;
            case PARENT_APP -> {
                long raw = amount / 100;                        // 1 %
                long fee = (raw / 100) * 100;                   // down to 100
                yield Math.min(fee, PARENT_APP_FEE_CAP);
            }
        };
    }

    /** Validates the request, computes the fee and credits the wallet. */
    public TopUp topUp(Card card, Wallet wallet, Channel channel, long amount) {
        if (card.state() != CardState.ACTIVE) throw new IllegalStateException("card is " + card.state());
        if (amount % STEP != 0) throw new IllegalArgumentException("amount must be a multiple of " + STEP);
        if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) throw new IllegalArgumentException("amount out of range");
        long fee = fee(channel, amount);
        long credited = amount - fee;
        wallet.credit(credited);                                 // IllegalStateException above MAX_BALANCE
        return new TopUp(channel, amount, fee, credited);
    }
}