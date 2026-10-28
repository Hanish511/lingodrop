# RFC-001: LingoDrop System Overview & Architecture Baseline

## 1. Context & Motivation
LingoDrop is a real-time, cross-lingual chat platform designed to eliminate language barriers. Users send messages in their native tongue, and the system dynamically intercepts, translates, and distributes localized payloads so all recipients read the message in their preferred language. The architectural challenge centers on real-time fan-out efficiency: preventing $O(N)$ translation overhead by grouping subscribers by language and dispatching asynchronous, concurrent translation requests without blocking the primary chat event loop.

## 2. System Container Architecture
```mermaid
graph TD
    Client["React PWA"] -->|"HTTPS/REST (Auth, Room Lifecycle)"| Server["Spring Boot Backend"]
    Client -->|"WSS/STOMP (Live Chat Stream)"| Server
    Server -->|"JDBC/SQL"| Database[("PostgreSQL")]
    Server -->|"Async REST/HTTPS"| External["External Translation API"]
```

## 3. Real-Time Translation Fan-Out Lifecycle
```mermaid
sequenceDiagram
        participant Sender as User A (English)
        participant Backend as Spring Boot
        participant Translator as Translation API
        participant Spanish as User B, User C (Spanish)
        participant Japanese as User D (Japanese)
        
        Sender ->> Backend: STOMP: "Hello"
        Note over Backend: Deduplicate and group subscribers:</br>[ES: B, C],[JA: D]
        
        par Parallel Async Translations (CompletableFuture)
            Backend ->> Translator: POST /translate (target: "es", text: "Hello")
            Translator -->> Backend: 200 OK: "Hola"
            Backend ->> Spanish: STOMP: "Hola"
        and
            Backend ->> Translator: POST /translate (target: "ja", text: "Hello")
            Translator -->> Backend: 200 OK: "Konnichiwa"
            Backend ->> Japanese: STOMP: "Konnichiwa"
        end
```

## 4. Persistent Relational Data Model
```mermaid
erDiagram
    USERS {
        uuid id PK
        string username
        string password_hash
        string native_language
        timestamp created_at
    }

    CHANNELS {
        uuid id PK
        string name
        uuid created_by FK
        boolean is_private
        timestamp created_at
    }

    CHANNEL_MEMBERS {
        uuid channel_id PK,FK
        uuid user_id PK,FK
        timestamp joined_at
    }

    MESSAGES {
        uuid id PK
        uuid channel_id FK
        uuid sender_id FK
        string original_content
        string source_language
        timestamp created_at
    }

    MESSAGE_TRANSLATIONS {
        uuid id PK
        uuid message_id FK
        string language_code
        string translated_text
    }

    USERS ||--o{ CHANNELS : "creates"
    USERS ||--o{ CHANNEL_MEMBERS: "Junction connection"
    CHANNELS ||--o{ CHANNEL_MEMBERS: "Junction connection"
    USERS ||--o{ MESSAGES: "writes"
    CHANNELS ||--o{ MESSAGES: "contains"
    MESSAGES ||--o{ MESSAGE_TRANSLATIONS: "translations"
```

## 5. Architectural Decisions & Operational Trade-Offs
* **Repository Layout:** Monorepo architecture (`/server`, `/client`, `/docs`) to maintain unified versioning, atomic commits, and centralized documentation.
* **Storage Boundaries:** 
  * *Casual Mode:* Entirely in-memory via `ConcurrentHashMap` with TTL expiration and connection-drop listeners to eliminate database write amplification from ephemeral sessions.
  * *Ranked Mode:* Persisted in PostgreSQL using a normalized schema with 1-to-N message translations to support zero-schema-drift localized chat histories.
* **Concurrency Model:** Asynchronous non-blocking fan-out using Java `CompletableFuture` combined with HTTP connection pooling to prevent third-party API latency from bottlenecking the real-time STOMP message broker.
* **Git Conventions:** GitHub Flow with Conventional Commits (`feat:`, `fix:`, `docs:`, `test:`, `refactor:`) using the imperative mood.