# V1 acceptance and delivery plan

This plan implements the [consolidated specification](handover.md), including its explicitly labeled recommended defaults. D2 (reservation records, deferred priority) is user-confirmed. Tests should use fixed clocks and deterministic typed ID generators; avoid real sleeps. Each case begins with a fresh fixture unless stated otherwise.

## Acceptance cases

| ID | Scenario | Required result |
| --- | --- | --- |
| A01 | Start session, request status before creation, then create two floors | First command returns `LOT_NOT_INITIALIZED`; creation succeeds and later commands see the same lot. |
| A02 | Create with `--floor 1:1:1 --floor 2:2:0` | 2 floors, 4 spots, 7 units; IDs F1-C1, F1-B1, F2-C1, F2-C2, in numeric configured order. |
| A03 | Duplicate floor, zero/negative floor number, negative counts, zero-spot floor or overflow | `INVALID_INPUT`; no partially initialized inventory. A second valid create after success returns `LOT_ALREADY_EXISTS` and preserves inventory. |
| A04 | CAR arrives at a lot with a free BIKE spot and a free CAR spot | Allocate the CAR spot; ticket ACTIVE, one allocation and one vehicle-index entry agree. |
| A05 | Two bikes arrive with only one empty CAR spot | Both use that spot; first arrival makes it PARTIAL, second FULL. A third bike fails without creating a ticket or allocation. |
| A06 | Only two free BIKE spots remain; a car arrives | `NO_SUITABLE_SPOT` despite two total free units. |
| A07 | Bike candidates include a free BIKE spot, partially occupied CAR spot and empty CAR spot | Prefer BIKE; when unavailable prefer partial CAR; then empty CAR. Break ties by numeric floor and spot ordering, including floors 2 and 10. |
| A08 | Park `"KA 05 AB 1234"`, then `ka05ab1234` | Second request returns `VEHICLE_ALREADY_PARKED`. Original ticket remains the only active ticket. |
| A09 | Parse state and BH formats, blank/null values and punctuation | Both documented normalized patterns pass; invalid inputs fail. Uppercase normalization is unchanged under a Turkish default locale. |
| A10 | Unpark CAR after 70 minutes / BIKE after 90 minutes / zero duration | Fees INR 23.33 / INR 15.00 / INR 0.00; duration and stored exit agree. |
| A11 | BIKE after exactly 1.8 seconds (INR 0.005 before rounding) | Fee INR 0.01 using HALF_UP; fractional seconds are not discarded. |
| A12 | Exit time precedes entry | `INVALID_EXIT_TIME`; ticket/allocation/index remain active; history and revenue unchanged. |
| A13 | Unpark one of two bikes sharing a CAR spot | Only the matching allocation is removed; spot becomes PARTIAL, other bike remains findable. |
| A14 | Unpark successfully, then unpark same ticket; separately use unknown ID | First retry returns `TICKET_ALREADY_CLOSED`; unknown ID returns `TICKET_NOT_FOUND`. Only one closed record and one fee exist. |
| A15 | Find parked vehicle and occupied spot; unpark then find vehicle | Lookups include correct floor, spot and ticket; post-exit vehicle lookup returns `VEHICLE_NOT_PARKED`. Unknown floor/spot IDs return their specific errors. |
| A16 | Reserve a nonparked registration while lot is full | Record created ACTIVE; no allocations, occupancy or guaranteed capacity change. |
| A17 | Reserve twice with equivalent normalized registrations, including different types | `ACTIVE_RESERVATION_EXISTS`; first record unchanged. Reserving a currently parked registration returns `VEHICLE_ALREADY_PARKED`. |
| A18 | Reserve with missing, invalid, past or equal-to-now expiry | Input rejected; no record created. |
| A19 | Matching reservation and successful park before expiry | Same transaction creates ticket and marks reservation FULFILLED with ticket ID. No fee or spot-selection advantage. |
| A20 | Matching reservation but no eligible spot; separately, active reservation type mismatch | No-space case leaves reservation unconsumed; mismatch returns `RESERVATION_TYPE_MISMATCH` with no parking mutation. |
| A21 | Query/cancel/park exactly at reservation expiry | Query shows EXPIRED; cancel fails with `RESERVATION_NOT_ACTIVE`; parking is ordinary parking and does not fulfill expired record. A new reservation is allowed. |
| A22 | Cancel ACTIVE twice; cancel unknown or FULFILLED | First cancellation transitions to CANCELLED, second is no-op success; unknown returns `RESERVATION_NOT_FOUND`; FULFILLED returns `RESERVATION_NOT_ACTIVE`. |
| A23 | List reservations, filtered and unfiltered, including no matches | Stable records with effective status; empty list succeeds. Terminal records remain available for the session. |
| A24 | One CAR spot with one bike and one empty BIKE spot | Occupancy 33.33%, current vehicles 1, FREE 1, PARTIAL 1, FULL 0, car placements 0, bike placements 2. |
| A25 | Two completed visits by same registration plus one active vehicle | Completed visits 2, current vehicles 1; revenue is sum of only the two closed fees. By-type totals reconcile. |
| A26 | Closed tickets exit just before `from`, at `from`, just before `to`, at `to` | Include only the middle two. Entry before `from` does not exclude a qualifying exit. Current occupancy unchanged by historical range. |
| A27 | No history; invalid or one-sided range | No history returns zero revenue/counts; invalid range or only one bound returns `INVALID_INPUT`. |
| A28 | Force policy to return incompatible/full/nonexistent spot | Aggregate rejects without changing state, even though a policy supplied the spot. |
| A29 | Inject history-save or commit failure during unpark | Error result, no success receipt; original active ticket, allocation and vehicle index remain; no closed record or revenue. Retry with healthy storage succeeds once. |
| A30 | Inject failure after allocation staging, reservation fulfillment or cancellation staging | Entire working copy discarded; original lot and reservation state unchanged. |
| A31 | Generated ID collides; constructor input/view collections are changed externally | Collision cannot overwrite data; external collection changes cannot mutate the aggregate. Returned snapshots cannot close live tickets. |
| A32 | Interactive malformed command followed by valid command; batch malformed command | Interactive session continues; batch stops on that line and returns appropriate nonzero code. Expected errors have no stack trace. |
| A33 | Help, quoted registrations, blank lines, EOF and exit | Documented parsing works; help/exit need no lot; blank lines ignored and normal termination returns 0. |
| A34 | Close and reopen application after a working session | New process is empty and startup explains state loss; no claim of durable reservations/revenue. |

