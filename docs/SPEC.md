# 2026 Modern Android Design and Engineering Specification

[English](SPEC.md) | [简体中文](SPEC_ZH_HANS.md)

**Status**: Draft
**Scope**: Modern Android applications in 2026, especially tools, consoles, configuration apps, editors, debugging tools, networking apps, and complex settings-heavy apps.
**Goal**: Provide a unified specification for new projects, legacy modernization, vibe-coding reviews, architecture refactors, design-system consolidation, and quality gates.

Android's current modernization direction clearly emphasizes layered architecture, unidirectional data flow, UI state and state holders, repositories, adaptive layouts, Compose design systems, semantics and testing, edge-to-edge, foreground service constraints, and runtime adaptation for large screens and multi-window environments. Compose also allows teams to extend Material or build a fully custom design system rather than being bound to default styling. ([Android Developers][1])

---

## 1. Normative Language

This specification uses the following terms:

* **MUST**: required
* **MUST NOT**: prohibited
* **SHOULD**: strongly recommended unless there is a sufficient, explicit, and auditable exception
* **SHOULD NOT**: generally should not be used
* **MAY**: optional

---

## 2. Scope Of The Specification

This specification governs the following areas:

* product layering and capability exposure
* information architecture and task-flow organization
* object models and state machines
* UI architecture and Compose design systems
* adaptive UI and multi-window support
* platform-constraint handling
* security, privacy, and debugging boundaries
* performance, accessibility, testing, and release gates

This specification **MUST NOT** be interpreted as a manual for a particular visual style.
This specification **MUST** be interpreted as a coordinated set of product, design, engineering, and quality constraints.

---

## 3. Product Definition

Modern Android apps, especially complex tool-oriented apps, **MUST** be defined as:

> a product system jointly formed by object models, state machines, task flows, a design system, runtime adaptation, and quality gates.

An app **MUST NOT** be defined as merely "a collection of pages".
An app **MUST NOT** use "does it look like some existing platform app" as the primary completion criterion.
An app **SHOULD** be evaluated first against the following top-level dimensions:

* **Understandable**: the current state, causes, risks, and results are clear
* **Operable**: critical task paths are short, accidental-tap cost is low, and feedback is timely
* **Recoverable**: high-impact changes can be previewed, undone, or rolled back
* **Extensible**: object relationships, capability boundaries, and module responsibilities can evolve sustainably
* **Adaptive**: it remains consistently usable across phones, tablets, foldables, desktop windows, and multi-window contexts
* **Verifiable**: states, components, interactions, and performance have automated validation entry points

---

## 4. Product Types In Scope

This specification is especially applicable to the following categories:

* configuration managers
* proxy / VPN / network consoles
* editor / IDE companion tools
* log / monitoring / diagnostic tools
* settings-heavy applications with high state density
* import / export / sync / version-control tools
* productivity tools with multiple objects, strategies, and panes

If a product has any of the following characteristics, it **SHOULD** adopt this specification:

* high state density
* high cost of mistakes
* clear differences in user expertise levels
* complex object relationships
* long-term maintenance needs
* large-screen or multi-window adaptation needs

---

## 5. General Principles

### 5.1 Task First

The top surface of the app **MUST** be organized around user tasks rather than internal implementation terms.
The top-level structure **SHOULD** prioritize:

* current state
* current goal
* currently available actions
* common recovery paths
* current impact scope

### 5.2 State First

Every primary page **MUST** answer "what is happening now?" before "what can I tap?".
Pure stacks of buttons, entries, or settings items **SHOULD NOT** define the main-page organization.

### 5.3 Explanation First

Complex systems **SHOULD** provide a "why" layer.
The system **MUST NOT** rely on users to infer the relationship between configuration, policy, state, and outcomes on their own.

### 5.4 Recovery First

High-impact actions **MUST** provide confirmation, dry run, diff, or recovery mechanisms.
"Change is live immediately and cannot be rolled back" **SHOULD NOT** be the default interaction model.

### 5.5 Adaptive First

