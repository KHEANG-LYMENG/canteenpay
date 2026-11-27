// Card.java
package edu.itc.canteen.core;

import java.time.LocalDate;

/**
 * A meal card and its life cycle.
 *
 * <pre>
 * ISSUED  --activate(pin)-->  ACTIVE
 * ACTIVE  --3 wrong PINs-->   BLOCKED   --unblock(newPin)-->  ACTIVE
 * ACTIVE | BLOCKED --reportLost()--> LOST
 * ACTIVE  --expireIfDue(today after expiresOn)--> EXPIRED
 * LOST | EXPIRED --markReplaced()--> REPLACED   (balance moves to the replacement card)
 * ACTIVE | BLOCKED | EXPIRED --close()--> CLOSED
 * </pre>
 * Every other transition throws IllegalStateException.
 */
public final class Card {
    public static final int MAX_PIN_ATTEMPTS = 3;

    private final String number;
    private final CardType type;
    private final boolean scholarship;
    private final LocalDate expiresOn;
    private String pin;
    private int failedAttempts;
    private CardState state = CardState.ISSUED;

    private Card(String number, CardType type, boolean scholarship, LocalDate expiresOn) {
        this.number = number;
        this.type = type;
        this.scholarship = scholarship;
        this.expiresOn = expiresOn;
    }

    /** Issues a card. Only STUDENT cards may carry a scholarship. */
    public static Card issue(String number, CardType type, boolean scholarship, LocalDate expiresOn) {
        if (!CardNumber.isValid(number)) throw new IllegalArgumentException("invalid card number: " + number);
        if (type == null || expiresOn == null) throw new IllegalArgumentException("type and expiry are required");
        if (scholarship && type != CardType.STUDENT) throw new IllegalArgumentException("only students have scholarships");
        return new Card(number, type, scholarship, expiresOn);
    }

    public void activate(String pin) {
        require(CardState.ISSUED, "activate");
        this.pin = validPin(pin);
        this.failedAttempts = 0;
        this.state = CardState.ACTIVE;
    }

    /**
     * Checks the PIN of an ACTIVE card. A wrong PIN counts an attempt; the third wrong attempt in a row
     * blocks the card. A correct PIN resets the counter. An expired card is moved to EXPIRED and rejected.
     */
    public boolean verifyPin(String attempt, LocalDate today) {
        expireIfDue(today);
        if (state == CardState.EXPIRED) return false;
        require(CardState.ACTIVE, "verifyPin");
        if (pin.equals(attempt)) {
            failedAttempts = 0;
            return true;
        }
        failedAttempts++;
        if (failedAttempts >= MAX_PIN_ATTEMPTS) state = CardState.BLOCKED;
        return false;
    }

    public void unblock(String newPin) {
        require(CardState.BLOCKED, "unblock");
        this.pin = validPin(newPin);
        this.failedAttempts = 0;
        this.state = CardState.ACTIVE;
    }

    public void reportLost() {
        if (state != CardState.ACTIVE && state != CardState.BLOCKED) throw new IllegalStateException("reportLost from " + state);
        state = CardState.LOST;
    }

    /** ACTIVE to EXPIRED when today is after the expiry date (the card is still valid on expiresOn). */
    public void expireIfDue(LocalDate today) {
        if (state == CardState.ACTIVE && today.isAfter(expiresOn)) state = CardState.EXPIRED;
    }

    public void markReplaced() {
        if (state != CardState.LOST && state != CardState.EXPIRED) throw new IllegalStateException("markReplaced from " + state);
        state = CardState.REPLACED;
    }

    public void close() {
        if (state != CardState.ACTIVE && state != CardState.BLOCKED && state != CardState.EXPIRED) throw new IllegalStateException("close from " + state);
        state = CardState.CLOSED;
    }

    public String number() { return number; }
    public CardType type() { return type; }
    public boolean scholarship() { return scholarship; }
    public LocalDate expiresOn() { return expiresOn; }
    public CardState state() { return state; }
    public int failedAttempts() { return failedAttempts; }

    private void require(CardState expected, String op) {
        if (state != expected) throw new IllegalStateException(op + " from " + state);
    }

    private static String validPin(String pin) {
        if (pin == null || !pin.matches("\\d{4}")) throw new IllegalArgumentException("PIN must be 4 digits");
        return pin;
    }
}