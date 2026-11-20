package edu.itc.canteen.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Lab-01 Task 4: the top-up fee rules, from the worked examples of the brief. */
class TopUpPolicyTest {

    private final TopUpPolicy policy = new TopUpPolicy();

    private static Card activeCard() {
        Card card = Card.issue("CP-20270001-9", CardType.STUDENT, true,
                LocalDate.of(2027, 12, 31));
        card.activate("1234");
        return card;
    }

    @Test
    void khqrFeeIsHalfPercentRoundedUpToTheNext100() {
        assertThat(policy.fee(Channel.KHQR, 2_000)).isEqualTo(100);
        assertThat(policy.fee(Channel.KHQR, 20_000)).isEqualTo(100);
        assertThat(policy.fee(Channel.KHQR, 25_000)).isEqualTo(200);
        assertThat(policy.fee(Channel.KHQR, 40_000)).isEqualTo(200);
        assertThat(policy.fee(Channel.KHQR, 40_100)).isEqualTo(300);
        assertThat(policy.fee(Channel.KHQR, 400_000)).isEqualTo(2_000);
    }

    @Test
    void parentAppFeeIsOnePercentRoundedDownAndCappedAt2000() {
        assertThat(policy.fee(Channel.PARENT_APP, 2_000)).isEqualTo(0);
        assertThat(policy.fee(Channel.PARENT_APP, 9_900)).isEqualTo(0);
        assertThat(policy.fee(Channel.PARENT_APP, 10_000)).isEqualTo(100);
        assertThat(policy.fee(Channel.PARENT_APP, 25_000)).isEqualTo(200);
        assertThat(policy.fee(Channel.PARENT_APP, 199_900)).isEqualTo(1_900);
        assertThat(policy.fee(Channel.PARENT_APP, 200_000)).isEqualTo(2_000);
        assertThat(policy.fee(Channel.PARENT_APP, 300_000)).isEqualTo(2_000);
    }

    @Test
    void cashDeskHasNoFee() {
        assertThat(policy.fee(Channel.CASH_DESK, 2_000)).isZero();
        assertThat(policy.fee(Channel.CASH_DESK, 400_000)).isZero();
    }

    @Test
    void khqrTopUpCreditsTheAmountMinusTheFee() {
        Card card = activeCard();
        Wallet wallet = new Wallet();
        TopUpPolicy.TopUp topUp = policy.topUp(card, wallet, Channel.KHQR, 20_000);
        assertThat(topUp.fee()).isEqualTo(100);
        assertThat(topUp.credited()).isEqualTo(19_900);
        assertThat(wallet.balance()).isEqualTo(19_900);
    }

    @Test
    void amountsOutsideTheRangeOrOffTheStepAreRejected() {
        Card card = activeCard();
        Wallet wallet = new Wallet();
        assertThatThrownBy(() -> policy.topUp(card, wallet, Channel.KHQR, 1_900))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> policy.topUp(card, wallet, Channel.KHQR, 400_100))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> policy.topUp(card, wallet, Channel.KHQR, 2_050))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(wallet.balance()).isZero();
    }

    @Test
    void topUpNeedsAnActiveCard() {
        Card card = Card.issue("CP-20270002-7", CardType.STUDENT, false,
                LocalDate.of(2027, 12, 31));
        Wallet wallet = new Wallet();
        assertThatThrownBy(() -> policy.topUp(card, wallet, Channel.CASH_DESK, 5_000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("card is ISSUED");
    }

    @Test
    void aCreditAboveTheMaximumBalanceIsRejectedByTheWallet() {
        Card card = activeCard();
        Wallet wallet = new Wallet(990_000);
        assertThatThrownBy(() -> policy.topUp(card, wallet, Channel.CASH_DESK, 10_100))
                .isInstanceOf(IllegalStateException.class);
        policy.topUp(card, wallet, Channel.CASH_DESK, 10_000);
        assertThat(wallet.balance()).isEqualTo(1_000_000);
    }
}