The app **MUST** be designed for runtime window space rather than nominal device classes.
Android explicitly recommends designing adaptive UI around window size and runtime-available space, and provides patterns such as `NavigationSuiteScaffold`, `ListDetailPaneScaffold`, and `SupportingPaneScaffold` to support navigation and multi-pane transitions across different window sizes. ([Android Developers][2])

---

## 6. User Capability Tier Model

The app **MUST** adopt a three-tier capability model: **Beginner / Intermediate / Advanced**.
The app **MUST NOT** replace layered exposure with a binary "simple mode / pro mode" switch.
The app **MUST NOT** become three disconnected products.

### 6.1 Beginner Tier (L1)

**Goal**: immediately usable.
**Definition**: users can complete primary tasks without understanding low-level objects, and get clear repair paths when something fails.

The L1 tier **MUST** support:

* importing or creating core objects
* primary tasks such as start / stop / save / apply
* an overview of current state
* display of the currently effective object
* health checks or diagnosis entry points
* failure explanations for non-experts

The L1 tier **MUST NOT** expose by default:

* raw rule trees
* script / plugin / extension configuration
* low-level protocol details
* destructive advanced toggles
* conditional, complex debugging capabilities

### 6.2 Intermediate Tier (L2)

**Goal**: understand reasons and optimize.
**Definition**: users begin to care why the system made a decision, why it failed, and how to optimize or troubleshoot it.

The L2 tier **SHOULD** support:

* explanation chains
* diff summaries
* health reports
* viewing state sources by object
* explanations for common failure stages
* visibility into intermediate policy or intermediate state

### 6.3 Advanced Tier (L3)

**Goal**: precise control and deep debugging.
**Definition**: users need object-level control, version management, raw exports, batch operations, and advanced diagnostics.

The L3 tier **MAY** support:

* dual views for structured editing and text editing
* object trees and dependency relationships
* raw trace
* raw export
* batch operations
* change diff / merge / rollback
* conditional advanced debugging

### 6.4 Layered Exposure Rules

The app **MUST** keep the concept of the same object consistent across all three tiers.
The app **SHOULD** change only information density and action depth, not the definition of objects.
Advanced capabilities **MUST** remain reachable, but **MUST NOT** dominate the default surface.
Low-level capabilities **MUST NOT** be disguised as safe surface actions.

---

## 7. Information Architecture

### 7.1 Top-Level Navigation

Top-level navigation **SHOULD** be organized by tasks and state rather than implementation terms.
Recommended top-level categories:

* dashboard
* primary tasks
* activity / session / logs
* configuration / resources
* toolbox
* settings

Top-level navigation **SHOULD NOT** directly use the following implementation terms as first-class global entries:

* Rule
* Rewrite
* Script
* Engine
* Kernel
* Advanced
* Internal

### 7.2 Page Skeleton

Every primary page **SHOULD** follow this skeleton:

1. state summary
2. primary action
3. secondary actions
4. object list or details
5. risks, diagnostics, or supplementary notes

### 7.3 Navigation And Multi-Pane Layouts

Complex object-centric apps **SHOULD** support a canonical layout:

* small windows: single pane
* medium windows: navigation + list / details
* large windows: list + details + supporting pane

Compose already provides adaptive navigation and multi-pane layout capabilities, and recommends switching navigation forms dynamically as windows change at runtime. ([Android Developers][3])

---

## 8. Adaptive UI

### 8.1 Basic Requirements

The app **MUST** adapt to runtime window size and available space.
The app **MUST NOT** depend on static thresholds such as `screenWidthDp >= 600` as its long-term core strategy.
The app **MUST NOT** rely on orientation lock, disabling resize, or special-casing tablets to avoid adaptation work.
Android explicitly requires apps to handle runtime window changes, orientation changes, split-screen, and desktop-window modes, and rejects outdated restrictions as an adaptation strategy. ([Android Developers][2])

### 8.2 Recommended Implementation

The app **SHOULD**:

