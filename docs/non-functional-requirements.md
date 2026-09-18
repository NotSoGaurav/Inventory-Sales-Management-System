# Non-Functional Requirements

This document specifies the non-functional requirements (NFRs) governing quality attributes, constraints, performance, and security of the **Inventory & Sales Management System**.

---

## 1. Performance Requirements

* **NFR-PERF-01: Low Latency Response**: Console menu operations, queries, and file updates shall complete in under 200 milliseconds under typical academic dataset sizes (< 10,000 records).
* **NFR-PERF-02: Efficient Memory Utilization**: Use lightweight Java standard Collections (`ArrayList`, `HashMap`, `Optional`) and stream processing for filtering and aggregations without unnecessary object duplication.
* **NFR-PERF-03: Quick Startup Time**: The application shall bootstrap, verify/create data directories, and present the login screen within 1 second.

---

## 2. Reliability and Data Integrity

* **NFR-REL-01: Atomic Persistence**: File writing operations must write complete, consistent records. In the event of a validation or stock failure during checkout, no partial sales or partial inventory deductions shall be persisted.
* **NFR-REL-02: Delimiter Injection Protection**: All string inputs from users (e.g. names, notes, passwords) shall be validated to forbid internal file delimiters (`|` and `;`) to preserve file serialization structure.
* **NFR-REL-03: Defensive Copying**: Entity collections (such as items inside a `Sale`) shall be encapsulated with defensive copying to guard against unintended mutation.
* **NFR-REL-04: Graceful Degradation**: Missing data files or directories shall be automatically created on startup with informative warning messages rather than unhandled crashes.

---

## 3. Maintainability and Architecture

* **NFR-MAINT-01: Layered Decoupling**: Strict separation between UI, Service, Repository, and Utility layers must be maintained. UI classes shall never directly execute file I/O.
* **NFR-MAINT-02: Clean OOP Principles**: The codebase shall adhere to SOLID design principles, utilizing interfaces (`CrudRepository<T, ID>`), abstraction, encapsulation, and type-safe enums.
* **NFR-MAINT-03: Self-Documenting Code**: Code shall use clear descriptive naming conventions and standard Javadoc comments on public classes and service methods.

---

## 4. Portability and Independence

* **NFR-PORT-01: Zero External Dependencies**: The application shall depend exclusively on the standard Java SE runtime (`java.*`). No external libraries, databases, JDBC connectors, or frameworks shall be required.
* **NFR-PORT-02: Cross-Platform Execution**: File paths and line separators shall utilize platform-independent abstractions (`File.separator`, `System.lineSeparator()`, `java.nio.file.Path`) to run seamlessly across Windows, Linux, and macOS.

---

## 5. Usability and User Experience

* **NFR-USA-01: Clear Visual Hierarchy**: Console outputs shall utilize structured ASCII headers, dividers, formatted alignment columns, and standardized currency formatting (`Rs. 0.00`).
* **NFR-USA-02: User Guidance and Error Feedback**: Invalid user inputs (e.g., entering alphabets for price, negative numbers, non-existent IDs) shall prompt descriptive, friendly error notices without terminating the application.
* **NFR-USA-03: Non-Destructive Navigation**: Every sub-menu shall provide a clear option to exit back to the previous menu or cancel the ongoing action.

---

## 6. Security and Access Control

* **NFR-SEC-01: Role-Based Access Control (RBAC)**: Only users authenticated with the `ADMIN` role shall access administrative functions (supplier management, inventory configuration, raw logs, full analytical reports).
* **NFR-SEC-02: Audit Trail**: Critical business events (sales, logins, failed authentications) shall be appended to `data/app.log` with timestamps for accountability.
