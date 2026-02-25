## ADDED Requirements

### Requirement: Frontend emits structured HTTP request logs in development
The frontend SHALL log every outbound HTTP request in development mode with method, URL, status code, and elapsed time.

#### Scenario: Successful request is logged
- **WHEN** a frontend action triggers a successful backend call
- **THEN** a structured log entry SHALL include method, full URL, response status, and duration

#### Scenario: Failed request is logged with error context
- **WHEN** a frontend request fails by network error or non-2xx status
- **THEN** a structured log entry SHALL include method, URL, status/error code, and failure message

### Requirement: Backend exposes observable access logs for incoming frontend traffic
Each backend service consumed by the frontend SHALL expose logs that make inbound HTTP requests visible during local development.

#### Scenario: Request trace appears in service logs
- **WHEN** frontend calls a backend endpoint in the integration environment
- **THEN** the corresponding backend service logs SHALL show request method, path, and resulting status

### Requirement: End-to-end request observability is part of acceptance
The integration workflow SHALL include explicit verification that business actions from UI produce visible request traces in both frontend and backend logs.

#### Scenario: Purchase flow produces trace evidence
- **WHEN** a user performs login, product listing, cart confirmation, and order creation in local app
- **THEN** trace evidence for each HTTP hop SHALL be visible and attributable in command-line logs
