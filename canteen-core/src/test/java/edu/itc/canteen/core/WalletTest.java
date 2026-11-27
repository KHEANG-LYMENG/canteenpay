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
        // 5,500 charged from exactly 5,500 → returns true, balance 0
        Wallet wallet = new Wallet(5_500);
        assertThat(wallet.charge(5_500)).isTrue();
        assertThat(wallet.balance()).isZero();
    }

    @Test
    void invalidAmountsAreRejectedWithoutChangingTheBalance() {
        // on a wallet of 1,000:
        //   charge(-100)       → IllegalArgumentException (negative amount)
        //   credit(0)          → IllegalArgumentException (must be positive)
        //   credit(1,000,000)  → IllegalStateException (would exceed MAX_BALANCE)
        // balance stays 1,000 throughout
        Wallet wallet = new Wallet(1_000);
        assertThatThrownBy(() -> wallet.charge(-100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("amount must not be negative");
        assertThatThrownBy(() -> wallet.credit(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("amount must be positive");
        assertThatThrownBy(() -> wallet.credit(1_000_000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("balance would exceed 1000000");
        assertThat(wallet.balance()).isEqualTo(1_000);
    }

    @Test
    void transferMovesTheWholeBalanceToTheReplacementCard() {
        // 12,300 moved from old wallet to new wallet → old 0, new 12,300
        Wallet oldWallet = new Wallet(12_300);
        Wallet newWallet = new Wallet();
        oldWallet.transferTo(newWallet);
        assertThat(oldWallet.balance()).isZero();
        assertThat(newWallet.balance()).isEqualTo(12_300);
    }
}
