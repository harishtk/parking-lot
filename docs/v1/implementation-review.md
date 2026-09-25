# Implementation and requirements review

Reviewed 25 September 2026. Evidence: every file under `docs/v1`, including decoded class-diagram labels, plus the existing Java sources, tests and Gradle configuration. This is a documentation handover and code inspection, not an implementation change. Existing staged and unstaged application changes were preserved.

## Overall assessment

The brainstorm describes a feasible small CLI, but its original forms are not consistent enough to implement literally. The consolidated specification supplies a coherent baseline and acceptance cases. The product owner confirmed **reservation records with priority deferred**. Other added choices remain visibly labeled recommendations rather than claimed approvals.

The current application is an incomplete scaffold. It does not compile as inspected, and several existing domain methods would violate the intended behavior even after compilation is restored. A working CLI, application services, policies, repositories and transaction boundary still need implementation.

## Requirements and design problems

| Finding | Implementation consequence | Consolidated resolution |
| --- | --- | --- |
| `findings.txt` ranks reserved spots, while `design_spec.md` explicitly says reservations have no spot | No spot ownership exists to evaluate that ranking. Arrival priority also has no queue in a sequential CLI. | D2: user confirmed records only and deferred priority. Remove reserved-spot ranking from v1 implementation. |
| In-memory storage paired with unspecified command lifecycle | Separate park/unpark process launches cannot share tickets. | D1: long-running interactive/file session with explicit state-loss semantics. |
| “One car or two bikes” for every spot conflicts with typed BIKE spots and `SpotType.BIKE(1)` | Team could implement incompatible capacity rules. | D4: CAR spots have 2 units, BIKE spots 1; explicit compatibility matrix in specification. |
| Empty-first ranking splits bikes across CAR spots | Cars may be rejected while partially used CAR spots retain enough aggregate free units. | D5: bike spot first, then pack bikes into partial CAR spots; deterministic tie-breaks. This intentionally changes the brainstorm ranking. |
| Hourly/prorated examples do not define rounding | Different implementations charge INR 23, 23.33 or 40 for 70 minutes. | D6: exact prorating, two decimals, HALF_UP, no minimum. |
| Expiration mentioned but no expiry field, TTL or transition | Expiry cannot be implemented or tested; stale reservation can block repeats forever. | D3: explicit expiry input, effective status at command time, fulfillment/cancellation/expiry states. |
| Cancellation removes record in use-case notes, retains CLOSED in code, and uses CANCELLED in diagram | Unknown vs cancelled cannot be handled consistently. | Retain terminal records during session; explicit lifecycle and repeat-cancel contract. |
| Separate history/lot saves in unpark flow, no rollback definition | Error between saves loses a fee or leaves a closed ticket still parked. | D10: one transaction for lot and history, with deep working state and atomic publish. |
| “Single source of truth” also says avoid state in Ticket/ParkingSpot | Taken literally, contradicts owned allocations and active tickets. | Aggregate owns all runtime state; child-owned allocation data is valid; indexes derive from it and historical snapshots are intentional. |
| Analytics have no denominator or interval meaning | Bike-sharing spots can produce misleading occupancy; revenue can double-count visits. | D8: unit occupancy, current parked vs completed visits, closed-fee sums and half-open UTC exit filters. |
| No lot setup, reset, ID scope or error contract | CLI and domain implementations drift independently. | D7 and CLI contract define inventory, unique IDs, no reset, result codes and outputs. |

## Existing code findings

Paths below are relative to the repository root; line numbers refer to the reviewed snapshot and may move as development continues. P1 means blocks a correct core implementation; P2 means correctness/testability work required before release. No P0 production outage is implied for this unfinished project.

