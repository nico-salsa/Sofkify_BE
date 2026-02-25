## ADDED Requirements

### Requirement: Repository cleanup removes obsolete integration blockers
Both repositories SHALL remove obsolete or contradictory artifacts that hide real HTTP integration behavior.

#### Scenario: Mock-only product source is removed from runtime path
- **WHEN** the frontend product listing flow executes in local environment
- **THEN** it SHALL obtain data via HTTP backend calls instead of static in-memory mock datasets

#### Scenario: Contradictory Docker/readme instructions are reconciled
- **WHEN** a developer follows documented startup steps
- **THEN** the documented commands SHALL match actual executable infrastructure for combined FE/BE runtime

### Requirement: Both repositories provide operational README guidance
Each repository SHALL include a concise README with prerequisites, startup steps, environment variables, and verification commands for local integration.

#### Scenario: New contributor can run integration from README only
- **WHEN** a contributor follows README instructions from clean clone
- **THEN** they SHALL be able to start services and verify real frontend-to-backend requests without tribal knowledge

### Requirement: Cleanup changes are traceable and reviewable
Repository hygiene updates SHALL preserve auditability through explicit change lists and rationale.

#### Scenario: Cleanup is documented
- **WHEN** files are removed, consolidated, or rewritten during cleanup
- **THEN** the change record SHALL identify what changed and why it was considered obsolete or redundant
