## ADDED Requirements

### Requirement: Unified local runtime orchestration across repositories
The system SHALL provide a documented integration runtime that starts frontend and backend from their separate repositories with a shared Docker network and deterministic dependency order.

#### Scenario: Integration stack boots from a single workflow
- **WHEN** a developer executes the documented integration startup command
- **THEN** frontend and required backend services SHALL become reachable in the same runtime without manual network setup

#### Scenario: Cross-repository service discovery works
- **WHEN** the frontend resolves configured backend hostnames inside the integration runtime
- **THEN** service-name resolution SHALL succeed without host-file edits or ad-hoc port rewrites

### Requirement: Frontend service endpoints are configured explicitly per backend domain
The system SHALL define explicit frontend environment configuration for each consumed backend domain (users, products, carts, orders) instead of ambiguous implicit routing.

#### Scenario: Environment variables map to service domains
- **WHEN** the frontend starts in local or container mode
- **THEN** it SHALL read domain-specific API base URLs from environment variables and use them for every request

#### Scenario: Missing configuration fails fast
- **WHEN** required service URL configuration is absent or malformed
- **THEN** the runtime SHALL surface a clear startup/runtime error instead of silently falling back to mock behavior

### Requirement: API contract alignment is verifiable before merge
The system SHALL include a verification step that proves frontend routes and payloads match backend exposed endpoints for critical purchase flows.

#### Scenario: Contract mismatch is detected
- **WHEN** frontend references an endpoint or payload not implemented by backend
- **THEN** verification SHALL fail with actionable mismatch details before the change is considered complete
