# Lab-01 Challenge: From a Unit Failure to the Day's Settlement

Pair: KHEANG Lymeng, NAT Chamnan

## 1. Three failures against the supplied `SubsidyPolicy`

```text
[ERROR] Failures:
[ERROR]   SettlementImpactTest.aMealBoughtAtTheFailingInstantIsChargedInFull:40
expected: 0L
 but was: 4000L
[ERROR]   SettlementImpactTest.theInstituteOwesNoSubsidyForMealsBoughtAtTheFailingInstant:64
expected: 0L
 but was: 1200000L
[ERROR]   SubsidyPolicyTest.lunchEndIsExclusive:63
expected: 0L
 but was: 4000L
[INFO]
[ERROR] Tests run: 27, Failures: 3, Errors: 0, Skipped: 0
```

With the fixed `SubsidyPolicy.java` restored, `mvn verify` runs all **27 tests** (`1` smoke + `7` top-up + `5` wallet + `12` subsidy + `2` settlement impact) with **0 failures**.

## 2. How the wrong subsidy propagates

* **To `charged` (`PurchaseService.purchase`):** `subsidyPolicy.subsidy(...)` returns `4,000` instead of `0` at `14:00:00`, so `long charged = total - subsidy` drops from `7,000` to `3,000` riel, undercharging the student's `Wallet` by `4,000` riel and writing `subsidy = 4,000` and `charged = 3,000` onto the `Receipt`.
* **To the `DailyLedger` (`DailyLedger.record`):** `PurchaseService` passes that `Receipt` to `ledger.record(receipt)`, storing an `Entry` with `charged = 3,000` (undercounting the student's daily spend toward `DailyLimitPolicy.STUDENT_LIMIT`) and `subsidised = true` (wrongly marking that student's once-a-day subsidy as used).
* **To `subsidiesOwed` (`Settlement.settle`):** `Transaction.purchase(receipt)` copies `receipt.subsidy()` (`4,000`) into `Transaction.subsidy()`, so `Settlement.settle(day)` sums `4,000` × `300` into `report.subsidiesOwed()` (`1,200,000` instead of `0`) and into stall `S1`'s `subsidyOwed`, shifting `1,200,000` riel of payment from student wallets (`purchasesCharged`: `2,100,000` → `900,000`) onto the institute while keeping stall `S1` gross `sales` at `2,100,000`.

## 3. Financial impact (300 scholarship students)

### Stated assumptions

* `300` distinct scholarship students (`CardType.STUDENT`, `scholarship = true`) each buy one `Menu.KUY_TEAV` (`7,000` riel, `Category.MEAL`) at stall `S1` at `2027-03-15T14:00:00` (Asia/Phnom_Penh).
* None of the `300` students has already used a subsidy earlier on the same teaching day (`subsidyUsedOn(...) == false`).
* Teaching calendar: `5` teaching days per week, `15` teaching weeks per semester (`75` teaching days, excluding holidays).
* Because `7,000 >= SUBSIDY_CAP (4,000)`, each affected meal receives `4,000` riel of unearned subsidy.

### Cost breakdown

* **Each student under-paid:** `4,000` riel per meal (`3,000` riel charged instead of `7,000` riel).
* **Institute over-payment per teaching day:** `300 × 4,000` = **`1,200,000` riel/day**.
* **Institute over-payment per teaching week (5 days):** `5 × 1,200,000` = **`6,000,000` riel/week**.
* **Institute over-payment per semester (15 weeks / 75 days):** `15 × 6,000,000` = **`90,000,000` riel/semester**.

## 4. Earliest catching activity from Task 3 (`lab01/vv-mapping.md`)

* **If the fault came from an ambiguous boundary in rule `SUB-1`:** **Activity `A1` / `A2` (`Inspection of numbered rules with a boundary checklist` / `Walkthrough of rule SUB-1 with the scholarship office`)** in **Phase `1 Requirements`** catches a misread `[11:00, 14:00)` boundary before any code is written, at a cost of **1–2 hours** per iteration (one analyst/developer plus one scholarship-office staff member, as estimated in `lab01/vv-mapping.md`).
* **For a coding fault against an already clear rule `SUB-1` (such as writing `t.isAfter(LUNCH_END)` instead of `!t.isBefore(LUNCH_END)`):** **Activity `A4` (`Unit tests of each policy class, derived from the numbered rules`)** in **Phase `3 Implementation`** is the earliest executing check that catches the `14:00:00` boundary defect before PR review (`A6`) or pilot release (`A7`–`A9`), at a cost of **1–3 hours** of one developer's time (per `lab01/vv-mapping.md`).
* **Cost comparison:** Catching and fixing the fault in **1–2 hours** at Phase `1 Requirements` (`A1`/`A2`) or **1–3 hours** at Phase `3 Implementation` (`A4`) prevents **`90,000,000` riel per semester** (`1,200,000` riel/day) of unearned institute subsidy payouts—several orders of magnitude cheaper than discovering the discrepancy in `Settlement` reports during Phase `6 Operation`.
