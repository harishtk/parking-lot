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
## Supported Features
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
## Version 1
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



