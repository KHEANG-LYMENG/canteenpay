// DailyLimitPolicy.java
package edu.itc.canteen.core;

/** How much a card may spend per calendar day (Asia/Phnom_Penh), in riel. */
public final class DailyLimitPolicy {
    public static final long STUDENT_LIMIT = 30_000;
    public static final long STAFF_LIMIT = 60_000;
    public static final long GUEST_LIMIT = 20_000;

    private DailyLimitPolicy() {}

    public static long limit(CardType type) {
        return switch (type) {
            case STUDENT -> STUDENT_LIMIT;
            case STAFF -> STAFF_LIMIT;
            case GUEST -> GUEST_LIMIT;
        };
    }
}