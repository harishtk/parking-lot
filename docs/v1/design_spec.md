# Parking Lot System Design Specification (Phase 1-3)
> This document capture all design decisions finalized.

# 1. Objective
Design a **Parking Lot Management System as a Java Picocli CLI application.**  

The goal is to create a design that is:
* Object-oriented
* Extensible
* Clean Architecture aligned
* DDD-inspired (without overengineering)
* Persistence-agnostic
* In-memory first
* SQLite-ready later
---
# 2. Scope
### **Supported Features**
### Parking Operations
```
Park Vehicle
Unpark Vehicle
Generate Ticket
Calculate Fee
```
### Multiple Floors
```
One ParkingLot
Multiple Floors
Multiple Spots per Floor
```
### Vehicle Types
```
CAR
BIKE
```
### Reservation Support
```
Registration number based reservations
Soft reservations
Allocation priority eligible
```
### Search
```
Find Vehicle
Find Spot
```
### Visibility
```
Parking Status
Occupancy Status
```
### Analytics
```
Revenue
Vehicle Count
Occupancy
Floor Utilization
```
---
# 3. Explicitly Out Of Scope
### Version 1
```
Electric charging stations
Multiple parking lots
Distributed deployment
Concurrency
Networking
Authentication
Authorization
Billing integrations
```
---
# 4. Architecturla Principles
### Principle 1
---
All **state-changing operations go through** `ParkingLot`  
~~`Floor.allocate()`~~ -> `ParkingLot.allocate()`  
~~`ParkingSpot.reserve()`~~ -> `ParkingLot.reserve()`  
~~`Ticket.close()`~~ -> `ParkingLot.release()`

### Principle 2
---
**Policies Decide, Aggregate Mutates**
```java
SpotId spotId =
    policy.selectSpot(...);

parkingSlot.allocate(...);
```
### Principle 3
---
**Single Source of Truth**

Runtime operational state belongs inside: `ParkingLot`
Avoid duplicated operational state inside: `Ticket`,`ParkingSpot`, etc

### Principle 4
---
**Histoical State is Separate**  

  Operational state: `ParkingLot`  
  Historical state: `TicketRepository`

---
# 5. Domain Model
### Aggregate Root
`ParkingLot`

### Entities  

`ParkingLot`
1. Allocate vehicle
2. Release vehicle
3. Create reservation
4. Cancel reservation
5. Manage active tickets
6. Manage reservations
7. Maintain

`Floor` represents a physical floor, contains `ParkintSpot[]`

`ParkingSpot` represents a parking spot, contains `SpotType`, `Capacity`, `Occupancy Allocations` and enforces *Capacity invariants*

`Allocation` represents *Current occupancy*, A spot can have: 1 `CAR` or 2 `BIKE`. Allocation tracks *Which vehicle occupies capacity*

`Ticket` represents *Parking transaction*. Ticket is historical and hence contains snapshot data.

`Reservation` represents *Parking priority eligibility*. Reservation does NOT reserve a specific spot.
---
# 6. Value Objects
   Used to avoid primitive possession.
### Identifiers
```
TicketId
SpotId
FloorId
AllocationId
ReservationId
```
### Money
```
Money
```
Represents *Fee, Revenue and Payments*
---
# 7. Enums
### VehicleType - `CAR`, `BIKE`
### SpotType - `CAR`, `BIKE`
### TicketStatus - `ACTIVE`, `CLOSED`
### ReservationStatus - `ACTIVE`, `CLOSED`
---
# 8. Capacity Model
### Vehicle Capacity
```
BIKE = 1 Unit
CAR  = 2 Units
```
### Spot Capacity  
Allows
```
1 CAR 
or 
2 BIKE
```
Rule Evaluation  
`remainingCapacity >= requiredCapacity`
---
# 9. Reservation Model

**Soft Reservation** - Reservation means *Priority* not *Guaranteed Spot*. Allocation strategy considers *Reservation Presnet* as a ranking signal.
---
# 10. Historical State
### TicketRepository
Stores
> Closed Tickets  
> Historical Tickets  
> Revenue Data  
> Analytics Data  

### ParkingLot
Stores
> Only Active Tickets
---
# 11. Domain Policies
### `SpotSelectionPolicy`
Responsibilites:  
* Choose best spot

Input:  
* Vehicle Type
* Reservations
* Available Capacity
* Spot Inventory

Output:  
* `SpotId`  

Never mutates state

---
### `FeeCalculationStrategy`
Responsibility:
* Fee Calculation

Never mutates state

Fee model:
```
Bike = Rs. 10/hr
Car  = Rs. 20/hr

Prorated Calculation

Eg. CAR 70 mins
20 * (70 / 60) ~= 23
```

# 12. Repository Design
### `ParkingLotRepository`
Single parking lot system.
```
ParkingLot load();
void save(ParkingLot parkingLot);
```
### `TicketRepository`
```
save(Ticket);
find(TicketId);
findAll();
```
Contains *Historical Tickets* only.
---
# 12. Application Services
### `ParkingService`
Responsibilities:
* Park Vehicle
* Unpark Vehicle
* Find Vehicle
* Status  

Dependencies:
* ParkingLotRepository
* TicketRepository
* SpotSelectionPolicy
* FeeCalculationStrategy
* Clock
* ID Generators
---
### `ReservationService`
Responsibilites:
* Create Reservation
* Cancel Reservation
* List Reserations

Dependencies:
* ParkingLotRepository
---
### `AnalyticsService`
Responsibilites:
* Revenue
* Occupancy
* Vehicle Counts
* Floor Utilization

Dependencies:
* ParkingLotRepository
* Ticket Repository

**Read-only.**
---
# 14. ID Generation
ID generation is external (at least for now)
### Generators
* `TicketIdGenerator`
* `AllocationIdGenerator`
* `ReservationIdGenerator`
---
# 15. Time Handling
Use injected clock.  
~~`Instant.now()`~~ -> `Instant.now(clock)`  
Benefits:
* Testability
* Consistent timestamps
* Time-travel state
---









