# Module Dependency Diagram

**Generated:** 2025-01-XX  
**Purpose:** Visual representation of module relationships and orchestration flow

---

## Module Dependency Graph

```mermaid
graph TD
    %% Shared Module
    common[common<br/>Shared/Infrastructure]
    
    %% Domain Modules
    user[user<br/>Domain]
    product[product<br/>Domain]
    inventory[inventory<br/>Domain]
    payment[payment<br/>Domain]
    billing[billing<br/>Domain ⚠️]
    notifications[notifications<br/>Domain]
    
    %% Orchestration Modules
    order[order<br/>Orchestration]
    admin[admin<br/>Orchestration]
    
    %% Dependencies from common
    common --> user
    common --> product
    common --> inventory
    common --> payment
    common --> billing
    common --> notifications
    common --> order
    common --> admin
    
    %% Domain to Orchestration
    user --> order
    product --> order
    inventory --> order
    billing --> order
    notifications --> order
    
    user --> admin
    
    %% Violation - Domain to Domain
    payment -.->|⚠️ VIOLATION| billing
    
    %% Styling
    classDef shared fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    classDef domain fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef orchestration fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef violation stroke:#d32f2f,stroke-width:3px,stroke-dasharray: 5 5
    
    class common shared
    class user,product,inventory,payment,notifications domain
    class order,admin orchestration
    class billing violation
```

---

## Module Layer Diagram

```mermaid
graph TB
    subgraph "Layer 1: Shared"
        common[common]
    end
    
    subgraph "Layer 2: Domain Modules"
        user[user]
        product[product]
        inventory[inventory]
        payment[payment]
        billing[billing ⚠️]
        notifications[notifications]
    end
    
    subgraph "Layer 3: Orchestration Modules"
        order[order]
        admin[admin]
    end
    
    common --> user
    common --> product
    common --> inventory
    common --> payment
    common --> billing
    common --> notifications
    
    user --> order
    product --> order
    inventory --> order
    billing --> order
    notifications --> order
    
    user --> admin
    
    payment -.->|⚠️| billing
    
    style billing fill:#ffebee,stroke:#d32f2f,stroke-width:3px
```

---

## Orchestration Flow Diagram

```mermaid
sequenceDiagram
    participant Client
    participant Order as order<br/>(Orchestration)
    participant Product as product<br/>(Domain)
    participant Inventory as inventory<br/>(Domain)
    participant Billing as billing<br/>(Domain)
    participant Payment as payment<br/>(Domain)
    participant Notifications as notifications<br/>(Domain)
    
    Client->>Order: createOrder()
    
    Note over Order,Product: Direct Service Call ⚠️
    Order->>Product: getProductById()
    Product-->>Order: ProductResponse
    
    Note over Order,Inventory: Direct Service Call ⚠️
    Order->>Inventory: reserveInventory()
    Inventory-->>Order: InventoryResponse
    
    Note over Order,Billing: Adapter Pattern ✅
    Order->>Billing: createPayment() via BillingAdapter
    
    Note over Billing,Payment: Direct Service Call ⚠️
    Billing->>Payment: processPayment()
    Payment-->>Billing: Payment
    
    Billing-->>Order: PaymentResponse
    
    Note over Order,Notifications: Direct Service Call ⚠️
    Order->>Notifications: sendNotification()
    Notifications-->>Order: Success
    
    Order-->>Client: OrderResponse
```

---

## Dependency Violation Highlight

```mermaid
graph LR
    subgraph "Domain Modules"
        payment[payment<br/>Domain]
        billing[billing<br/>Domain]
    end
    
    payment -->|⚠️ VIOLATION<br/>compile scope| billing
    
    style payment fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    style billing fill:#ffebee,stroke:#d32f2f,stroke-width:3px
```

**Issue:** Direct domain-to-domain dependency violates architectural principle that domain modules should be independent.

