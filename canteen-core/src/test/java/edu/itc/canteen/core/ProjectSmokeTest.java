package edu.itc.canteen.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Lab-01 Task 1: proves that the project compiles and that JUnit and AssertJ run. */
class ProjectSmokeTest {

    @Test
    void theStandardCardNumberHasAValidCheckDigit() {
        assertThat(CardNumber.of(20270001)).isEqualTo("CP-20270001-9");
        assertThat(CardNumber.isValid("CP-20270001-9")).isTrue();
        assertThat(CardNumber.isValid("CP-20270001-0")).isFalse();
    }
}
