# Lab-01: Software quality in a nutshell

Pair: Student A (KHEANG-LYMENG), Student B

## Files changed in this lab
| File | New or changed | What and why |
|------|----------------|--------------|
| canteen-core/pom.xml | new | supplied, builds canteen-core, pins JUnit 6, AssertJ, Surefire, JaCoCo |
| canteen-core/src/main/java/edu/itc/canteen/core/*.java | new | 21 supplied domain classes from starter pack |
| canteen-core/src/main/java/edu/itc/canteen/core/SubsidyPolicy.java | changed | 1 seeded fault fixed: replaced `t.isAfter(LUNCH_END)` with `!t.isBefore(LUNCH_END)` for exclusive lunch end |
| canteen-core/src/test/java/edu/itc/canteen/core/ProjectSmokeTest.java | new | Task 1: sanity test verifying CardNumber check digit, formatting, and validity |
| canteen-core/src/test/java/edu/itc/canteen/core/TopUpPolicyTest.java | new | Task 4: 7 unit tests verifying KHQR, parent app, cash desk fees and top-up rules |
| canteen-core/src/test/java/edu/itc/canteen/core/WalletTest.java | new | Task 4: 5 unit tests verifying balance, charge, limit invariants, and transfer |
| canteen-core/src/test/java/edu/itc/canteen/core/SubsidyPolicyTest.java | new | Task 5: 12 unit tests verifying each clause and boundary of rule SUB-1 |
| lab01/*.md | new | tools.md, quality-requirements.md, vv-mapping.md, analysis.md, README.md |

## Reused from earlier labs
None: Lab-01 is the first lab. Supplied starter used as is: pom.xml and 20 classes.

## Change log
| Date | Change | Tests after the change |
|------|--------|------------------------|
| 2026-11-20 | Lab-01: project set up, first tests, 1 seeded fault fixed | mvn verify: 25 tests, 0 failures |

## Who did what
| Task | Student | Commit(s) |
|------|---------|-----------|
| Setup, Task 1, Task 3, Task 5 (tests, fix) | A | 59cbb02, 6c329e3, 884275b |
| Task 2, Task 4, Task 5 (analysis, README) | B | f396e74, 1189c59, 19a0959, and Task 5 Student B commit |
| Challenge | A and B | Optional |
