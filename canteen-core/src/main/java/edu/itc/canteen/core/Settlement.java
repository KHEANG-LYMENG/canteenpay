// Settlement.java
package edu.itc.canteen.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Daily settlement: what each stall is owed, what the institute pays in subsidies, what the holders paid in
 * top-ups and fees, and the balance check: credited top-ups - purchases + refunds must equal the change of
 * all card balances over the day.
 */
public final class Settlement {

    public record StallLine(String stallId, long sales, long subsidyOwed, long refunds, long payout) {}

    public record Report(List<StallLine> stalls, long topUpsPaid, long fees, long topUpsCredited,
                         long purchasesCharged, long subsidiesOwed, long refunds, long expectedBalanceChange) {}

    private Settlement() {}

    public static Report settle(List<Transaction> transactions) {
        Map<String, long[]> perStall = new LinkedHashMap<>();   // [charged, subsidy, refunds]
        long topUpsPaid = 0, fees = 0, credited = 0, charged = 0, subsidies = 0, refunds = 0;
        for (Transaction t : transactions) {
            switch (t.kind()) {
                case TOP_UP -> {
                    topUpsPaid += t.amount();
                    fees += t.fee();
                    credited += t.amount() - t.fee();
                }
                case PURCHASE -> {
                    charged += t.amount();
                    subsidies += t.subsidy();
                    long[] s = perStall.computeIfAbsent(t.stallId(), k -> new long[3]);
                    s[0] += t.amount();
                    s[1] += t.subsidy();
                }
                case REFUND -> {
                    refunds += t.amount();
                    long[] s = perStall.computeIfAbsent(t.stallId(), k -> new long[3]);
                    s[2] += t.amount();
                }
            }
        }
        List<StallLine> lines = new ArrayList<>();
        for (var e : perStall.entrySet()) {
            long[] s = e.getValue();
            long sales = s[0] + s[1];                 // gross: what the card paid plus what the institute pays
            lines.add(new StallLine(e.getKey(), sales, s[1], s[2], sales - s[2]));
        }
        return new Report(List.copyOf(lines), topUpsPaid, fees, credited, charged, subsidies, refunds,
            credited - charged + refunds);
    }

    /** True when the report explains the observed change of the sum of all balances. */
    public static boolean balances(Report report, long openingBalances, long closingBalances) {
        return closingBalances - openingBalances == report.expectedBalanceChange();
    }
}