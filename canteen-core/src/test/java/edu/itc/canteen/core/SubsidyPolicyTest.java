package edu.itc.canteen.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Lab-01 Task 5: the meal subsidy, one test per clause of rule SUB-1. */
class SubsidyPolicyTest {

    private final SubsidyPolicy policy = new SubsidyPolicy(new AcademicCalendar.Default());
    private final List<Item> kuyTeav = List.of(Menu.KUY_TEAV);

    /** Phnom Penh local time to an Instant. */
    private static Instant at(String isoLocal) {
        return LocalDateTime.parse(isoLocal).atZone(SubsidyPolicy.ZONE).toInstant();
    }

    private static Card scholarshipStudent() {
        Card card = Card.issue("CP-20270001-9", CardType.STUDENT, true,
                LocalDate.of(2027, 12, 31));
        card.activate("1234");
        return card;
    }

    @Test
    void mealAtLunchOnATeachingDayIsSubsidisedUpToTheCap() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-03-15T12:30:00"), false);
        assertThat(s).isEqualTo(4_000);
    }

    @Test
    void aMealCheaperThanTheCapIsSubsidisedAtItsPrice() {
        Item bobor = new Item("M09", "Bobor", 3_000, Category.MEAL);
        long s = policy.subsidy(scholarshipStudent(), List.of(bobor), at("2027-03-15T12:30:00"), false);
        assertThat(s).isEqualTo(3_000);
    }

    @Test
    void oneSecondBeforeLunchStartGivesNothing() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-03-15T10:59:59"), false);
        assertThat(s).isZero();
    }

    @Test
    void lunchStartIsInclusive() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-03-15T11:00:00"), false);
        assertThat(s).isEqualTo(4_000);
    }

    @Test
    void oneSecondBeforeLunchEndIsStillSubsidised() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-03-15T13:59:59"), false);
        assertThat(s).isEqualTo(4_000);
    }

    @Test
    void lunchEndIsExclusive() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-03-15T14:00:00"), false);
        assertThat(s).isZero();
    }

    @Test
    void saturdayIsNotATeachingDay() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-03-20T12:00:00"), false);
        assertThat(s).isZero();
    }

    @Test
    void khmerNewYearIsNotATeachingDay() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-04-15T12:00:00"), false);
        assertThat(s).isZero();
    }

    @Test
    void onlyOneSubsidyPerDay() {
        long s = policy.subsidy(scholarshipStudent(), kuyTeav, at("2027-03-15T12:00:00"), true);
        assertThat(s).isZero();
    }

    @Test
    void aBasketWithoutAMealGetsNothing() {
        List<Item> noMeal = List.of(Menu.ICED_COFFEE, Menu.NUM_ANSOM);
        long s = policy.subsidy(scholarshipStudent(), noMeal, at("2027-03-15T12:30:00"), false);
        assertThat(s).isZero();
    }

    @Test
    void theFirstMealItemCountsEvenAfterADrink() {
        List<Item> drinkThenMeal = List.of(Menu.ICED_COFFEE, Menu.MI_CHAR);
        long s = policy.subsidy(scholarshipStudent(), drinkThenMeal, at("2027-03-15T12:30:00"), false);
        assertThat(s).isEqualTo(4_000);
    }

    @Test
    void aStudentWithoutScholarshipGetsNothing() {
        Card regularStudent = Card.issue("CP-20270002-7", CardType.STUDENT, false,
                LocalDate.of(2027, 12, 31));
        regularStudent.activate("0000");
        long s = policy.subsidy(regularStudent, kuyTeav, at("2027-03-15T12:30:00"), false);
        assertThat(s).isZero();
    }
}
