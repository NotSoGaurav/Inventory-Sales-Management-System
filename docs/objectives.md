# Project Objectives

## 1. General Objective

The primary objective of this project is to design, implement, and validate a robust, modular, and completely standalone **Inventory & Sales Management System** in **Core Java** that utilizes **flat-file persistence** (`.txt` files) instead of external database engines, demonstrating mastery of Core Object-Oriented Programming and the Java standard library.

---

## 2. Specific Objectives

### A. Architectural & Design Objectives
* **Layered Architecture**: Establish clear separation of concerns across Model, Repository, Service, and Console UI layers.
* **Pure Core Java**: Exclude external third-party dependencies, databases (MySQL, SQLite), and frameworks (Spring, Hibernate, JDBC) to ensure complete portability and self-contained execution.
* **Clean OOP Principles**: Demonstrate encapsulation, inheritance, polymorphism, abstraction, interface programming, and strong type safety using Java Enums.

### B. Functional Objectives
* **Persistent File Storage**: Implement a reliable Repository layer backed by Java I/O (`java.nio.file`) that serializes and deserializes structured pipe-delimited (`|`) and composite (`:`, `;`) records.
* **Role-Based Authentication**: Secure the application with user login mechanisms, distinguishing between `ADMIN` (system configuration, product CRUD, supplier management, full reports) and `CASHIER` (sales processing, customer lookup, stock inquiry).
* **Inventory Management**: Maintain accurate product records (ID, name, category, price, quantity, threshold, supplier) with dynamic stock status computation (`IN_STOCK`, `LOW_STOCK`, `OUT_OF_STOCK`).
* **Customer & Supplier Tracking**: Manage master data for customers and suppliers with phone, email, and address validation.
* **Sales & Billing Processing**: Enable multi-item point-of-sale checkout, computing subtotal, discount, tax, and final amount, with automatic atomic inventory deduction and invoice generation.
* **Reports & Analytics**: Provide real-time operational summaries including low-stock alerts, sales revenue analytics, category breakdowns, and top-selling product summaries.

### C. Quality & Robustness Objectives
* **Data Validation & Sanitization**: Guard against delimiter injection, negative values, malformed emails, and invalid phone numbers using centralized validators.
* **Custom Exception Architecture**: Implement clear, meaningful domain-specific exceptions (`ValidationException`, `ProductNotFoundException`, `InsufficientStockException`).
* **Audit Logging**: Maintain operational event tracking in `data/app.log` recording security events, transaction completions, and system errors with timestamps.
* **Automated Test Suite**: Validate system correctness with unit and integration tests covering models, repositories, validators, and service workflows.
