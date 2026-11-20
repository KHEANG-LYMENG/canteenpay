package edu.itc.canteen.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Lab-01 Challenge: the financial impact of the seeded fault on daily settlement. */
class SettlementImpactTest {

    private final SubsidyPolicy subsidyPolicy = new SubsidyPolicy(new AcademicCalendar.Default());
    private final DailyLedger ledger = new DailyLedger();
    private final PurchaseService purchaseService = new PurchaseService(subsidyPolicy, ledger);

    private static Instant at(String isoLocal) {
        return LocalDateTime.parse(isoLocal).atZone(SubsidyPolicy.ZONE).toInstant();
    }

    private static Card scholarshipStudent(String number) {
        Card card = Card.issue(number, CardType.STUDENT, true, LocalDate.of(2027, 12, 31));
        card.activate("1234");
        return card;
    }

    @Test
    void aMealBoughtAtTheFailingInstantIsChargedInFull() {
        Card card = scholarshipStudent("CP-20270001-9");
        Wallet wallet = new Wallet(50_000);
        Receipt receipt = purchaseService.purchase(card, wallet, "S1", List.of(Menu.KUY_TEAV), at("2027-03-15T14:00:00"));
        assertThat(receipt.subsidy()).isEqualTo(0L);
        assertThat(receipt.charged()).isEqualTo(7_000L);
    }

    @Test
    void theInstituteOwesNoSubsidyForMealsBoughtAtTheFailingInstant() {
        List<Transaction> transactions = new ArrayList<>();
        Instant failingInstant = at("2027-03-15T14:00:00");
        for (int i = 1; i <= 300; i++) {
            Card card = scholarshipStudent(CardNumber.of(20270000 + i));
            Wallet wallet = new Wallet(50_000);
            Receipt receipt = purchaseService.purchase(card, wallet, "S1", List.of(Menu.KUY_TEAV), failingInstant);
            transactions.add(Transaction.purchase(receipt));
        }
        Settlement.Report report = Settlement.settle(transactions);
        assertThat(report.subsidiesOwed()).isEqualTo(0L);
    }
}
