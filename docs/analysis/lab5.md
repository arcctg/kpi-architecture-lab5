# Lab 5: Modular Monolith

## Part I — Lab 5 Analysis

### 1. How did you identify the bounded contexts? What alternatives did you consider?

The system was split into two contexts: Core and Analytics.

**Core** encapsulates everything built across Labs 1-4: user registration and authentication, deck and card lifecycle management, the layered domain model, CQS command/query handlers, and the event publication mechanism. It owns the write side of the system and enforces all business invariants.

**Analytics** is a read-only consumer that aggregates user activity data into its own projection model (`UserActivitySummary`). It does not share tables with Core, does not call Core's services directly, and has no influence over Core's state.

**Why this split?**

The most important signal was that Core and Analytics have different reasons to change and different consistency requirements. A change in the deck domain model (e.g., adding a `description` field) must not require changes in the analytics projection. Conversely, adding a new analytics metric must not touch Core business logic. This maps directly to Conway's Law: the two contexts reflect naturally independent capabilities.

**Alternatives considered:**

- **Single bounded context with an analytics package.** Rejected because it would allow direct queries from analytics code into the Core domain model, creating invisible coupling that grows over time.
- **Three contexts: Core / Audit / Analytics.** The activity log from Lab 4 was a natural candidate for its own context. However, because the lab's analytics context already builds its projection from the same integration events, promoting the activity log to a full bounded context would add overhead without introducing a meaningfully independent capability. It was retained inside Core (`core.activitylog`) as an internal, non-published cross-cutting concern, while Analytics was elevated to its own module.
- **Splitting Core itself** (e.g., `UserManagement` + `FlashcardContent`). This would be a valid DDD decomposition for a larger team, but at the current scale it would produce two modules that share the same tables, the same transactional boundaries, and the same development lifecycle — a sign that the boundary is premature.

The chosen split produces two modules with clearly distinct responsibilities, different data models, and different consistency guarantees.

---

### 2. How does the ACL protect modules from changes in other modules?

The **Anti-Corruption Layer** lives in `com.flashcard.analytics.acl` and is the only place in the Analytics module that is aware of the shared integration event format.

The key class is `CoreEventTranslator`, which maps each incoming integration event (e.g., `DeckCreated`, `CardAdded`) into the Analytics module's internal `ActivityRecord` value object:

```java
// analytics/acl/CoreEventTranslator.java
public static ActivityRecord fromDeckCreated(DeckCreated event) {
    return new ActivityRecord(
            event.ownerId(), ActivityType.DECK_CREATED,
            "DECK", event.deckId(), event.occurredAt());
}
```

**What protection means in practice:**

Suppose the Core team renames `DeckCreated.deckId()` to `DeckCreated.id()`. Without an ACL, this rename would propagate silently into every Analytics handler, listener, and aggregate that references the field. With the ACL in place, the change is absorbed in a single method: `CoreEventTranslator.fromDeckCreated()`. The Analytics domain model (`UserActivitySummary`, `ActivityRecord`) is completely unaffected; it only knows about its own types.

The Analytics module's event listeners receive the integration event, immediately delegate to `CoreEventTranslator`, and work exclusively with `ActivityRecord` from that point forward. The external event type never crosses the ACL boundary into the analytics `application` or `domain` layers.

Similarly, the Core module's **public contract** (`CoreModuleApi`) is the only surface Analytics may call synchronously. Analytics code never imports from `core.domain`, `core.application`, or `core.infrastructure` — it is limited to `core.api` and `shared.event`. This means that any internal Core refactoring (changing a command handler, restructuring the domain model) is invisible to Analytics as long as `CoreModuleApi` stays stable.

---

### 3. Where is strong consistency in the system? Where is eventual consistency? Why is this acceptable?

**Strong consistency — within the Core module**

All primary business operations (create deck, add card, register user) execute within a single `@Transactional` boundary in the Core module. The command handler persists the domain aggregate and publishes the integration event within the same unit of work. The client receives a `201 Created` or `200 OK` only after the domain state has been durably committed to the Core's database tables. There is no partial write: either the deck is created and the event is queued, or neither happens.

**Eventual consistency — between Core and Analytics**