## Cross-object checks

Run an invariant assertion after every successful or failed mutation in service tests: active tickets and allocations are one-to-one; registration index matches active tickets; spot capacities and compatibility hold; closed history is disjoint from active tickets; fulfilled reservations reference their successful tickets. Compare complete business snapshots around injected failures, not just spot counts.

## Delivery order

1. **Restore a compiling baseline.** Complete `findSpot`, fix test-source compilation and registration test inputs, and correct capacity/status defects recorded in the review. Run the Java 21 build/tests before building on these classes.
2. **Implement domain behavior.** Immutable inventory/IDs and protected collections; aggregate allocation/release; ticket and reservation lifecycles; deterministic selection, exact decimal fees and injected timestamps. Cover A03–A23, A28 and A31 at the appropriate domain/policy level.
3. **Add in-memory orchestration.** One shared session store with atomic replacement, repository ports and application services. Exercise complete park/unpark cycles and failure recovery (A24–A30).
4. **Complete CLI and query views.** Bootstrap services, interactive and file modes, commands, DTOs, stable errors and help. Cover A01–A02 and A32–A34 plus end-to-end park/reserve/unpark/status/analytics.
5. **Release check.** Run the full acceptance suite and packaged-launcher smoke test. Confirm remaining recommended decisions with the product owner; update docs and tests together if changed. No SQLite, queue or extra architecture framework is required for v1.

## Demonstration script

Run the commands below in one implemented interactive session; substitute the actual ticket IDs returned by `park` when unparking. This is an acceptance walkthrough, not a claim that today's scaffold executes it.

```text
create-lot --floor 1:1:1 --floor 2:1:0
park --registration KA05AB1234 --type CAR
park --registration MH12C3456 --type BIKE
find-vehicle --registration KA05AB1234
find-spot --spot F1-C1
status
analytics
unpark --ticket <first-ticket-id>
status
analytics
exit
```

Use a separate fixed-clock fixture for reservation expiry and exact fees; a real-time manual demonstration cannot prove time-boundary correctness. Acceptance is complete only with verified results, not simply because the cases are documented.
