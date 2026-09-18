# Inventory & Sales Management System

A robust, modular, and fully standalone **Core Java console-based enterprise application** for managing product catalog, inventory levels, customer master data, and sales billing with **flat-file persistence** (`.txt` files).

Developed for the **Programming in Java** academic curriculum (Vityarthi).

---

## Key Highlights

- **Pure Core Java**: Built entirely using Java SE standard library (`java.base`). Zero external dependencies, zero Maven third-party plugins, and zero build tool overhead.
- **No Database Required**: No MySQL, PostgreSQL, SQLite, JDBC, Hibernate, or Spring. Persistent state is safely stored in human-readable `.txt` files in the `data/` directory.
- **Layered Architecture**: Strict separation of concerns across Model, Repository, Service, and Console UI layers.
- **Role-Based Access Control (RBAC)**: Distinct permissions for `ADMIN` (full catalog, stock, suppliers, reports) and `CASHIER` (sales processing, customer lookup, stock checking).
- **Atomic Stock Checkout**: Pre-flight inventory verification prevents overselling, with automatic atomic quantity deduction upon sale completion.
- **Automated Test Suite**: 34 automated unit and integration tests executing natively without third-party test runners.

---

## System Architecture

```text
               +-----------------------------+
               |      Console UI Layer       |
               | (LoginUI, MainMenuUI, ...)  |
               +-----------------------------+
                              |
                              v
               +-----------------------------+
               |        Service Layer        |
               | (Auth, Product, Sales, ...) |
               +-----------------------------+
                              |
                              v
               +-----------------------------+
               |      Repository Layer       |
               |  (CrudRepository<T, ID>)    |
               +-----------------------------+
                              |
                              v
               +-----------------------------+
               |        FileUtil (I/O)       |
               +-----------------------------+
                              |
                              v
               +-----------------------------+
               |    Local Data Files (.txt)  |
               | (users, products, sales, ...) |
               +-----------------------------+
```

---

## Data Storage & Formats

All persistent files are located in the `data/` directory:

| File | Entity | Format & Sample |
|---|---|---|
| `users.txt` | `User` | `userId\|username\|password\|role`<br>`U1\|admin\|admin123\|ADMIN` |
| `products.txt` | `Product` | `productId\|name\|category\|price\|quantity\|minThreshold\|supplierId`<br>`P1\|Mechanical Keyboard\|Electronics\|2500.0\|15\|5\|S1` |
| `customers.txt` | `Customer` | `customerId\|name\|phone\|email\|address`<br>`C1\|Aarav Sharma\|9876543210\|aarav@example.com\|Bhopal` |
| `suppliers.txt` | `Supplier` | `supplierId\|name\|contactPerson\|phone\|email\|address`<br>`S1\|Tech Distributors\|Rajesh Gupta\|9812345678\|rajesh@techdist.com\|Delhi` |
| `sales.txt` | `Sale` | `saleId\|customerId\|timestamp\|items\|totalAmount\|paymentMethod`<br>`SALE-001\|C1\|2026-09-18T17:00:00\|P1:Keyboard:2:2500.0;P2:Mouse:1:800.0\|5800.0\|UPI` |
| `app.log` | Audit Trail | `[Timestamp] [LEVEL] [TAG] Message` |

---

## Directory Structure

