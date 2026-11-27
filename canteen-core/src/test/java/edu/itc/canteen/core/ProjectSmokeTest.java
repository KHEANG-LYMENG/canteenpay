package edu.itc.canteen.core;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectSmokeTest {
    @Test
    void theStandardCardNumberHasAValidCheckDigit() {
        assertThat(CardNumber.of(20270001).toString())
                .isEqualTo("CP-20270001-9");
        assertThat(CardNumber.isValid("CP-20270001-0")).isFalse();
    }
}
