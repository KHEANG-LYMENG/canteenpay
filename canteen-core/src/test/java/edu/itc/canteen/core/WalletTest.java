package edu.itc.canteen.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Lab-01 Task 4: the wallet, correctness and robustness. */
class WalletTest {

    @Test
    void chargeTakesTheAmountWhenTheBalanceCoversIt() {
        Wallet wallet = new Wallet(50_000);
        assertThat(wallet.charge(6_000)).isTrue();
        assertThat(wallet.balance()).isEqualTo(44_000);
    }

    @Test
    void chargeRefusesAndChangesNothingWhenTheBalanceIsShort() {
        Wallet wallet = new Wallet(5_000);
        assertThat(wallet.charge(5_500)).isFalse();
        assertThat(wallet.balance()).isEqualTo(5_000);
    }

    @Test
    void chargeOfTheExactBalanceLeavesZero() {
        Wallet wallet = new Wallet(5_500);
        assertThat(wallet.charge(5_500)).isTrue();
        assertThat(wallet.balance()).isZero();
    }

    @Test
    void invalidAmountsAreRejectedWithoutChangingTheBalance() {
        Wallet wallet = new Wallet(1_000);
        assertThatThrownBy(() -> wallet.charge(-100))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> wallet.credit(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> wallet.credit(1_000_000))
                .isInstanceOf(IllegalStateException.class);
        assertThat(wallet.balance()).isEqualTo(1_000);
    }

    @Test
    void transferMovesTheWholeBalanceToTheReplacementCard() {
        Wallet oldWallet = new Wallet(12_300);
        Wallet newWallet = new Wallet();
        oldWallet.transferTo(newWallet);
        assertThat(oldWallet.balance()).isZero();
        assertThat(newWallet.balance()).isEqualTo(12_300);
    }
}