```text
Inventory-Sales-Management-System/
├── data/                               # Persistent text storage
│   ├── users.txt
│   ├── products.txt
│   ├── customers.txt
│   ├── suppliers.txt
│   └── sales.txt
├── docs/                               # Academic documentation
│   ├── architecture.md
│   ├── problem-statement.md
│   ├── objectives.md
│   ├── functional-requirements.md
│   ├── non-functional-requirements.md
│   ├── workflow.md
│   ├── use-case.md
│   ├── class-diagram.md
│   └── sequence-diagram.md
├── src/
│   ├── main/java/com/inventory/
│   │   ├── Main.java                   # Application Entry Point
│   │   ├── model/                      # Domain Entities & Enums
│   │   ├── repository/                 # CRUD File Repositories
│   │   ├── service/                    # Business Logic Layer
│   │   ├── ui/                         # Console Menus & Forms
│   │   ├── util/                       # FileUtil, LoggerUtil
│   │   ├── validation/                 # InputValidator
│   │   └── exception/                  # Custom Domain Exceptions
│   └── test/java/com/inventory/
│       ├── TestSuiteRunner.java        # Master Test Suite Runner
│       ├── model/                      # Model Unit Tests
│       ├── repository/                 # Repository Unit Tests
│       ├── service/                    # Service Workflow Tests
│       ├── util/                       # File I/O Tests
│       └── validation/                 # Input Validation Tests
├── README.md
├── statement.md
└── .gitignore
```

---

## Getting Started

### Prerequisites
* **Java Development Kit (JDK)**: JDK 17 or higher (tested on Temurin JDK 25).
* Any terminal (PowerShell, Command Prompt, Bash, Zsh).

### Compilation

#### Windows (PowerShell)
```powershell
$files = (Get-ChildItem -Path "src/main/java" -Filter "*.java" -Recurse).FullName
javac -d bin $files
```

#### Windows (Command Prompt)
```cmd
dir /s /b src\main\java\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt
```

#### Linux / macOS
```bash
find src/main/java -name "*.java" > sources.txt
javac -d bin @sources.txt
rm sources.txt
```

---

## Running the Application

Once compiled into `bin/`, run the application:

```bash
java -cp bin com.inventory.Main
```

### Default Credentials
On initial startup, if `data/users.txt` is missing or empty, the system automatically initializes default administrator credentials:

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `cashier1` | `cashier123` | `CASHIER` |

*(You can add and manage additional users directly through the system or data files.)*

---

## Running the Automated Test Suite

To compile and run the comprehensive 34-point automated test suite:

#### Windows (PowerShell)
```powershell
$files = (Get-ChildItem -Path "src/main/java", "src/test/java" -Filter "*.java" -Recurse).FullName
javac -d bin $files
java -cp bin com.inventory.TestSuiteRunner
```

#### Sample Test Output
```text
==========================================================
    INVENTORY & SALES MANAGEMENT SYSTEM - TEST SUITE      
==========================================================
--- Running Model Tests ---
  [PASS] User entity fields and role
  [PASS] Product stock status IN_STOCK
  [PASS] Product stock status LOW_STOCK
  [PASS] Product stock status OUT_OF_STOCK
  [PASS] SaleItem subtotal (3 * 100.0 = 300.0)
  [PASS] Sale total amount calculation (300 + 300 = 600.0)
  [PASS] Sale dynamic recalculation upon item removal (total = 300.0)

--- Running Validation & Exception Tests ---
  [PASS] Clean required text validated
  [PASS] Delimiter injection prevented
  [PASS] Positive integer parsed
  ...
==========================================================
                   TEST SUITE SUMMARY                     
==========================================================
 Total Test Cases Executed  : 34
 Total Test Cases Passed    : 34
 Total Test Cases Failed    : 0
 Status                     : ALL TESTS PASSED (100%)
==========================================================
```

---

## Academic Documentation References

Detailed project analysis and architectural blueprints are available in the [`docs/`](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs) folder:
* [Architecture & Storage Specification](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/architecture.md)
* [Problem Statement](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/problem-statement.md)
* [Project Objectives](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/objectives.md)
* [Functional Requirements](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/functional-requirements.md)
* [Non-Functional Requirements](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/non-functional-requirements.md)
* [System Workflow & Diagrams](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/workflow.md)
* [Use Case Specifications](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/use-case.md)
* [Class Diagram & OOP Design](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/class-diagram.md)
* [Sequence Diagrams](file:///c:/Github/Vityarthi/Inventory%20and%20sales%20management%20%20system/Inventory-Sales-Management-System/docs/sequence-diagram.md)