After Core commits, the Spring `ApplicationEventPublisher` dispatches integration events to the Analytics module's `@EventListener` methods, which run in a separate `@Async` thread pool. The Analytics projection (`UserActivitySummary`) is updated asynchronously and will momentarily lag behind the Core state.

**Why is this acceptable?**

Analytics data is inherently retrospective. A user querying "how many decks did I create this month?" is not making a business-critical decision that depends on the last 200 milliseconds of activity. The analytics projection does not block or influence any Core operation — it is a read-side artifact, not a primary state store. Eventual consistency in this context means: the answer will be correct within seconds, and this is sufficient for the analytics use case.

More importantly, this model aligns with the **CAP theorem trade-off** the system makes: by relaxing consistency between modules, we gain availability and partition tolerance. If the Analytics module's database is temporarily unavailable, Core operations continue unaffected. Auditing and aggregation happen once the module recovers. In a fully synchronous system, an analytics database failure would propagate to the user as a 500 error on card creation — an unacceptable coupling for a side-effect concern.

---

### 4. What would change if one module needed to be extracted into a separate service?

The modular monolith was deliberately designed to make this migration straightforward. The changes would be **structural, not architectural**:

| Concern | Current (modular monolith) | After extraction (microservice) |
|---|---|---|
| **Event transport** | Spring `ApplicationEventPublisher` (in-process) | Replace with a message broker (Kafka, RabbitMQ); shared event classes move to a published schema |
| **Synchronous API calls** | Direct Java interface call via `CoreModuleApi` | Replace with an HTTP client or gRPC stub; `CoreModuleApi` interface becomes a Feign client |
| **Shared kernel** | `shared.event` package in the same JAR | Publish as a separate `flashcard-events` library or use a schema registry |
| **Transactional guarantees** | `@Transactional` spans a single datasource | Cross-service operations require the Saga pattern or Outbox pattern for reliability |
| **ACL** | `CoreEventTranslator` stays unchanged | `CoreEventTranslator` stays unchanged — it already consumes the event contract, not the internal model |
| **Module boundary enforcement** | Convention + code review | Enforced by the network: modules literally cannot import each other's classes |

The ACL and the public contract (`CoreModuleApi`, `AnalyticsModuleApi`) are the two investments that pay off directly at extraction time. Because the Analytics module was never allowed to reach into Core's internal packages, there is no hidden coupling to untangle. The migration is a matter of swapping the transport mechanism, not rewriting business logic.

---

## Part II — Course Retrospective (Labs 1–5)

### How did the architecture evolve across the labs?

| Lab | Architectural pattern | Key change |
|---|---|---|
| **Lab 1** | Flat service-layer monolith | Single package per concern (`controller`, `service`, `repository`). Business logic lived in `@Service` classes alongside JPA entities. No domain isolation. |
| **Lab 2** | 4-layer DDD architecture | Introduced Domain / Application / Infrastructure / Presentation separation. The domain layer became free of framework dependencies. Rich domain models and value objects replaced anemic entities. Dependency Inversion via repository interfaces. |
| **Lab 3** | Command-Query Separation (CQS) | Replaced monolithic UseCases with dedicated Command and Query handlers. Introduced separate read repositories and read-optimized projections. Controllers were thinned to pure HTTP routing. |
| **Lab 4** | Asynchronous side effects | Introduced an `EventPublisher` abstraction and `@Async` event listeners. Extracted the activity log as an auxiliary component decoupled from the main execution path. Compared synchronous and asynchronous communication trade-offs. |
| **Lab 5** | Modular monolith | Split the system into bounded contexts (`core`, `analytics`) with explicit public contracts, an Anti-Corruption Layer, integration events in a shared kernel, and eventual consistency between modules. |

Each lab addressed the dominant pain point of the previous one. Lab 1's tight coupling made testing hard → Lab 2 introduced layers. Lab 2's monolithic use cases mixed reads and writes → Lab 3 separated them. Lab 3's linear execution made side effects invasive → Lab 4 made them asynchronous. Lab 4's single large context had no module boundaries → Lab 5 enforced them.

---

### Which architectural decisions turned out to be the most valuable?

**1. Rich Domain Model with Value Objects (Lab 2)**

