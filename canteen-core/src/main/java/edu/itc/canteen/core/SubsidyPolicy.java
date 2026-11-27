// SubsidyPolicy.java
package edu.itc.canteen.core;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Meal subsidy for scholarship students: one subsidised meal per day, during lunch hours on a teaching day,
 * for the first MEAL item of the purchase, capped at SUBSIDY_CAP.
 */
public final class SubsidyPolicy {
    public static final ZoneId ZONE = ZoneId.of("Asia/Phnom_Penh");
    public static final LocalTime LUNCH_START = LocalTime.of(11, 0);   // inclusive
    public static final LocalTime LUNCH_END = LocalTime.of(14, 0);     // exclusive
    public static final long SUBSIDY_CAP = 4_000;

    private final AcademicCalendar calendar;

    public SubsidyPolicy(AcademicCalendar calendar) {
        this.calendar = calendar;
    }

    /** The subsidy in riel for this purchase, or 0 when any condition fails. */
    public long subsidy(Card card, List<Item> items, Instant at, boolean alreadyUsedToday) {
        if (card.type() != CardType.STUDENT || !card.scholarship()) return 0;
        if (alreadyUsedToday) return 0;
        ZonedDateTime local = at.atZone(ZONE);
        if (!calendar.isTeachingDay(local.toLocalDate())) return 0;
        LocalTime t = local.toLocalTime();
        if (t.isBefore(LUNCH_START) || !t.isBefore(LUNCH_END)) return 0;
        for (Item item : items) {
            if (item.category() == Category.MEAL) return Math.min(item.price(), SUBSIDY_CAP);
        }
        return 0;
    }
}