**Recommended Fix:**
```mermaid
graph LR
    subgraph "Domain Modules"
        payment[payment<br/>Domain]
    end
    
    subgraph "Orchestration Modules"
        billing[billing<br/>Orchestration]
    end
    
    payment -.->|via adapter| billing
    
    style payment fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    style billing fill:#fff3e0,stroke:#e65100,stroke-width:2px
```

---

## Module Type Distribution

```mermaid
pie title Module Types
    "Domain Modules" : 6
    "Orchestration Modules" : 2
    "Shared Module" : 1
```

---

## Dependency Count Visualization

```mermaid
graph LR
    subgraph "Outgoing Dependencies"
        A[common: 0] 
        B[user: 1]
        C[product: 1]
        D[inventory: 1]
        E[payment: 1]
        F[billing: 2 ⚠️]
        G[notifications: 1]
        H[order: 6]
        I[admin: 2]
    end
    
    style F fill:#ffebee
```

---

## Recommended Architecture (After Fixes)

```mermaid
graph TD
    %% Shared Module
    common[common<br/>Shared]
    
    %% Domain Modules
    user[user<br/>Domain]
    product[product<br/>Domain]
    inventory[inventory<br/>Domain]
    payment[payment<br/>Domain]
    notifications[notifications<br/>Domain]
    
    %% Orchestration Modules
    billing[billing<br/>Orchestration ✅]
    order[order<br/>Orchestration]
    admin[admin<br/>Orchestration]
    
    %% Dependencies
    common --> user
    common --> product
    common --> inventory
    common --> payment
    common --> notifications
    common --> billing
    common --> order
    common --> admin
    
    %% Orchestration dependencies (via adapters)
    user -.->|adapter| order
    product -.->|adapter| order
    inventory -.->|adapter| order
    payment -.->|adapter| billing
    billing -.->|adapter| order
    notifications -.->|adapter| order
    
    user -.->|adapter| admin
    
    %% Styling
    classDef shared fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    classDef domain fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef orchestration fill:#fff3e0,stroke:#e65100,stroke-width:2px
    
    class common shared
    class user,product,inventory,payment,notifications domain
    class billing,order,admin orchestration
```

**Key Changes:**
- `billing` reclassified as orchestration module
- All domain-to-orchestration communication via adapters (dashed lines)
- No direct domain-to-domain dependencies

---

## Module Interaction Patterns

### Current Pattern (With Violations)

```mermaid
graph TD
    O[order] -->|direct service| P[product]
    O -->|direct service| I[inventory]
    O -->|adapter ✅| B[billing]
    O -->|direct service| N[notifications]
    B -->|direct service ⚠️| Pay[payment]
    
    style B fill:#ffebee
    style Pay fill:#ffebee
```

### Recommended Pattern

```mermaid
graph TD
    O[order] -->|adapter| P[product]
    O -->|adapter| I[inventory]
    O -->|adapter| B[billing]
    O -->|adapter| N[notifications]
    B -->|adapter| Pay[payment]
    
    style O fill:#fff3e0
    style B fill:#fff3e0
    style P fill:#f3e5f5
    style I fill:#f3e5f5
    style N fill:#f3e5f5
    style Pay fill:#f3e5f5
```

---

## Usage Instructions

### Viewing the Diagrams

1. **In Markdown viewers:** Most modern Markdown viewers (GitHub, GitLab, VS Code with extensions) will render Mermaid diagrams automatically.

2. **Online:** Copy the Mermaid code blocks to [Mermaid Live Editor](https://mermaid.live/) for interactive viewing.

3. **VS Code:** Install the "Markdown Preview Mermaid Support" extension.

4. **Documentation:** Include these diagrams in your architecture documentation for visual reference.

### Updating Diagrams

When module dependencies change:
1. Update the dependency graph
2. Update the layer diagram if module classification changes
3. Update the orchestration flow if interaction patterns change
4. Regenerate violation highlights if new issues are found

---

**Report Status:** ✅ Complete  
**Diagram Format:** Mermaid (compatible with GitHub, GitLab, and most Markdown viewers)