Introducing types like `DeckTitle` and `CardDefinition` instead of raw `String` fields was the single highest-leverage decision of the course. Every subsequent lab benefited: value objects carry their own validation, they are immutable by construction, and they make method signatures self-documenting. When the Labs 3-5 layers were built on top, there was never a question of where string-length validation belongs — it belongs to the value object, and it moved with it.

**2. Repository interfaces in the Domain layer (Lab 2)**

Defining `DeckRepository` and `CardRepository` as domain interfaces and implementing them in Infrastructure made the two most expensive changes of the course (switching to read repositories in Lab 3 and isolating modules in Lab 5) purely additive. No existing code was broken; new adapters were dropped in alongside existing ones.

**3. Event publishing abstraction (Lab 4)**

The `EventPublisher` interface introduced in Lab 4 became the integration backbone for Lab 5. Because command handlers always published through an abstraction, the integration events were already properly named (past tense), properly scoped (carried only the minimum required data), and properly typed. Lab 5 needed only to add new subscribers to the existing event stream — it did not need to modify any Core command handler to wire in analytics.

**4. Shared Kernel for integration events (Lab 5)**

Placing all integration events in `com.flashcard.shared.event` — separate from both `core` and `analytics` — proved essential. It created a neutral, stable contract that both modules depend on without depending on each other. The events are plain Java records with no framework annotations, making them the most portable and longest-lived artifact in the codebase.

---

### What would be done differently, knowing the final result?

**Design events from Lab 2 onward.** The integration events added in Lab 4 turned out to be more fundamental than the lab sequencing implies. If the domain events had been defined as part of the domain model in Lab 2 (alongside the aggregates that produce them), the CQS handlers in Lab 3 and the module contracts in Lab 5 would have had a stable, first-class event vocabulary to build on from the start.

**Make module boundaries explicit earlier.** In Labs 2–4, all code lived under a single root package `com.flashcard`. This was convenient, but it allowed implicit dependencies (e.g., Analytics code accidentally importing a Core domain class) that were only discovered and fixed during Lab 5. Introducing package-level conventions (or at minimum, a ArchUnit rule) in Lab 3 would have eliminated an entire class of late-stage refactoring.

**Introduce a read model contract earlier.** In Lab 3, `DeckResult` and `CardResult` were defined in the Application layer of the single monolith. When Lab 5 split Core and Analytics into separate modules, these read DTOs needed to become part of `CoreModuleApi`. Designing a public-contract layer (`api/`) in Lab 2 or 3 would have made this migration a no-op.

---

### What trade-offs were encountered between simplicity and flexibility?

**Simplicity lost in the short term, gained in the long term**

The transition from Lab 1 (30 files, everything in one service) to Lab 5 (120+ files, three modules, six layers) is a dramatic increase in structural complexity. A developer joining at Lab 1 can understand the full system in under an hour. A developer joining at Lab 5 needs to understand bounded contexts, CQS, event-driven architecture, and ACL before they can trace a single request end-to-end.

However, the flexibility gained at each step was immediately exercised by the next lab's requirements. Lab 3 could not have cleanly introduced read repositories without the domain repository interfaces from Lab 2. Lab 4 could not have safely made side effects asynchronous without the command/query boundary from Lab 3. Lab 5 could not have enforced module isolation without the event abstraction from Lab 4.

**The eventual consistency trade-off**

Choosing eventual consistency between Core and Analytics introduced a new category of operational concern: the analytics projection can be temporarily stale, event handlers can fail silently, and there is no built-in retry or dead-letter mechanism in the current Spring event bus implementation. For a production system, this would require an Outbox pattern and a reliable message broker. The trade-off is: simpler infrastructure now, more complex operations later. For a learning exercise this is entirely appropriate, but it is important to recognize that eventual consistency is not "free" — it shifts complexity from the write path to the operational and observability layers.

**The mapping overhead trade-off**

Every layer boundary introduced a new mapper: `Request → Command`, `Domain Entity → JPA Entity`, `JPA Entity → Read DTO`, `Integration Event → ACL Record`. Each mapper is simple in isolation but represents an ongoing maintenance cost when fields are added or renamed. The benefit is that each layer's data structure can evolve independently, which was demonstrated when the `UserActivitySummary` analytics projection was shaped entirely around analytics queries rather than mirroring the Core domain model. The trade-off is justified, but it must be managed with thorough tests for each mapper to avoid silent data loss at layer boundaries.
