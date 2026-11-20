# Lab-01: failure report and analysis

Pair: Student A, Student B

## FR-01: Subsidy granted at 14:00:00 (LUNCH_END) instead of returning 0 riel

Reported by: Student A, 2026-11-20 · Component: canteen-core 1.0.0, SubsidyPolicy
Environment: JDK 27.0.1 (build 27.0.1.0.3+1-2), Maven 3.9.9, macOS (Darwin 27.2.0 arm64), commit 19a0959

### Steps to reproduce
1. Card CP-20270001-9 (active scholarship student), basket [KUY_TEAV (7,000 riel, MEAL)], at `2027-03-15T14:00:00` (Phnom Penh local time), alreadyUsedToday = false.
2. Call `SubsidyPolicy.subsidy(...)` with these arguments.
   Automated: `SubsidyPolicyTest.lunchEndIsExclusive` (`mvn verify`)

### Expected
0 riel, from rule SUB-1: "LUNCH_START = 11:00 inclusive, LUNCH_END = 14:00 exclusive ... 14:00:00 -> 0"

### Actual
4000 riel (Surefire: `expected: 0L but was: 4000L`)

### Severity and impact
Major: The institute (ITC) subsidises transactions that occur at the exact closing boundary of lunch (14:00:00). Cardholders receive an unentitled 4,000 riel meal discount, and the institute owes this unbudgeted money to the stall in the daily settlement. While occurring within a narrow 1-second time window, at peak canteen checkout times on teaching days this could leak 4,000 to 20,000 riel per day in wrongful disbursements.

### Reproducibility
always: 3 of 3 runs. Probed inputs around the failing one:
| Input | Expected | Actual | Verdict |
|---|---|---|---|
| `2027-03-15T13:59:59` | 4,000 riel | 4,000 riel | Pass |
| `2027-03-15T14:00:00` | 0 riel | 4,000 riel | Fail |
| `2027-03-15T14:00:01` | 0 riel | 0 riel | Pass |

Suspected fault (unverified, written before reading the code): The boundary comparison uses `t.isAfter(LUNCH_END)` instead of `!t.isBefore(LUNCH_END)` or `>= LUNCH_END`, treating 14:00:00 as strictly within the allowed range rather than exclusive.

## The fix
Smallest change that makes SUB-1 hold (one-line diff, written after the report):
```diff
-        if (t.isBefore(LUNCH_START) || t.isAfter(LUNCH_END)) return 0;
+        if (t.isBefore(LUNCH_START) || !t.isBefore(LUNCH_END)) return 0;
```
After the fix: `mvn verify` → Tests run: 25, Failures: 0.

## Analysis (at most one page)
What tests derived from the verified examples and the boundaries of SUB-1 revealed:
- 12 unit tests were written for `SubsidyPolicyTest` covering every clause and boundary of rule SUB-1.
- Exactly 1 test failed: `lunchEndIsExclusive` at input `2027-03-15T14:00:00`.
- Probing 1 second before (`13:59:59`) passed with 4,000 riel, and 1 second after (`14:00:01`) passed with 0 riel.
- The width of the failing interval is exactly 1 second at integer-second resolution (specifically the instant $[14:00:00.000, 14:00:01.000)$).

What they missed or could have missed:
- Inputs that pass on both versions include all standard lunch transactions between 11:00:00 and 13:59:59, because both implementations evaluate `t.isBefore(11:00)` as false and both treat interior lunch times as valid.
- Sub-second timestamp resolution inputs (e.g. `14:00:00.500`) pass on the supplied version because `LocalTime.of(14,0,0,500_000_000).isAfter(14:00)` evaluates to true.
- One fault of the same kind that the twelve tests would not catch: if the policy incorrectly selected the *cheapest* or *last* meal in a multi-meal basket rather than the *first* meal item (e.g. basket containing `[NUM_ANSOM (snack), BOBOR (meal, 3,000), KUY_TEAV (meal, 7,000)]`). Because none of our twelve test cases contain multiple distinct MEAL items in a single basket, code implementing `item.price()` over all meals without breaking on the first MEAL item would pass all 12 tests.
- Test to add: `subsidy(card, List.of(bobor, kuyTeav), at("2027-03-15T12:30:00"), false)` asserting that the returned subsidy is 3,000 riel (first meal price) rather than 4,000 riel (cap of second meal).

What this says about testing as a verification activity for SUB-1:
Boundary value testing is remarkably powerful at exposing off-by-one comparison faults that easily escape manual review and casual functional checks. However, because tests evaluate only isolated discrete sample points, testing alone cannot prove the absence of faults across multi-dimensional input combinations like multi-item baskets without systematic domain coverage.