| ID / severity | Evidence | Impact and required correction |
| --- | --- | --- |
| C01 / P1 | `app/src/main/java/com/parkinglot/app/domain/model/ParkingLot.java:57–59`, `findSpot` has an empty non-void body | Java compiler reports missing return statement. Implement consistent optional lookup or documented exception semantics. |
| C02 / P1 | `app/src/main/java/com/parkinglot/app/domain/model/ParkingSpot.java:38–43` | `canAccommodate` uses `totalCapacity <= remaining + request`. A full 2-unit spot accepts a 2-unit request; `isFull` returns true for an empty spot and false for a full one. Use compatibility plus `remaining >= required`, and `remaining == 0` for full. |
| C03 / P1 | `app/src/main/java/com/parkinglot/app/domain/model/Ticket.java:29–51` | Constructor never initializes ACTIVE; `close` never assigns CLOSED. Active checks fail and repeated closure can overwrite exit/fee. Initialize status and enforce the complete one-way transition with time/fee validation. |
| C04 / P1 | `app/src/main/java/com/parkinglot/app/domain/model/Reservation.java:20–28`; `ParkingLot.java:30–44` | New reservation status is null; aggregate creates duplicates without checking parked registration, active reservation or ID collision. Initialize ACTIVE and enforce specified lifecycle/uniqueness with expiry. |
| C05 / P1 | `app/src/main/java/com/parkinglot/app/domain/model/ParkingSpot.java:19–23,61–70`; `Floor.java` constructor/`spots`/`addSpot`; `ParkingLot.java` constructor | Stores caller-owned mutable maps; returns writable maps; public child mutators bypass aggregate validation. Caller can overfill spots, replace IDs or disconnect tickets. Copy inputs, restrict mutation and expose immutable snapshots. A read-only map containing mutable children is insufficient. |
| C06 / P2 | `app/src/main/java/com/parkinglot/app/domain/model/ParkingLot.java:39`; `infrastructure/idgenerator/ReservationIdGenerator.java`, `next` | Direct wall-clock calls bypass the injected clock. Timestamp-based IDs are not guaranteed unique and return raw strings. Supply command time to domain methods and use injected typed collision-resistant generators. |
| C07 / P2 | `app/src/main/java/com/parkinglot/app/domain/model/ParkingSpot.java`, constructor; `Allocation.java`, constructor | Arbitrary supplied capacity and preloaded allocation maps can contradict spot type/vehicle capacity and accept inconsistent records. Derive capacity from type and validate reconstructed aggregates and nonnull fields. |
| C08 / P2 | `app/src/main/java/com/parkinglot/app/domain/valueobject/Money.java`; `Ticket.java:62–66` | Money has no scale normalization; active ticket duration silently reports zero. Adopt the documented INR scale and separate active elapsed time from final closed duration. |
| C09 / P1 | `app/src/test/java/com/parkinglot/app/domain/valueobject/RegistrationNumberTest.java:8,28–32` | `ValueSources` is a suspect invalid/unused import that needs removal/build confirmation. `Named.of(name, payload)` is supplied labels as payloads, so the test receives strings such as `"Valid State Series"` instead of registrations. Correct argument order or use literal arguments. Spaced state/BH strings marked invalid conflict with the normalizer and documented accepted syntax. |
| C10 / P2 | `app/src/main/java/com/parkinglot/app/domain/valueobject/RegistrationNumber.java`, normalization; all ID record constructors | Uppercasing uses default locale; ID checks allow whitespace-only values. Use `Locale.ROOT` and reject blank IDs. Keep syntax validation distinct from real registry validation. |

### Missing implementation, rather than defective existing behavior

`App.main` still prints Hello World. No CLI commands/session loop, parking/unparking application services, selection/fee policies, in-memory repositories, atomic state transaction, analytics implementation or end-to-end lifecycle tests were found. Existing Guice wiring only provides a clock and reservation generator. `ParkingLot` does not yet implement allocation/release or enforce ticket/allocation/index consistency. These are the main development backlog, not evidence that any runtime parking feature currently works.

The tests currently cover a sample greeting and registration inputs. They do not provide evidence for allocation, closure, fees, expiry, recovery or CLI behavior. The delivery plan prioritizes these capabilities and their tests.

## Verification performed and limitations

- Installed runtime reports Java 21.0.8; `app/build.gradle` targets Java 21.
- Attempted `gradlew.bat :app:test --offline --no-daemon`. The wrapper failed before building because access to its lock file under the local user Gradle cache was denied. Therefore no Gradle test result is claimed; dependency resolution and the suspected `ValueSources` import were not validated by this run.
- Ran `javac` over the domain Java sources with output isolated in a temporary directory. Compilation failed at `ParkingLot.java:59` with **missing return statement**, independently confirming C01 without needing external dependencies.
- Other findings are source-inspection results with concrete counterexamples, not claimed passing/failing runtime tests. The review did not modify or fix application source.
- Documentation checks cover local link targets and consistency of the command/acceptance baseline. Full release verification remains the development team's work after implementation.

## Handover recommendation

Proceed in the order in [acceptance.md](acceptance.md). Adopt or revise D1 and D3–D10 explicitly as product/technical defaults; D2 is already confirmed. The most consequential remaining business choices are spot compatibility, bike packing, reservation expiry and monetary rounding. These have concrete proposed behavior and tests, so the team need not invent them during implementation. Do not describe the current code as a working v1 or ship it before compilation, invariants and atomic unpark behavior are verified.