* derive top-level navigation from window size classes
* provide list-detail or supporting-pane layouts on medium and large windows
* preserve state continuity across runtime window changes
* elevate window information into UI state rather than reading scattered configuration values

### 8.3 Large Screens And Multi-Window

On large screens and in desktop-window modes, the app **SHOULD** support:

* left-side navigation or an object tree
* central details
* a right-side supporting pane
* viewing multiple contexts simultaneously
* supporting panes that can be independently closed or collapsed

---

## 9. Design System

### 9.1 Direction

The app **SHOULD** follow a **Material-informed, information-first** direction.
That means using Material 3 / Compose themes, base components, adaptive navigation, and interaction primitives as the foundation, while product semantics **MUST** be carried by a custom design system. Compose explicitly documents that Material is recommended but not mandatory, and that teams can extend `MaterialTheme` or build a fully custom theme system. ([Android Developers][4])

### 9.2 Design-System Layers

The app **MUST** define at least:

* theme tokens
* semantic color tokens
* shape tokens
* border and hierarchy tokens
* a typography scale
* state tokens
* motion tokens
* component specifications
* semantics and accessibility contracts

### 9.3 Semantic Colors

The app **SHOULD** provide at least the following semantic color domains:

* `Primary/Brand`
* `Success/Active`
* `Info/Network`
* `Warning/Override`
* `Danger`
* `Neutral`

The constraints are:

* backgrounds **SHOULD** use low saturation
* foregrounds **MUST** stay highly recognizable
* dangerous actions **MUST** carry explicit danger semantics
* the currently effective state **MUST** remain stably visible
* read-only items **MUST NOT** look like primary actions
* the same semantic meaning **MUST** stay consistent across the app

### 9.4 Component Families

The app **SHOULD** standardize the following foundational component families:

* `StatusBadge`
* `SemanticActionButton`
* `HealthBanner`
* `MetricCard`
* `SettingsRow`
* `TimelineRow`
* `DiffViewer`
* `NavigationSurface`
* `EditorCommandBar`
* `TracePanel`

### 9.5 Lists And Settings Pages

Lists and settings pages **MUST** distinguish the following at a glance:

* clickable items
* toggle items
* dangerous items
* read-only items
* current-state items

Lists and settings pages **SHOULD** use soft structural separation, including:

* lightweight dividers
* inset dividers
* container hierarchy
* group titles
* whitespace hierarchy

Relying only on brightness differences in headings and descriptive text to build the entire hierarchy **SHOULD NOT** be considered acceptable design.

---

## 10. Object Model

Complex apps **MUST** define their object model before defining pages.
The object model **MUST NOT** be reverse-engineered from temporary page fields.

### 10.1 Common Objects

Complex tool-oriented apps **SHOULD** model at least the following objects:

* `Profile`: a complete configuration or workspace snapshot
* `Source` / `Subscription`: external sources and sync metadata
* `Policy` / `StrategyGroup`: intermediate decision sets
* `RuleSet` / `Matcher`: rule collections for matching
* `Session` / `ActivityTrace`: one activity, request, or session process
* `HealthReport`: health and anomaly summaries
* `WorkspaceSnapshot`: local drafts, historical versions, and rollback points

### 10.2 Minimum Fields Per Object

Every core object **MUST** include at least:

* a stable ID
* a user-visible name
* a lifecycle state
* the last updated time
* a source or owner
* whether it is editable
* a risk level
* its relationship to the currently effective state

### 10.3 Object Relationships

Object relationships **MUST** be modeled explicitly.
Object relationships **MUST NOT** be hidden inside strings, page order, or temporary mappings.

---

## 11. State And State Machines

### 11.1 Single Source Of Truth

Business truth **MUST** have a single source of truth.
UI **MUST NOT** treat local widget state as business truth directly.
Compose and Android architecture guidance both require unidirectional data flow: the UI reads state and sends events, while business state is held uniformly by a state holder or `ViewModel` when business logic is involved. ([Android Developers][5])

### 11.2 Lifecycle States

Runnable objects **SHOULD** define at least:

