package edu.itc.canteen.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Lab-01 challenge: the subsidy failure seen from the purchase and the daily settlement. */
class SettlementImpactTest {

    private static Instant at(String isoLocal) {
        return LocalDateTime.parse(isoLocal).atZone(SubsidyPolicy.ZONE).toInstant();
    }

    private static Card scholarshipStudent(long serial) {
        Card card = Card.issue(CardNumber.of(serial), CardType.STUDENT, true,
                LocalDate.of(2027, 12, 31));
        card.activate("1234");
        return card;
    }

    @Test
    void aMealBoughtAtTheFailingInstantIsChargedInFull() {
        DailyLedger ledger = new DailyLedger();
        PurchaseService service = new PurchaseService(
                new SubsidyPolicy(new AcademicCalendar.Default()), ledger);
        Wallet wallet = new Wallet(50_000);

        Receipt receipt = service.purchase(
                scholarshipStudent(20270001L),
                wallet,
                "S1",
                List.of(Menu.KUY_TEAV),
                at("2027-03-15T14:00:00"));

        assertThat(receipt.subsidy()).isZero();
        assertThat(receipt.charged()).isEqualTo(7_000);
        assertThat(wallet.balance()).isEqualTo(43_000);
    }

    @Test
    void theInstituteOwesNoSubsidyForMealsBoughtAtTheFailingInstant() {
        DailyLedger ledger = new DailyLedger();
        PurchaseService service = new PurchaseService(
                new SubsidyPolicy(new AcademicCalendar.Default()), ledger);
        List<Transaction> day = new ArrayList<>();
        for (long serial = 1; serial <= 300; serial++) {
            Wallet wallet = new Wallet(50_000);
            Receipt receipt = service.purchase(
                    scholarshipStudent(serial),
                    wallet,
                    "S1",
                    List.of(Menu.KUY_TEAV),
                    at("2027-03-15T14:00:00"));
            day.add(Transaction.purchase(receipt));
        }

        Settlement.Report report = Settlement.settle(day);

        assertThat(report.subsidiesOwed()).isZero();
        assertThat(report.purchasesCharged()).isEqualTo(2_100_000);
        assertThat(report.stalls().getFirst().sales()).isEqualTo(2_100_000);
    }
}
