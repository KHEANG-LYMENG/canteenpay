# Lab-01 Challenge: Financial Impact of the Subsidy Policy Defect

Pair: Student A (KHEANG-LYMENG), Student B

## 1. Observed Test Failures against Supplied SubsidyPolicy

Running `mvn verify` against the supplied `SubsidyPolicy` with `SettlementImpactTest` and `SubsidyPolicyTest` produces exactly 3 failures:

```
[ERROR] Failures: 
[ERROR]   SettlementImpactTest.aMealBoughtAtTheFailingInstantIsChargedInFull:34 
expected: 0L
 but was: 4000L
[ERROR]   SettlementImpactTest.theInstituteOwesNoSubsidyForMealsBoughtAtTheFailingInstant:49 
expected: 0L
 but was: 1200000L
[ERROR]   SubsidyPolicyTest.lunchEndIsExclusive:63 
expected: 0L
 but was: 4000L
[INFO] 
[ERROR] Tests run: 27, Failures: 3, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

---

## 2. Propagation Path of the Defect

The failure originates in `SubsidyPolicy.java` at line 32:
```java
if (t.isBefore(LUNCH_START) || t.isAfter(LUNCH_END)) return 0;
```
Because `LocalTime.of(14, 0).isAfter(LocalTime.of(14, 0))` evaluates to `false`, the policy treats `14:00:00` as within lunch hours, returning `4,000` riel instead of `0`. This error cascades through the canteen-core financial system along three critical paths:

### A. Through `charged`
In `PurchaseService.purchase(...)`:
$$\text{charged} = \text{total} - \text{subsidy}$$
For a 7,000 riel meal (`KUY_TEAV`), the charged amount is calculated as $7,000 - 4,000 = 3,000$ riel instead of the correct $7,000 - 0 = 7,000$ riel. The student's wallet is charged only 3,000 riel, granting the student an unentitled discount.

### B. Through `DailyLedger`
The resulting receipt is recorded in `DailyLedger`:
- The card's daily spent balance records $3,000$ riel instead of $7,000$ riel, preserving $4,000$ riel of the cardholder's daily spending limit (`STUDENT_LIMIT = 30,000`).
- The ledger marks `subsidised = true` for that day, erroneously recording that the student legitimately consumed their one daily subsidy.

### C. Through `subsidiesOwed` in Daily Settlement
In `Settlement.settle(...)`, each purchase transaction is aggregated into the daily settlement report:
- For each stall, gross sales is computed as $\text{sales} = \text{charged} + \text{subsidy} = 3,000 + 4,000 = 7,000$ riel.
- The stall receives its full payout of 7,000 riel.
- However, the institute owes the stall the subsidy portion: $\text{subsidyOwed} += 4,000$.
- In the institute-wide report, `subsidiesOwed` accumulates 4,000 riel per transaction. The institute transfers this money to the stalls out of its institutional subsidy budget, bearing the entire financial loss of the undercharge. Across 300 meals, the institute pays **1,200,000 riel** in wrongful disbursements.

---

## 3. Financial Over-Payment Estimation

### Stated Assumptions
1. **Eligible Population**: 1,200 scholarship students on campus.
2. **Late-Lunch Transactions**: During busy teaching days, students rushing to beat the lunch cutoff generate between 50 and 300 transactions right at `14:00:00` (or within terminal clock synchronization tolerances).
3. **Academic Calendar**: 5 teaching days per week, 15 teaching weeks per semester (total 75 teaching days per semester).
4. **Subsidy Cap**: Each affected transaction consumes the full subsidy cap of 4,000 riel.

### Projections

| Period | Conservative Scenario (50 students/day) | Test Scenario (300 students/day) |
|:---|:---|:---|
| **Per Day** | $50 \times 4,000 = \mathbf{200,000\text{ riel}}$ (~$50 USD) | $300 \times 4,000 = \mathbf{1,200,000\text{ riel}}$ (~$300 USD) |
| **Per Week (5 days)** | $200,000 \times 5 = \mathbf{1,000,000\text{ riel}}$ (~$250 USD) | $1,200,000 \times 5 = \mathbf{6,000,000\text{ riel}}$ (~$1,500 USD) |
| **Per Semester (75 days)** | $200,000 \times 75 = \mathbf{15,000,000\text{ riel}}$ (~$3,750 USD) | $1,200,000 \times 75 = \mathbf{90,000,000\text{ riel}}$ (~$22,500 USD) |

---

## 4. Comparison with the Cost of Earliest Catching Activity

From Task 3 (V&V mapping):
- **Earliest Catching Activity**: **A1 (Inspection of the numbered rules with a boundary checklist)** in **Phase 1 Requirements**.
- **Effort & Cost**: Requires **1 to 2 hours of two software engineers** (2 to 4 person-hours total). At an engineering labor rate of \$20/hour, the inspection costs approximately **\$40 to \$80** (160,000 to 320,000 riel) once during the iteration.
- **Cost Comparison**:
  - Preventing the defect in Phase 1 costs **~240,000 riel**.
  - Permitting the defect into operation costs **15,000,000 to 90,000,000 riel per semester**.
  - **ROI**: Catching the defect through early boundary inspection yields a financial return of **60x to 375x** the cost of the verification activity itself.
