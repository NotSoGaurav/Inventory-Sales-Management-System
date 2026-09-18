# System Architecture & Data Storage Format

## 1. Overview and Rationale

The **Inventory & Sales Management System** is structured as a modular, layered Core Java console application. 

### Why File Handling is Used
Instead of requiring complex relational database setup (such as MySQL or external database servers) and additional drivers (JDBC), this project uses **Java File Handling** for persistence:
* **Self-contained & Portable**: Runs on any machine with Java installed without needing database installation, credentials, or network configuration.
* **Academic Focus**: Demonstrates core OOP concepts, Java I/O (`java.nio.file`), exception handling, string manipulation, and collections without external dependencies.
* **Human-readable**: Data is stored in plain text files that can be easily inspected, debugged, and backed up.

---

## 2. Layered Architecture

The application is structured into four distinct layers:

```text
               Console UI Layer
                      ↓
                Service Layer
                      ↓
               Repository Layer
                      ↓
            File Utility (FileUtil)
                      ↓
           Persistent File Storage (.txt)
```

1. **Console UI Layer (`com.inventory.ui`)**: Handles console menu displays, user prompts, and basic input reading.
2. **Service Layer (`com.inventory.service`)**: Encapsulates business logic, calculations, inventory adjustments, and validation orchestration.
3. **Repository Layer (`com.inventory.repository`)**: Translates domain model objects to/from delimiter-separated text representations.
4. **File Utility (`com.inventory.util.FileUtil`)**: Handles physical file I/O operations (file creation, safe reading, line appending, truncation/overwriting).
5. **Data Files (`data/*.txt`)**: Persistent local storage on disk.

---

## 3. Data Storage Location

All persistent text files are stored in the root-level `data/` directory:

```text
data/
├── users.txt
├── products.txt
├── suppliers.txt
├── customers.txt
└── sales.txt
```

---

## 4. Text Serialization Format and Delimiters

A consistent delimiter format is used across all files to separate attributes.

### Delimiters:
* **Field Separator**: `|` (Pipe character) — Used between entity fields. Avoids conflict with names or addresses containing commas.
* **Item Separator**: `;` (Semicolon) — Used in `sales.txt` to separate multiple line items (`SaleItem`s) belonging to one sale.
* **Sub-Item Separator**: `~` (Tilde) — Used in `sales.txt` to separate individual fields inside each `SaleItem` (`productId~productName~quantity~unitPrice`).

---

## 5. File Specifications & Examples

### 5.1 `users.txt`
Stores system user accounts for authentication and role-based access.

* **Format**:
  ```text
  userId|username|password|fullName|role
  ```
* **Example**:
  ```text
  1|admin|admin123|System Administrator|ADMIN
  2|cashier1|pass123|John Doe|CASHIER
  ```

---

### 5.2 `products.txt`
Stores product inventory records.

* **Format**:
  ```text
  productId|name|category|price|quantity|minStockThreshold|supplierId
  ```
* **Example**:
  ```text
  101|Mechanical Keyboard|Electronics|1200.0|15|5|S1
  102|Wireless Mouse|Electronics|500.0|25|5|S1
  ```

---

### 5.3 `suppliers.txt`
Stores supplier contact and location details.

* **Format**:
  ```text
  supplierId|name|contactNumber|email|address
  ```
* **Example**:
  ```text
  S1|Tech Distributor Corp|9876543210|contact@techdist.com|Bhopal, MP
  ```

---

### 5.4 `customers.txt`
Stores customer profile records.

* **Format**:
  ```text
  customerId|name|phone|email|address
  ```
* **Example**:
  ```text
  C1|Rahul Sharma|9876543210|rahul.sharma@example.com|Arera Colony, Bhopal
  ```

---

### 5.5 `sales.txt`
Stores completed sales transactions. Each sale line contains the sale header details plus all associated items formatted as a composite list.

* **Format**:
  ```text
  saleId|customerId|saleDate|paymentMethod|totalAmount|items
  ```
* **Items Sub-format**:
  Each item is encoded as:
  ```text
  productId~productName~quantity~unitPrice
  ```
  Multiple items are joined by `;`.

* **Example**:
  ```text
  SALE-001|C1|2026-09-18T16:30:00|UPI|2900.0|101~Mechanical Keyboard~2~1200.0;102~Wireless Mouse~1~500.0
  ```

---

## 6. How Repositories Use `FileUtil`

Repositories (e.g., `ProductRepository`, `UserRepository`) do not implement low-level file I/O operations directly. Instead, they interact via `FileUtil`:

* **Reading**: The repository calls `FileUtil.readAllLines(filePath)`. It parses each line with `line.split("\\|")`, constructs model instances, and returns Java collections (e.g., `List<Product>`).
* **Writing/Appending**: When adding a record, the repository formats the model into a delimited string and calls `FileUtil.appendLine(filePath, formattedLine)`.
* **Updating/Deleting**: The repository reads all lines, performs the update or removal in memory, and persists the updated collection using `FileUtil.writeAllLines(filePath, updatedLines)`.
