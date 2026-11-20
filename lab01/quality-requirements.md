# Lab-01 Task 2: quality requirements of CanteenPay

Pair: Student A, Student B

Columns: internal or external (or process) · ISO/IEC 25010:2023 characteristic ·
dependability property (correctness, reliability, robustness, safety) or process
property (timeliness, visibility, improvability) · how it could be measured.

| Id  | Statement | Internal / external | ISO/IEC 25010 characteristic | Dependability or process property | How it could be measured |
|-----|-----------|---------------------|------------------------------|-----------------------------------|--------------------------|
| Q1  | `subsidy(...)` returns exactly the amount of rule SUB-1 for every purchase | external | functional suitability (functional correctness) | correctness | every clause of SUB-1 has a passing test; no open failure report on SUB-1 |
| Q2  | 999 of 1,000 kiosk purchases on a teaching day produce a correct receipt within 2 s | external | performance efficiency (time behaviour) | reliability | percentage of kiosk purchase transactions completing under 2 s recorded in kiosk access logs on teaching days |
| Q3  | When the network is down, a stall terminal keeps accepting purchases and sends them later | external | reliability (fault tolerance) | robustness | automated tests with simulated network disconnection verifying offline purchases succeed and sync cleanly upon reconnection |
| Q4  | A card is never charged more than its balance, and never twice for one meal, whatever the kiosk and the app do at the same moment | external | security (integrity) | safety | multi-threaded stress test results asserting wallet balances and receipt counts remain strictly invariant under concurrent operations |
| Q5  | A new holiday can be added to the academic calendar without changing `SubsidyPolicy` | internal | maintainability (modifiability) | none: modifiability, ease of developer code change | code review and git diff confirming zero lines changed in SubsidyPolicy when adding a calendar holiday |
| Q6  | `SubsidyPolicy` takes the time as a parameter instead of reading the system clock | internal | maintainability (testability) | none: testability, facilitates controllable automated unit tests | unit test suite covering edge dates and times without mocking or manipulating the system clock |
| Q7  | The daily settlement balances to the riel: credited top-ups - purchases + refunds = change of all balances | external | functional suitability (functional correctness) | correctness | daily reconciliation report verifying zero discrepancy between expected balance change and actual closing balances in production ledger |
| Q8  | The team can say at every build which rules have passing tests and how many failure reports are open | process | none: process | visibility | share of rules with at least one passing test, and the count of open reports, published per build |
| Q9  | A failure found at the pilot kiosk is traced to its root cause and a test is added so that it cannot come back | process | none: process | improvability | proportion of resolved pilot defect tickets with an identified root cause and a new regression test in Git commit history |
| Q10 | The unit tests of a rule are written in the same iteration as the rule, not before the pilot release | process | none: process | timeliness | git commit history comparing commit timestamps between rule implementations and their corresponding unit test files |
| Q11 | The third wrong PIN in a row blocks the card | external | security (authenticity) | robustness | test suite results verifying that card state transitions to BLOCKED after exactly three consecutive failed PIN attempts |
| Q12 | A first-year student tops up with KHQR at the kiosk without help in under one minute | external | interaction capability (learnability) | none: usability, user experience not dependability | usability testing survey and timed kiosk session recordings measuring first-year student completion time without assistance |

## The two hardest rows
Statements Q2 and Q4 are the hardest to classify because Q2 blends performance efficiency with operational reliability, while Q4 simultaneously addresses functional correctness, concurrency robustness, and financial safety. This classification difficulty matters for the verifier because the assigned category directly determines the testing strategy, tooling, and test environment required to check each statement. For instance, misclassifying Q4 as a standard functional requirement could lead a tester to rely solely on single-threaded unit tests, completely overlooking severe race conditions that require concurrent multi-threaded stress testing and ledger integrity audits.
