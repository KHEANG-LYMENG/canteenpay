// Wallet.java
package edu.itc.canteen.core;

/**
 * The prepaid balance of a card, in whole riel. All operations are atomic: the kiosk and the app may
 * act on the same wallet at the same moment.
 */
public final class Wallet {
    public static final long MAX_BALANCE = 1_000_000;

    private long balance;

    public Wallet() { this(0); }

    public Wallet(long openingBalance) {
        if (openingBalance < 0 || openingBalance > MAX_BALANCE) throw new IllegalArgumentException("opening balance");
        this.balance = openingBalance;
    }

    public synchronized long balance() { return balance; }

    /** Adds amount; fails when the balance would exceed MAX_BALANCE. */
    public synchronized void credit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        if (balance + amount > MAX_BALANCE) throw new IllegalStateException("balance would exceed " + MAX_BALANCE);
        balance += amount;
    }

    /** Takes amount when the balance covers it. Returns false (and changes nothing) otherwise. */
    public synchronized boolean charge(long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    /** Moves the whole balance to another wallet (card replacement). */
    public void transferTo(Wallet target) {
        if (target == this) throw new IllegalArgumentException("same wallet");
        long moved;
        synchronized (this) {
            moved = balance;
            balance = 0;
        }
        if (moved > 0) target.credit(moved);
    }
}