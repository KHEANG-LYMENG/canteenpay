// Receipt.java
package edu.itc.canteen.core;

import java.time.Instant;
import java.util.List;

/** The result of a successful purchase. charged = total - subsidy. */
public record Receipt(String receiptId, String cardNumber, String stallId, Instant at, List<Item> items,
                      long total, long subsidy, long charged, long remainingAllowance) {
    public Receipt {
        items = List.copyOf(items);
    }
}