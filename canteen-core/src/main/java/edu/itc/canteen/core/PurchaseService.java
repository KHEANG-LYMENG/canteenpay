package edu.itc.canteen.core;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** Applies the purchase rules: active card, subsidy, daily limit, balance. */
public final class PurchaseService {
    private final SubsidyPolicy subsidyPolicy;
    private final DailyLedger ledger;
    private final AtomicLong sequence = new AtomicLong();

    public PurchaseService(SubsidyPolicy subsidyPolicy, DailyLedger ledger) {
        this.subsidyPolicy = subsidyPolicy;
        this.ledger = ledger;
    }

    public Receipt purchase(Card card, Wallet wallet, String stallId, List<Item> items, Instant at) {
        if (items == null || items.isEmpty()) {
            throw new PurchaseRejectedException(PurchaseRejectedException.Reason.EMPTY_BASKET, "no items");
        }
        LocalDate day = at.atZone(SubsidyPolicy.ZONE).toLocalDate();
        card.expireIfDue(day);
        if (card.state() != CardState.ACTIVE) {
            throw new PurchaseRejectedException(PurchaseRejectedException.Reason.CARD_NOT_ACTIVE, "card is " + card.state());
        }
        long total = 0;
        for (Item item : items) total += item.price();
        long subsidy = subsidyPolicy.subsidy(card, items, at, ledger.subsidyUsedOn(card.number(), day));
        long charged = total - subsidy;
        long limit = DailyLimitPolicy.limit(card.type());
        long spent = ledger.spentOn(card.number(), day);
        if (spent + charged > limit) {
            throw new PurchaseRejectedException(PurchaseRejectedException.Reason.DAILY_LIMIT,
                "spent " + spent + " + " + charged + " > " + limit);
        }
        if (!wallet.charge(charged)) {
            throw new PurchaseRejectedException(PurchaseRejectedException.Reason.INSUFFICIENT_BALANCE,
                "balance " + wallet.balance() + " < " + charged);
        }
        String id = "R-" + String.format("%06d", sequence.incrementAndGet());
        Receipt receipt = new Receipt(id, card.number(), stallId, at, items, total, subsidy, charged, limit - spent - charged);
        ledger.record(receipt);
        return receipt;
    }
}
