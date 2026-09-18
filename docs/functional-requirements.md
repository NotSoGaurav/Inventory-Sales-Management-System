# Functional Requirements

This document outlines the detailed functional requirements of the **Inventory & Sales Management System**.

---

## 1. User Authentication and Authorization (FR-AUTH)

* **FR-AUTH-01: User Login**: The system shall authenticate users against credentials stored in `data/users.txt` using username and password.
* **FR-AUTH-02: Role-Based Access Control (RBAC)**: The system shall enforce role permissions:
  * **ADMIN**: Full access to all modules including Product CRUD, Inventory Stock-in, Supplier Management, Customer Management, Sales Processing, and Analytical Reports.
  * **CASHIER**: Access restricted to Sales Processing, Customer Management, Product Search, and Stock Viewing. Administrative tasks (product editing/deletion, system configuration) shall be blocked.
* **FR-AUTH-03: Session Management**: The system shall maintain the authenticated user's session context during runtime and provide a clean logout capability.

---

## 2. Product Management (FR-PROD)

* **FR-PROD-01: Add Product**: Authorized users shall be able to register new products with a unique Product ID, name, category, unit price (> 0), initial quantity (>= 0), minimum threshold (>= 0), and supplier reference.
* **FR-PROD-02: View All Products**: The system shall display all catalogued products in a formatted tabular view.
* **FR-PROD-03: Search Products**: The system shall enable searching products by ID, name, or category using case-insensitive partial match.
* **FR-PROD-04: Update Product**: Authorized users shall be able to update product details (name, category, price, minimum threshold, supplier) while preserving uniqueness of the Product ID.
* **FR-PROD-05: Delete Product**: Authorized users shall be able to delete a product from storage.

---

## 3. Inventory and Stock Control (FR-INV)

* **FR-INV-01: Stock Status Computation**: The system shall dynamically calculate product stock status:
  * `OUT_OF_STOCK`: Quantity == 0
  * `LOW_STOCK`: Quantity > 0 and Quantity <= minStockThreshold
  * `IN_STOCK`: Quantity > minStockThreshold
* **FR-INV-02: Increase Stock (Restocking)**: Users shall be able to replenish stock for a given product by specifying a positive increment.
* **FR-INV-03: Decrease Stock (Adjustment)**: Users shall be able to manually decrease stock, provided requested quantity does not exceed available stock.
* **FR-INV-04: Low Stock Alerts**: The system shall filter and present all items currently falling at or below their configured minimum threshold.

---

## 4. Customer Management (FR-CUST)

* **FR-CUST-01: Add Customer**: Users shall register customers with unique Customer ID, full name, 10-digit phone number, valid email address, and physical address.
* **FR-CUST-02: View All Customers**: Display a list of all registered customers.
* **FR-CUST-03: Search Customer**: Search customer records by ID, name, or phone number.
* **FR-CUST-04: Update Customer Details**: Modify contact details or address for an existing customer.

---

## 5. Supplier Management (FR-SUPP)

* **FR-SUPP-01: Register Supplier**: Manage supplier profiles with Supplier ID, supplier name, contact person, phone, email, and address in `data/suppliers.txt`.
* **FR-SUPP-02: Supplier Association**: Link products to registered suppliers for procurement traceability.

---

## 6. Sales and Billing Module (FR-SALE)

* **FR-SALE-01: New Sale Creation**: Cashiers/Admins shall initiate sales linked to registered customer IDs or guest transactions.
* **FR-SALE-02: Cart Management**: Add multiple items with quantities to a sale. The system shall dynamically compute item subtotals (`quantity * unitPrice`) and running grand total.
* **FR-SALE-03: Stock Verification**: Prior to finalizing a sale, the system shall verify that requested quantities are in stock. If stock is insufficient, an `InsufficientStockException` shall abort the sale.
* **FR-SALE-04: Payment Method Selection**: Support multiple payment options: `CASH`, `CARD`, `UPI`, `NET_BANKING`.
* **FR-SALE-05: Atomic Inventory Deduction**: Finalizing a sale shall immediately deduct sold quantities from `data/products.txt`.
* **FR-SALE-06: Invoice Generation**: The system shall display and log a formatted invoice receipt including Sale ID, timestamp, customer info, itemized line items, and grand total.
* **FR-SALE-07: Sales History Lookup**: Retrieve past sales records and detailed transaction line items from `data/sales.txt`.

---

## 7. Reports and Analytics (FR-REP)

* **FR-REP-01: Revenue Summary**: Compute total accumulated revenue, number of completed transactions, and average transaction value.
* **FR-REP-02: Inventory Valuation**: Calculate total inventory units and aggregate inventory valuation (`sum(quantity * price)`).
* **FR-REP-03: Low Stock Report**: Produce a dedicated report listing products needing re-orders.
* **FR-REP-04: Top-Selling Products**: Rank products by total sales volume and total revenue generated.

---

## 8. Persistence & Logging (FR-SYS)

* **FR-SYS-01: Flat-File Persistence**: All state changes must be persisted immediately to corresponding `.txt` files in the `data/` directory.
* **FR-SYS-02: Audit & Error Logging**: All security events (login/failed attempts), financial operations (sales completed), and system errors must be written to `data/app.log` with timestamp and severity level.
