# Lab-01 Challenge: Financial Impact of the Subsidy Policy Defect

Pair: Student A (KHEANG-LYMENG), Student B

## 1. The Three Failures Against the Supplied SubsidyPolicy

Running `mvn verify` against the supplied `SubsidyPolicy` with `SettlementImpactTest` and `SubsidyPolicyTest` yields three failures:

```
[ERROR] Failures: 
[ERROR]   SettlementImpactTest.aMealBoughtAtTheFailingInstantIsChargedInFull:34 
expected: 0L
 but was: 4000L
[ERROR]   SettlementImpactTest.theInstituteOwesNoSubsidyForMealsBoughtAtTheFailingInstant:50 
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

- **Path to `charged`**: In `PurchaseService.purchase`, `charged` is calculated as `total - subsidy`, so an unentitled 4,000 riel subsidy reduces the price deducted from the student's wallet from 7,000 riel to 3,000 riel.
- **Path to `ledger`**: In `DailyLedger.record`, the receipt records the undercharged 3,000 riel against the student's daily spending limit and flags `subsidised = true`, improperly registering that the cardholder consumed their daily meal subsidy.
- **Path to `subsidiesOwed`**: In `Settlement.settle`, each purchase aggregates its 4,000 riel subsidy into the stall's payout and the overall `subsidiesOwed`, creating a financial liability that bills the institute 1,200,000 riel in unbudgeted subsidies for meals that rule SUB-1 did not subsidise.

---

## 3. Financial Impact and Over-Payment Computation

### Stated Assumptions
1. Exactly 300 scholarship students purchase one `KUY_TEAV` meal (price 7,000 riel, MEAL category) at stall S1 at the failing instant `14:00:00` on each teaching day.
2. The academic calendar comprises 5 teaching days per week and 15 teaching weeks per semester, for a total of 75 teaching days per semester.
3. Each purchase receives the maximum allowable subsidy of 4,000 riel instead of the correct 0 riel.

### Amount Under-Paid by Each Student
Because the card is charged 3,000 riel instead of 7,000 riel:
- **Per meal / per day**: Each student under-pays by **4,000 riel**.
- **Per week (5 days)**: Each student under-pays by $5 \times 4,000 = \mathbf{20,000\text{ riel}}$.
- **Per semester (75 days)**: Each student under-pays by $75 \times 4,000 = \mathbf{300,000\text{ riel}}$.

### Institute Over-Payment (300 Students)
- **Per teaching day**:
  $$300 \times 4,000\text{ riel} = \mathbf{1,200,000\text{ riel}}\quad(\approx \$300\text{ USD})$$
- **Per teaching week (5 days)**:
  $$5 \times 1,200,000\text{ riel} = \mathbf{6,000,000\text{ riel}}\quad(\approx \$1,500\text{ USD})$$
- **Per semester (15 weeks = 75 days)**:
  $$75 \times 1,200,000\text{ riel} = \mathbf{90,000,000\text{ riel}}\quad(\approx \$22,500\text{ USD})$$

---

## 4. Earliest Catching Activity and Cost Comparison

- **Earliest Activity**: **A1 (Inspection of the numbered rules with a boundary checklist)**.
- **Process Phase**: **Phase 1 Requirements**.
- **Cost per Iteration**: Approximately **1 to 2 hours of two software engineers** (2 to 4 person-hours total). At an industry labor rate of \$20/hour, this inspection costs approximately **\$40 to \$80** (160,000 to 320,000 riel, average ~240,000 riel) once per two-week iteration.
- **Comparison with Semester Over-Payment**:
  The potential semester financial loss to the institute of **90,000,000 riel** dwarfs the **~240,000 riel** cost of conducting the Phase 1 boundary checklist inspection by a factor of **375x**. Spending 2 hours upfront to clarify that `LUNCH_END = 14:00` is strictly exclusive prevents tens of thousands of dollars in wrongful cash disbursements at the canteen counters.
