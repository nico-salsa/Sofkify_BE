## ADDED Requirements

### Requirement: Backend repository SHALL provide architecture context for workspace explanations
The backend repository SHALL include a maintained architecture summary that describes service boundaries, interaction patterns, and domain constraints needed for `@workspace /explain`.

#### Scenario: Developer asks for architecture summary in workspace chat
- **WHEN** a developer requests `@workspace /explain` in the backend repository
- **THEN** the available context MUST describe the four backend services, their responsibilities, and synchronous/asynchronous integration boundaries

### Requirement: Backend repository SHALL define Copilot coding guardrails
The backend repository SHALL provide `.github/copilot-instructions.md` with explicit rules for architecture direction, layering, contracts, and business invariants.

#### Scenario: Copilot generates backend code suggestions
- **WHEN** Copilot proposes code changes for backend files
- **THEN** the guidance MUST enforce hexagonal boundaries (`infrastructure -> application -> domain`) and MUST avoid domain logic in adapters/controllers

### Requirement: Copilot guidance MUST preserve existing backend contracts
Repository guidance MUST state that context setup does not change runtime behavior, service APIs, database schema ownership, or message contracts.

#### Scenario: Team performs context-setup change
- **WHEN** the team completes this context configuration change
- **THEN** no service endpoint, persistence contract, or RabbitMQ contract MUST be modified by this change alone
