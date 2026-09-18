# Problem Statement & Project Scope

## 1. Project Overview & Academic Context

* **Project Title**: Inventory & Sales Management System
* **Course**: Programming in Java (Vityarthi Academic Curriculum)
* **Author**: Gaurav (NotSoGaurav)
* **Language & Runtime**: Core Java (Java SE 17+)
* **Architecture**: Layered Modular Architecture with Flat-File Persistence
* **Database / External Frameworks**: **None** (Strictly zero SQL, MySQL, JDBC, Hibernate, Spring, or external database engines)

---

## 2. Problem Statement

Small to medium retail establishments encounter severe operational friction when tracking inventory levels, monitoring sales transactions, preventing stock-outs, and maintaining customer and supplier relationships. The standard methods currently in use exhibit notable limitations:

1. **Manual Registers & Ledgers**: Error-prone, susceptible to loss, illegibility, and arithmetic errors; unable to compute real-time stock valuation or notify operators of low stock.
2. **Spreadsheet Files**: Lack referential integrity, automated stock adjustments, concurrent data validation, and role-based security.
3. **Enterprise Databases (RDBMS / ERP)**: Solutions like MySQL, PostgreSQL, Oracle, or SAP impose heavy resource footprints, network dependencies, complex configuration, administrative overhead, and ongoing licensing fees.

### The Objective
To design, engineer, and validate a completely standalone, lightweight, robust, and platform-independent **Core Java console application** that implements end-to-end inventory management and sales billing through **Java File Handling** (`.txt` files) while demonstrating rigorous Object-Oriented Design (OOP) principles.

---

## 3. Project Scope & Capabilities

### Core Modules
1. **Authentication & Authorization**:
   - Secure login mechanism with role-based segregation (`ADMIN` vs. `CASHIER`).
   - Audit logging for successful and failed access attempts.
2. **Product & Catalog Management**:
   - Comprehensive CRUD operations with price, category, and minimum stock threshold tracking.
   - Case-insensitive search across name, category, and ID.
3. **Inventory & Stock Control**:
   - Automated status determination (`IN_STOCK`, `LOW_STOCK`, `OUT_OF_STOCK`).
   - Restocking operations and manual stock adjustments.
   - Proactive low-stock warnings when inventory falls below thresholds.
4. **Customer & Supplier Master Data**:
   - Master records for customers and suppliers with regex-validated phone numbers and emails.
   - Traceability linking products to registered suppliers.
5. **Point-of-Sale (POS) & Billing Engine**:
   - Interactive multi-item cart calculation (subtotals and grand totals).
   - Atomic pre-flight inventory availability check before committing sales.
   - Automatic deduction of stock quantities from `products.txt`.
   - Formatted invoice receipt generation.
   - Multiple payment methods (`CASH`, `CARD`, `UPI`, `NET_BANKING`).
6. **Operational Analytics & Business Intelligence**:
   - Total accumulated revenue, transaction counts, and average order values.
   - Comprehensive inventory valuation and total units held.
   - Top-selling products ranked by sales count and revenue.

---

## 4. Key Constraints & Design Principles

* **Self-Contained Execution**: No database setup or internet connection required. All files persist locally in `data/`.
* **Data Integrity**: Input sanitization prevents delimiter injection (`|`, `;`), and defensive copying protects domain collections.
* **Custom Exception Architecture**: Domain exceptions (`ValidationException`, `ProductNotFoundException`, `InsufficientStockException`) ensure explicit, recoverable error handling.
* **Automated Testing**: Dedicated 34-point automated test suite running without external testing frameworks.