* `Idle`
* `Preparing`
* `Active`
* `Degraded`
* `Failed`
* `Stopping`
* `Stopped`

### 11.3 Change States

Editable objects **SHOULD** define at least:

* `Synced`
* `Modified`
* `Conflicted`
* `Applying`
* `Applied`
* `Invalid`
* `Reverted`

### 11.4 Error Model

An error **MUST NOT** be just a string.
Errors **SHOULD** be structured and include at least:

* category
* phase
* impact
* retryability
* suggested action
* raw cause
* user-visible message

---

## 12. Architecture

### 12.1 Layered Architecture

The app **MUST** have at least:

* a UI layer
* a Data layer

Complex apps **SHOULD** additionally provide:

* a Domain layer
* a System Integration layer
* an Observability layer

Android recommends that apps provide at least UI and Data layers, optionally adding a Domain layer based on complexity, while using repositories as data boundaries that centralize business logic and data integration. ([Android Developers][6])

### 12.2 Repository

Repositories **MUST**:

* act as the data-access boundary
* aggregate local and network data sources
* output stable data streams
* encapsulate low-level implementation differences
* remain replaceable and unit-testable

### 12.3 State Holders / ViewModels

Screen state involving business logic **SHOULD** be held by a retainable state holder or a `ViewModel`.
These state holders **SHOULD** survive Activity recreation or restore the most recent stable state consistently. Android explicitly states that business-logic state holders should preserve state across configuration changes and rebuild a coherent state when necessary. ([Android Developers][7])

### 12.4 Offline-First

Whenever the app depends on the network, critical read paths **SHOULD** adopt an offline-first approach.
Repositories **SHOULD** provide both local and network data sources.
At minimum, critical read operations **MUST** continue to work without network access. Android explicitly states that offline-first systems should prioritize local data, keep at least critical reads available offline, and provide local data sources that do not depend on the network. ([Android Developers][8])

---

## 13. Configuration And Change Management

### 13.1 Basic Requirements

Configuration, drafts, policies, and rules **MUST** be treated as structured assets.
Configuration **MUST NOT** be treated as a read-only text blob.

### 13.2 Change Flow

High-impact changes **SHOULD** support the following flow:

1. parse
2. validate
3. dry run
4. diff review
5. apply
6. failure recovery
7. history

### 13.3 Required Capabilities

Complex configuration-oriented apps **SHOULD** provide:

* pre-import health checks
* pre-apply diffs
* post-update diff summaries
* conflict prompts
* stable-version rollback
* hints for invalid items and override relationships

### 13.4 Editors

Editors **SHOULD** support all of the following:

* structured editing
* text editing
* error localization
* change highlighting
* a command bar
* dry run before apply

---

## 14. Explainability

### 14.1 Explanation Chains

Complex systems **SHOULD** provide a standard explanation chain.
The generic template is:

`Input → Match / Decision → Intermediate Policy → Output → Result`

The error template is:

`Phase → Root Cause → Impact → Suggested Action`

### 14.2 Presentation Rules

The surface layer **MUST** show conclusions first.
The intermediate layer **SHOULD** show reasons.
The advanced layer **MAY** show raw traces.
The system **MUST NOT** output only raw low-level text and require users to interpret it themselves.

---

## 15. Observability And Logging

### 15.1 Log Levels

The app **SHOULD** define at least:

* `UserVisible`
* `Operational`
* `Diagnostic`
* `Security`
* `Failure`

### 15.2 Structured Logging

Critical logs **SHOULD** include at least:

* timestamp
* object id
* action
* phase
* status
* correlation id
* configuration version
* error category

### 15.3 User-Visible Logs

User-visible logs **MUST** prioritize expressing:

* what happened
* what was affected
* whether recovery already happened
* what the next recommended step is

User-visible logs **MUST NOT** expose raw low-level technical text as the only user-facing content.

### 15.4 Debug Exports

Debug-export bundles **SHOULD** be standardized and include at least:

* app version
* platform version
* key state snapshots
* the latest failure chain
* redacted logs
* configuration version identifiers

---

