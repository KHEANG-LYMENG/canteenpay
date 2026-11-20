# Lab-01 Task 3: verification and validation in the ITC process

Pair: Student A, Student B

## Activities (one row each)

| Id  | Activity | Phase (earliest) | Verification or validation | Artefact checked | Checked against |
|-----|----------|------------------|----------------------------|------------------|-----------------|
| A1  | Inspection of the numbered rules with a boundary checklist (every "within", "before", "until": inclusive or exclusive?) | 1 Requirements | validation of each rule against the workshop notes; verification of the rules against each other (consistency) | rules SUB-1, REF-1, TOP-1, PUR-1, SET-1 | workshop notes; the other rules |
| A2  | Walkthrough of rule SUB-1 with the scholarship office | 1 Requirements | validation against the needs and intent of the scholarship office | rule SUB-1 | the actual policy and needs of the scholarship office |
| A3  | Static analysis of canteen-core for null dereferences and data races | 3 Implementation | verification against Java language rules and concurrency/null-safety specifications | canteen-core source code on feature branches | Java language specification and concurrency/null safety rules |
| A4  | Unit tests of each policy class, derived from the numbered rules | 3 Implementation | verification against the numbered rules (SUB-1, TOP-1, TOP-2, REF-1, PUR-1) | policy classes (SubsidyPolicy, TopUpPolicy, RefundPolicy, DailyLimitPolicy) | numbered rules (SUB-1, TOP-1, TOP-2, REF-1, PUR-1) |
| A5  | Model checking of the wallet protocol (kiosk and app on one wallet) | 2 Design | verification against wallet balance integrity and concurrency safety invariants | formal state machine / sequence model of the wallet protocol | wallet balance integrity invariants and non-overcharge properties (MAX_BALANCE, no overdraft) |
| A6  | Review of every pull request against the rules it implements | 4 Review | verification against the implemented numbered rules and coding standards | pull request branch / code changes | the numbered rules being implemented (e.g. SUB-1, TOP-1) and coding guidelines |
| A7  | Integration test of kiosk, API and core on the pilot build | 5 Pilot release | verification against terminal-API protocol specifications and purchase workflows | integrated system build (pilot kiosk hardware/software, backend API, and canteen-core) | terminal-API protocol specifications and end-to-end purchase workflows |
| A8  | Acceptance test with the canteen manager on the pilot kiosk | 5 Pilot release | validation against what the canteen manager needs at the counter | the pilot build on kiosk 1, with the real menu | what the canteen manager needs at the counter |
| A9  | Regression run of all tests before every pilot release | 5 Pilot release | verification against the existing automated regression test suite | the candidate release build / entire codebase | the existing suite of unit, integration, and policy regression tests |
| A10 | Triage and root cause analysis of each failure report | 6 Operation | validation of user problem reports against stakeholder operational needs; verification of observed failures against numbered rules and system logs | failure reports and system logs from canteen operation | numbered business rules and expected user behavior |

## Diagram

```mermaid
flowchart LR
  P1["1 Requirements"] --> P2["2 Design"] --> P3["3 Implementation"] --> P4["4 Review"] --> P5["5 Pilot release"] --> P6["6 Operation"]
  P1 -.-> A1["A1 inspection of the rules (validation, verification)"]
  P1 -.-> A2["A2 walkthrough with scholarship office (validation)"]
  P2 -.-> A5["A5 model checking of wallet protocol (verification)"]
  P3 -.-> A3["A3 static analysis of canteen-core (verification)"]
  P3 -.-> A4["A4 unit tests of policy classes (verification)"]
  P4 -.-> A6["A6 pull request review (verification)"]
  P5 -.-> A7["A7 integration test of pilot build (verification)"]
  P5 -.-> A8["A8 acceptance on the pilot kiosk (validation)"]
  P5 -.-> A9["A9 regression run before release (verification)"]
  P6 -.-> A10["A10 triage and root cause analysis (verification, validation)"]
```

## Earliest catch of a misread boundary in SUB-1
Activity **A1 (Inspection of the numbered rules with a boundary checklist)** in **Phase 1 Requirements** is the earliest activity to catch an ambiguous or misread boundary in SUB-1 before any code or tests are created. It costs approximately **1 to 2 hours of two software engineers** (a total of 2 to 4 person-hours per iteration) to inspect the drafted rules against the checklist. If the rule specification itself was clear but a developer misread the boundary while coding (e.g. using `t.isAfter(14:00)` instead of exclusive comparison), Activity **A4 (Unit tests of each policy class)** in **Phase 3 Implementation** is the earliest activity to detect the failure, costing approximately **1 to 2 hours of one developer** to write and run boundary test cases (11:00:00, 13:59:59, 14:00:00).
