package edu.itc.canteen.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** In-memory record of what each card spent per day, used for the daily limit and the once-a-day subsidy. */
public final class DailyLedger {
    private record Entry(String receiptId, String cardNumber, LocalDate day, long charged, boolean subsidised, boolean reversed) {}

    private final List<Entry> entries = new ArrayList<>();

    public synchronized long spentOn(String cardNumber, LocalDate day) {
        long sum = 0;
        for (Entry e : entries) {
            if (!e.reversed && e.cardNumber.equals(cardNumber) && e.day.equals(day)) sum += e.charged;
        }
        return sum;
    }

    public synchronized boolean subsidyUsedOn(String cardNumber, LocalDate day) {
        for (Entry e : entries) {
            if (!e.reversed && e.subsidised && e.cardNumber.equals(cardNumber) && e.day.equals(day)) return true;
        }
        return false;
    }

    public synchronized void record(Receipt receipt) {
        LocalDate day = receipt.at().atZone(SubsidyPolicy.ZONE).toLocalDate();
        entries.add(new Entry(receipt.receiptId(), receipt.cardNumber(), day, receipt.charged(), receipt.subsidy() > 0, false));
    }

    /** A refund gives the allowance (and the subsidy, if it was used) back for that day. */
    public synchronized void reverse(String receiptId) {
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            if (e.receiptId.equals(receiptId)) {
                entries.set(i, new Entry(e.receiptId, e.cardNumber, e.day, e.charged, e.subsidised, true));
                return;
            }
        }
        throw new IllegalArgumentException("unknown receipt " + receiptId);
    }

    public synchronized int size() { return entries.size(); }
}
