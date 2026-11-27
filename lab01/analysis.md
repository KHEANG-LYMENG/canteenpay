# Lab-01 Analysis: SubsidyPolicy seeded failure

Pair: KHEANG LYMENG, NAT Chamnan

## Failure report

Reported by: KHEANG Lymeng, NAT Chamnan, 2026-11-27 · Component: `canteen-core` 1.0.0, `SubsidyPolicy`
Environment: JDK 27.0.1.0.3, Maven 3.9.9, macOS 27.2.1, commit `f0920d8` (Student B: JDK 27.0.1.0.3, Maven 3.10.0, Windows 11 build 26300)

### Steps to reproduce

1. Open `canteen-core` with the supplied `SubsidyPolicy.java`.
2. Run `mvn verify` with the 12 tests in `SubsidyPolicyTest` written from rule SUB-1.
3. Observe the single red test:

```text
[ERROR] Tests run: 12, Failures: 1, Errors: 0, Skipped: 0 -- in edu.itc.canteen.core.SubsidyPolicyTest
[ERROR] SubsidyPolicyTest.lunchEndIsExclusive:63
expected: 0L
 but was: 4000L
```

### Expected (source: rule SUB-1)

> "Subsidy is granted only during the lunch window **[11:00, 14:00)** (`11:00` inclusive to `14:00` exclusive); a purchase at exactly `14:00:00` is outside lunch hours."

Expected return value at `2027-03-15T14:00:00`: **0**.

### Actual (as printed by Surefire)

```text
expected: 0L
 but was: 4000L
```

### Faulty interval (boundary probing)

| Input (local time) | Expected  | Actual    | Result   |
| ------------------ | --------- | --------- | -------- |
| `13:59:59`         | `4,000`   | `4,000`   | Pass     |
| `14:00:00`         | `0`       | `4,000`   | **Fail** |
| `14:00:01`         | `0`       | `0`       | Pass     |

Width of failing interval: **1 second** (`[14:00:00, 14:00:01)`) — only `14:00:00` itself fails.

### Severity

**High.** Every scholarship student who buys a meal at `14:00:00` on a teaching day receives up to `4,000` riel of unearned subsidy from the institute and under-pays at the stall.

### Reproducibility

100% deterministic across 3 consecutive `mvn verify` runs (3 of 3 runs failed with `expected: 0L but was: 4000L`).

### Suspected fault

> **Suspicion (unverified prior to inspection):** `SubsidyPolicy` likely checks `t.isAfter(LUNCH_END)` instead of `!t.isBefore(LUNCH_END)` when rejecting times at or after `14:00`, accidentally making `14:00:00` inclusive.

---

## Fix

One-line diff in `canteen-core/src/main/java/edu/itc/canteen/core/SubsidyPolicy.java` (line 33):

```diff
-        if (t.isBefore(LUNCH_START) || t.isAfter(LUNCH_END)) return 0;
+        if (t.isBefore(LUNCH_START) || !t.isBefore(LUNCH_END)) return 0;
```

After restoring the fix and running `mvn verify` (Task 5 suite): `Tests run: 25, Failures: 0, Errors: 0, Skipped: 0` (`BUILD SUCCESS`). `lunchEndIsExclusive` remains as a permanent regression test.

---

## Analysis (what the tests revealed and what they missed)

### Measured numbers

- **Tests written in `SubsidyPolicyTest`:** `12` (3 starter tests + 9 clause tests from rule SUB-1)
- **Tests failed against supplied code:** `1` (`lunchEndIsExclusive`)
- **Boundary inputs probed:** `3` (`13:59:59`, `14:00:00`, `14:00:01`)
- **Failing interval width:** `1 second`

### What the technique revealed

Clause-by-clause boundary testing against the written rule caught an off-by-one boundary error that random or happy-path inputs (such as `12:30:00` or `14:00:01`) would almost certainly miss, because the fault only infects the single boundary second `14:00:00`. Probing 1 second to either side narrowed the defect to the upper-bound comparison immediately.

### What the technique would have missed

The 12 tests focus heavily on time boundaries and single-rule clauses, so they only sample a small fraction of input combinations and non-time boundaries. A fault of the **same kind** (an off-by-one boundary error) that these 12 tests would **not** catch is a strict cap comparison (`item.price() < SUBSIDY_CAP` or `Math.min(...) - 100` at the cap) when a meal costs **exactly `4,000` riel**, because our tests only used meals priced at `3,000`, `5,500`, and `7,000` riel.

**Test to add:**

```java
@Test
void aMealPricedExactlyAtTheCapIsSubsidisedAtTheCap() {
    Item exactCap = new Item("M99", "Cap meal", 4_000, Category.MEAL);
    long s = policy.subsidy(scholarshipStudent(), List.of(exactCap),
            at("2027-03-15T12:00:00"), false);
    assertThat(s).isEqualTo(4_000);
}
```