## 16. Android Platform Constraints

### 16.1 Edge-To-Edge

Apps targeting SDK 35+ run edge-to-edge by default on Android 15+, and opting out of edge-to-edge enforcement is no longer available when targeting Android 16. The app **MUST** correctly handle system bars, gesture insets, and IME. ([Android Developers][9])

The requirements are:

* Activity initialization **SHOULD** enable edge-to-edge
* lists, bottom bars, FABs, and input areas **MUST** handle insets correctly
* critical actions **MUST NOT** be obscured by system bars
* landscape, multi-window, and IME-visible scenarios **MUST** be validated separately

### 16.2 Predictive Back

The app **SHOULD** support predictive back.
If the app uses custom back behavior, pane closing, editor-exit confirmation, or in-stack animations, it **MUST** use supported modern back APIs. Compose provides `PredictiveBackHandler`, and Android explicitly recommends migrating to predictive-back-compatible implementations as soon as possible. Android 16 also extends predictive back to 3-button navigation. ([Android Developers][10])

### 16.3 Foreground Services

If the app uses foreground services, it **MUST** declare the correct service type for every FGS and satisfy the related manifest and runtime-permission requirements. Android 14+ requires appropriate foreground service types. ([Android Developers][11])

### 16.4 Local Network Permission

If the app needs local network discovery, LAN debugging, LAN connections, or device interconnect features, it **MUST** reserve a local-network-permission path and a fallback strategy. Android 17 introduces the `ACCESS_LOCAL_NETWORK` runtime permission and blocks local-network access by default for apps targeting API 37+; Android 16 is the transition phase. ([Android Developers][12])

### 16.5 VPN And Per-App Scenarios

If the app involves VPN, network takeover, or per-app routing, it **SHOULD** consider the following from the start of design:

* user authorization
* always-on
* per-app VPN
* reconnect recovery
* notification visibility
* lifecycle consistency

Android's VPN documentation explicitly treats custom VPN, always-on, and per-app VPN as standard capability domains. ([Android Developers][13])

---

## 17. Security And Privacy

### 17.1 Environment Separation

The app **MUST** distinguish between:

* debug environments
* test environments
* production environments

Debug trust policies, weakened validation, development certificates, or special network configuration **MUST NOT** enter production builds.

### 17.2 Network Security Configuration

If the app uses Network Security Configuration, debug-only trust policies **SHOULD** be declared in a debug-only manner. Android explicitly supports using Network Security Configuration to control trust anchors, cleartext policy, debug overrides, and ECH behavior starting in Android 17. ([Android Developers][14])

### 17.3 Log And Export Minimization

Debug data collection **MUST** be minimized.
Sensitive exports **MUST**:

* require confirmation before export
* present risk warnings
* support redaction
* be deletable
* clearly state the save location

### 17.4 Capability-Statement Boundaries

The app **MUST NOT** make absolute security or debugging claims such as:

* all connections can be decrypted
* all apps can be traffic-inspected
* all HTTPS requests can be analyzed at content level

If a capability is limited by the platform, certificate trust, pinning, ECH, or target-app policy, the app **MUST** state those boundaries clearly. Since Android 7.0, apps targeting API 24+ no longer trust user-installed CAs by default; Network Security Configuration can further customize trust anchors; and Android 17 introduces opportunistic ECH for supported TLS clients and servers, reducing the visibility of some handshake information. ([Android Developers][14])

---

## 18. Performance

### 18.1 Critical Paths

The following paths **MUST** be treated as critical performance paths:

* cold start
* first-screen state establishment
* list scrolling
* switching object details
* search and filtering
* pane expansion / collapse
* editor input
* saving / applying configuration
* loading large log datasets

### 18.2 Compose Performance Constraints

Compose UI **SHOULD**:

* avoid meaningless recomposition
* avoid expensive computation on the composition path
* maintain stable parameters and state
* move business logic out of composable bodies
* use appropriate `remember`, derived state, lazy loading, and state hoisting

### 18.3 Baseline Profile

