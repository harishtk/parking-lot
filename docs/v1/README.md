# V1 development handover

Prepared: 25 September 2026. Scope: one Java 21 / Picocli parking-lot CLI.

## Read in this order

1. [Consolidated specification](handover.md): scope, decisions, commands, domain rules, architecture and transaction behavior.
2. [Acceptance and delivery plan](acceptance.md): observable acceptance cases and implementation order.
3. [Implementation review](implementation-review.md): conflicting requirements, current code defects and verification limitations.

These three documents form the consolidated handover baseline. The specification distinguishes requirements preserved from the brainstorm from recommended decisions added to make implementation concrete. Recommendations are not previously approved product requirements. The product owner explicitly confirmed that v1 retains reservation records and defers priority on 25 September 2026. Remaining recommendations are clearly listed for review.

## Original material

The existing files are preserved as historical inputs, not competing implementation specifications. Where they disagree, use the consolidated baseline and its explicit decision register.

| Input | Consolidated coverage |
| --- | --- |
| `functional_requirements.txt`, `non_functional_requirements.txt` | Handover sections 1–3, 8; acceptance cases |
| `design_spec.md` | Handover sections 1–8 |
| `findings.txt` | Decision register, allocation policy, invariants, analytics |
| `behavior_contracts.txt` | Domain contracts and use-case flows |
| `p3_usecase_modeling.txt` | CLI contract, flows, acceptance cases |
| `p4_sequence_diagram.txt`, `p5_transaction_boundaries.txt` | Atomic command boundary and unpark flow |
| `code_architecture.txt` | Dependency direction and package responsibilities |
| `class_diagram_v1.drawio` | Consolidated ownership diagram; original diagram is historical |
| `init.txt` | Delivery sequence |

Review also inspected the existing Java sources, tests and build configuration. Application code was not changed by this documentation task.
