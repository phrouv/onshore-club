# Diagrams Template

## Purpose

Generate detailed technical diagrams using Mermaid syntax for component interactions, sequences, and state flows.

## Template Variables

- `{{COMPONENT_DIAGRAM}}` - Mermaid diagram showing component interactions
- `{{SEQUENCE_DIAGRAM}}` - Mermaid diagram illustrating execution order
- `{{STATE_DIAGRAM}}` - Mermaid diagram for workflows with multiple states (optional)

## Output Format

```markdown
## Diagrams

### Component Interaction Diagram

```mermaid
{{COMPONENT_DIAGRAM}}
```

### Sequence Diagram

```mermaid
{{SEQUENCE_DIAGRAM}}
```

### State Flow Diagram

```mermaid
{{STATE_DIAGRAM}}
```

```

## Example Usage

```markdown
## Diagrams

### Component Interaction Diagram

```mermaid
graph TB
    subgraph "Frontend Layer"
        UI[Payment UI]
        Validation[Input Validation]
    end
    
    subgraph "Backend Layer"
        Controller[Payment Controller]
        Service[Payment Service]
        Repository[Payment Repository]
    end
    
    subgraph "External Services"
        PSP[Payment Service Provider]
        DB[(Database)]
    end
    
    UI --> Validation
    Validation --> Controller
    Controller --> Service
    Service --> Repository
    Service --> PSP
    Repository --> DB
```

### Sequence Diagram

```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant PaymentAPI
    participant PaymentService
    participant PSP
    participant Database
    
    User->>Frontend: Initiate Payment
    Frontend->>PaymentAPI: POST /api/payments
    PaymentAPI->>PaymentService: processPayment()
    PaymentService->>Database: Save payment record
    PaymentService->>PSP: Send payment request
    PSP-->>PaymentService: Payment response
    PaymentService->>Database: Update payment status
    PaymentService-->>PaymentAPI: Return result
    PaymentAPI-->>Frontend: Payment confirmation
    Frontend-->>User: Display result
```

### State Flow Diagram

```mermaid
stateDiagram-v2
    [*] --> Initiated
    Initiated --> Processing : Submit Payment
    Processing --> Completed : Success
    Processing --> Failed : Error
    Processing --> Pending : Async Processing
    Pending --> Completed : Confirmation
    Pending --> Failed : Timeout
    Failed --> [*]
    Completed --> [*]
```

```

## Mermaid Diagram Reference

### Component Diagrams
```mermaid
graph TB
    A[Component A] --> B[Component B]
    B --> C{Decision}
    C -->|Yes| D[Action 1]
    C -->|No| E[Action 2]
```

### Sequence Diagrams

```mermaid
sequenceDiagram
    participant A as Actor A
    participant B as System B
    A->>B: Synchronous call
    A-->>B: Asynchronous call
    B-->>A: Return
```

### State Diagrams

```mermaid
stateDiagram-v2
    state "State Name" as s1
    [*] --> s1
    s1 --> [*]
```

### Class Diagrams

```mermaid
classDiagram
    class ClassName {
        +attribute : type
        +method() : returnType
    }
```

## Guidelines

- Use component diagrams to show how different parts of the system interact
- Use sequence diagrams to illustrate the order of operations and message flow
- Use state diagrams for complex workflows with multiple states and transitions
- Keep diagrams focused and not overly complex
- Use descriptive names for all components and interactions
- Include decision points and error flows where relevant
- Group related components using subgraphs for better organization
- Ensure diagrams complement and enhance the written documentation