The app **SHOULD** provide an app-specific Baseline Profile, generated and validated using critical user paths. Android explicitly states that Baseline Profiles can improve code execution speed by around 30 percent from first launch, and Compose also recommends supplementing the default profile with an app-specific one. ([Android Developers][15])

### 18.4 Performance Acceptance

Every release **SHOULD** validate at least:

* cold-start time
* time to usable first screen
* scrolling stability for lists and logs
* latency and jank in critical interactions
* performance regressions after reconfiguration
* long-run memory growth trends

---

## 19. Accessibility And Semantics

### 19.1 Production Requirement

Accessibility and semantics **MUST** be treated as production requirements.
Compose semantics serve accessibility, autofill, and testing at the same time. ([Android Developers][16])

### 19.2 Component Semantics

All custom components **MUST** define:

* role
* label
* state description
* selected / checked / enabled
* clickability
* danger / warning state
* value / progress / range, when applicable

### 19.3 State Expression

The app **MUST NOT** rely only on color to express state.
State **SHOULD** be expressed using at least two of the following signal types:

* color
* text
* icon
* border / container
* positional / structural difference

### 19.4 Complex Container Semantics

Complex list items, card containers, command bars, and status bars **SHOULD** use semantics merging or reset strategies when appropriate. Compose provides capabilities such as `mergeDescendants` and `clearAndSetSemantics` to control the semantics tree. ([Android Developers][17])

---

## 20. Testing

### 20.1 Testing Layers

The app **MUST** establish a layered testing system that includes at least:

* unit tests
* repository / data-layer tests
* state-machine tests
* Compose UI tests
* critical-path integration tests

### 20.2 State-Machine Tests

State transitions of critical objects **MUST** be testable.
At minimum, tests **MUST** cover:

* normal paths
* failure paths
* interruption paths
* retry paths
* conflict paths
* recovery paths

### 20.3 Compose UI Tests

Compose UI tests **MUST** be based on stable semantic nodes.
If a custom component cannot be reliably selected, asserted, and interacted with, it **MUST** be treated as not meeting the testability requirement. Android explicitly states that Compose tests operate on the semantics tree. ([Android Developers][18])

### 20.4 Minimum Required Test Paths

The following paths **MUST** be included in minimum coverage:

1. first launch
2. primary-task success path
3. primary-task failure path
4. permission denial and fallback
5. configuration edits and undo
6. adaptive-layout switching
7. edge-to-edge / IME / system-bars scenarios
8. back navigation and pane closing
9. confirmation for high-risk actions
10. export and clear

---

## 21. Supplemental Rules For Network Control / VPN / Proxy Apps

> This chapter is a professional supplement. Its object-modeling and capability-tiering approach also applies to other complex tool-oriented applications.

### 21.1 Core Objects

Apps in this category **SHOULD** model at least:

* `Profile`
* `Subscription`
* `PolicyGroup`
* `RuleSet`
* `SessionTrace`
* `HealthReport`
* `WorkspaceSnapshot`

### 21.2 Three-Tier Capability Mapping

#### Beginner Tier

The app **MUST** provide:

* configuration or subscription import
* start / stop
* current connection state
* current effective configuration
* current policy
* one-tap testing
* common failure-repair entry points

#### Intermediate Tier

The app **SHOULD** provide:

* match-chain summaries
* current outbound path
* DNS results
* update diffs
* health checks
* readable error explanations

#### Advanced Tier

The app **MAY** provide:

* rule-object editing
* policy-group editing
* raw traces
* raw traffic export
* batch operations
* version rollback
* conditional content-level debugging

### 21.3 Explanation-Chain Templates

Recommended explanation chain:

`App → DNS → RuleSet → Rule → PolicyGroup → Outbound → Result`

Recommended failure chain:

`FailedStage → RootCause → Impact → SuggestedAction`

### 21.4 Capture And MITM Boundaries

If the app offers packet capture or decryption-oriented debugging, its copy **MUST** clearly distinguish between:

1. **session-level observation**
2. **raw traffic observation**
3. **content-level debugging**

