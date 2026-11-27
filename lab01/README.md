# Lab-01: Software quality in a nutshell

Pair: KHEANG LYMENG (KHEANG-LYMENG), NAT Chamnan (NatChamnan)

## Files changed in this lab

| File                                                                      | New or changed          | What and why                                                                                                                                                                        |
| ------------------------------------------------------------------------- | ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| canteen-core/pom.xml                                                      | new                     | supplied, unchanged (`f0920d8`)                                                                                                                                                   |
| canteen-core/src/main/java/edu/itc/canteen/core/*.java                    | new                     | 21 supplied classes (`f0920d8`)                                                                                                                                                   |
| canteen-core/src/main/java/edu/itc/canteen/core/SubsidyPolicy.java        | unchanged (`f0920d8`) | supplied version in `f0920d8` already passes all 12 `SubsidyPolicyTest` cases; see `analysis.md`                                                                              |
| canteen-core/src/test/java/edu/itc/canteen/core/ProjectSmokeTest.java     | new                     | 1 supplied smoke test for `CardNumber` (`f0920d8`)                                                                                                                              |
| canteen-core/src/test/java/edu/itc/canteen/core/TopUpPolicyTest.java      | new                     | 7 unit tests for `TopUpPolicy` fee and top-up rules (`440a2da`)                                                                                                                 |
| canteen-core/src/test/java/edu/itc/canteen/core/WalletTest.java           | new                     | 5 unit tests for `Wallet` charge, validation, and transfer (`440a2da`)                                                                                                          |
| canteen-core/src/test/java/edu/itc/canteen/core/SubsidyPolicyTest.java    | new                     | 12 unit tests for `SubsidyPolicy`, one per clause of rule `SUB-1` (`37d20af`)                                                                                                 |
| canteen-core/src/test/java/edu/itc/canteen/core/SettlementImpactTest.java | new                     | 2 challenge tests tracing the `14:00:00` subsidy fault through `PurchaseService` and `Settlement` (uncommitted)                                                               |
| lab01/*.md                                                                | new                     | `tools.md` (`f0920d8`), `quality-requirements.md` (`6b4d6d4`, `2636948`), `vv-mapping.md` (`2636948`), `analysis.md` (`37d20af`), `impact.md` & `README.md`  |

## Reused from earlier labs

None: Lab-01 is the first lab. Supplied starter used as is: `pom.xml` and all 21 classes in `f0920d8`.

## Change log

| Date       | Change                                                                                                                                                                                                                                                   | Tests after the change               |
| ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------ |
| 2026-11-27 | Lab-01: project set up (`f0920d8`), quality requirements (`6b4d6d4`), V&V mapping (`2636948`), `TopUpPolicyTest` & `WalletTest` (`440a2da`), `SubsidyPolicyTest` & `analysis.md` (`37d20af`), `SettlementImpactTest` & `impact.md` | `mvn verify`: 27 tests, 0 failures |

## Who did what

| Task                                                                                          | Student       | Commit(s)                                          |
| --------------------------------------------------------------------------------------------- | ------------- | -------------------------------------------------- |
| Setup, Task 1, Task 3, Task 5 (`SubsidyPolicyTest`)                                         | KHEANG Lymeng | `ba9ce13`, `f0920d8`, `2636948`, `37d20af` |
| Task 2, Task 4 (`TopUpPolicyTest`, `WalletTest`), Task 5 (`analysis.md`, `README.md`) | NAT Chamnan   | `6b4d6d4`, `440a2da`, `37d20af`              |
| Challenge                                                                                     | A and B       | define requirement logic                           |
