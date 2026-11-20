package edu.itc.canteen.core;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/** Tells whether a date is a teaching day. The default calendar is fictional and fixed for the course. */
public interface AcademicCalendar {

    boolean isTeachingDay(LocalDate date);

    /** Monday to Friday, except the listed holidays of 2027. */
    final class Default implements AcademicCalendar {
        public static final Set<LocalDate> HOLIDAYS_2027 = Set.of(
            LocalDate.of(2027, 1, 7),                                         // Victory over Genocide Day
            LocalDate.of(2027, 4, 14), LocalDate.of(2027, 4, 15), LocalDate.of(2027, 4, 16),   // Khmer New Year
            LocalDate.of(2027, 5, 1),                                         // Labour Day
            LocalDate.of(2027, 9, 24),                                        // Constitution Day
            LocalDate.of(2027, 9, 30), LocalDate.of(2027, 10, 1), LocalDate.of(2027, 10, 2),   // Pchum Ben
            LocalDate.of(2027, 11, 9),                                        // Independence Day
            LocalDate.of(2027, 11, 13), LocalDate.of(2027, 11, 14), LocalDate.of(2027, 11, 15)  // Water Festival
        );

        @Override
        public boolean isTeachingDay(LocalDate date) {
            DayOfWeek d = date.getDayOfWeek();
            if (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) return false;
            return !HOLIDAYS_2027.contains(date);
        }
    }
}