The app **MUST NOT** describe the third category as universally available capability.
Android's `VpnService` allows an app to establish a custom VPN and process traffic forwarded through the VPN interface, so session-level and raw-traffic observation can be legitimate product capabilities. However, whether content-level TLS debugging is possible depends on the target app's trust chain, pinning, platform privacy hardening, and actual library support, and is not guaranteed globally by the platform. ([Android Developers][13])

---

## 22. Legacy Project Remediation

### 22.1 Applicable Scenarios

This chapter applies to:

* projects with accumulated features but no coherent structure
* projects that run but have chaotic state behavior
* projects iterated rapidly with vibe coding but without system-level constraints
* existing codebases that need modernization refactors

### 22.2 High-Risk Determination

If any of the following is true, the project **SHOULD** be treated as high risk:

* pages directly read or write global singletons
* raw configuration text drives the UI directly
* business state is scattered across composable local state
* there is no `ViewModel` / state-holder boundary
* the repository is only a pass-through layer
* semantic colors are hardcoded in pages
* static width thresholds are used as a long-term adaptive strategy
* insets / IME / edge-to-edge are not handled
* custom components lack semantics
* there are no Compose UI tests
* there is no confirmation or rollback for critical changes
* there is no performance baseline

### 22.3 Remediation Priorities

#### P0: Stability And Compliance

The following **MUST** be fixed first:

* crashes
* state corruption
* broken critical paths
* non-compliant platform permissions / foreground services
* edge-to-edge obstructions
* dangerous actions without confirmation

#### P1: Structural Reconstruction

The following **MUST** be completed:

* core object modeling
* UI state modeling
* repository boundaries
* cleanup of `ViewModel` / state-holder boundaries
* initial extraction of design tokens

#### P2: Product Capability Reconstruction

The following **SHOULD** be completed:

* explanation chains
* diff views
* rollback mechanisms
* adaptive-layout refactors
* missing component semantics
* missing automated tests

#### P3: Professional Capability Enhancement

The following **MAY** be completed:

* deep debugging
* batch tooling capabilities
* multi-pane console workflows
* automated health checks and recovery
* raw exports and advanced filtering

---

## 23. Review Checklist

### 23.1 Product Review

* Is first-level navigation organized by tasks rather than internal terms?
* Are the beginner / intermediate / advanced tiers clear?
* Is the current state visible at a glance?
* Are errors understandable?
* Are high-risk capabilities properly isolated?

### 23.2 Information-Architecture Review

* Does the page follow the order of summary → actions → details?
* Are objects traceable?
* Is there an explanation chain?
* Is there a diff or change-preview capability?

### 23.3 Architecture Review

* Is unidirectional data flow in place?
* Is there a single source of state truth?
* Is there a clear state-holder / `ViewModel` boundary?
* Does the repository serve as a real boundary?
* Are local, network, and system integration concerns layered clearly?

### 23.4 Android Platform Review

* Does the app support adaptive UI?
* Are insets and edge-to-edge handled correctly?
* Has predictive back been considered?
* Are the correct FGS types declared?
* Is there a permission-fallback path?
* Have local-network access changes been considered?

### 23.5 Quality Review

* Do custom components provide semantics?
* Are UI tests and state-machine tests present?
* Is there a performance baseline?
* Are log redaction and debug-export rules defined?

---

## 24. Minimum Acceptable Standard

A modern, complex Android app that complies with this specification **MUST** provide at least:

1. a clear object model
2. a unified UI state
3. a state-holder / `ViewModel` boundary
4. repository boundaries
5. an adaptive-layout strategy
6. edge-to-edge and inset handling
7. a semantic design-system foundation
8. beginner / intermediate / advanced capability exposure
9. confirmation for critical changes
10. at least minimal recovery or rollback capability
11. an entry point for Compose UI tests
12. a critical-path performance baseline

Projects that do not satisfy the items above **SHOULD NOT** be considered modernized.

---

## 25. Recommended Implementation Order

### 25.1 New Projects

New projects **SHOULD** implement the following in order:

