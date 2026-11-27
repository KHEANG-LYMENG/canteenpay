// RefundService.java
package edu.itc.canteen.core;

import java.time.Instant;

/** Refunds a receipt: credits the wallet and gives the day's allowance and subsidy back. */
public final class RefundService {
    private final RefundPolicy policy;
    private final DailyLedger ledger;

    public RefundService(RefundPolicy policy, DailyLedger ledger) {
        this.policy = policy;
        this.ledger = ledger;
    }

    /** Returns the amount refunded (0 when the policy gives nothing; the ledger is still reversed). */
    public long refund(Receipt receipt, Wallet wallet, Instant cancelledAt, boolean kioskFailure) {
        long amount = policy.refund(receipt, cancelledAt, kioskFailure);
        ledger.reverse(receipt.receiptId());
        if (amount > 0) wallet.credit(amount);
        return amount;
    }
}