1. define the object model
2. define the state machine and UI state
3. define top-level information architecture
4. define the adaptive-layout strategy
5. define design tokens and component families
6. establish repository and state boundaries
7. implement the main task path
8. implement explanation chains and recovery mechanisms
9. handle platform constraints
10. add testing, semantics, and performance baselines

### 25.2 Existing Projects

Existing projects **SHOULD** remediate in the following order:

1. P0 stability and compliance
2. P1 structural reconstruction
3. P2 product capability reconstruction
4. P3 professional capability enhancement

---

## 26. Repository-Level Spec Statement Template

```md
# Project Spec Statement

This project follows the 2026 Modern Android Design and Engineering Specification.

## Core Rules

- The app uses layered architecture, unidirectional data flow, UI state, state holders, and repository boundaries.
- The UI uses an adaptive-first, edge-to-edge, information-first Compose design system.
- Product capabilities are exposed in three tiers: beginner, intermediate, advanced.
- Core objects are structurally modeled; high-impact changes require confirmation, diff, preview, or recovery.
- Custom components must provide semantics, accessibility support, and stable test hooks.
- Android platform constraints—including permissions, foreground services, window changes, local-network access, and system back behavior—are treated as product constraints, not post-release patches.

## Merge Gates

A feature MUST NOT be considered complete when any of the following is missing:

1. Stable object model
2. Single source of UI truth
3. Adaptive-layout acceptance
4. Edge-to-edge acceptance
5. Critical-path tests
6. High-risk action protection
7. Component semantics
8. Minimum performance baseline
```

---

## 27. Conclusion

The core conclusions of this specification are:

* A modern Android app is **not** a collection of pages. It is a product system.
* Modernizing a tool-oriented app is **not** the same as making it look generically Material. It means prioritizing information, state, recovery, and adaptation.
* Beginner users need something directly usable; intermediate users need to understand why; advanced users need full control.
* Android platform constraints such as window changes, permission changes, foreground services, edge-to-edge, predictive back, and local-network access **MUST** be treated as first-class product constraints.
* Projects without an object model, state architecture, adaptive layouts, semantics, testing, and a performance baseline **SHOULD NOT** be considered fully modernized.

---

[1]: https://developer.android.com/topic/architecture/recommendations "Recommendations for Android architecture"
[2]: https://developer.android.com/develop/ui/compose/layouts/adaptive "About adaptive layouts | Jetpack Compose"
[3]: https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation "Build adaptive navigation | Jetpack Compose"
[4]: https://developer.android.com/develop/ui/compose/designsystems/custom "Custom design systems in Compose"
[5]: https://developer.android.com/develop/ui/compose/architecture "Compose UI Architecture"
[6]: https://developer.android.com/topic/architecture "Guide to app architecture"
[7]: https://developer.android.com/topic/architecture/ui-layer/stateholders "State holders and UI state"
[8]: https://developer.android.com/topic/architecture/data-layer/offline-first "Build an offline-first app"
[9]: https://developer.android.com/develop/ui/views/layout/edge-to-edge "Display content edge-to-edge in views"
[10]: https://developer.android.com/guide/navigation/custom-back/predictive-back-gesture "Add support for the predictive back gesture"
[11]: https://developer.android.com/develop/background-work/services/fgs/service-types "Foreground service types"
[12]: https://developer.android.com/privacy-and-security/local-network-permission "Local network permission"
[13]: https://developer.android.com/develop/connectivity/vpn "VPN | Connectivity"
[14]: https://developer.android.com/privacy-and-security/security-config "Network security configuration"
[15]: https://developer.android.com/develop/ui/compose/performance/baseline-profiles "Use a baseline profile | Jetpack Compose"
[16]: https://developer.android.com/develop/ui/compose/accessibility/semantics "Semantics | Jetpack Compose"
[17]: https://developer.android.com/develop/ui/compose/accessibility/merging-clearing "Merging and clearing | Jetpack Compose"
[18]: https://developer.android.com/develop/ui/compose/testing "Test your Compose layout"
