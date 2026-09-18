# Problem Statement

## 1. Background and Context

In modern retail and wholesale operations, keeping track of stock levels, managing customer relationships, tracking suppliers, and recording daily sales transactions are fundamental business requirements. Small and medium-scale enterprises (SMEs) often struggle with inventory control due to:

- **Reliance on Manual Paper Registers**: Paper-based records are vulnerable to physical damage, loss, illegibility, and arithmetic errors.
- **Fragmented Spreadsheets**: Spreadsheet files lack data integrity constraints, concurrent validation, automated transactional stock deduction, and role-based access control.
- **Overstocking and Understocking**: Without real-time stock threshold tracking, businesses either lock capital in excess inventory or suffer lost revenue due to unexpected stock-outs.
- **Complex Enterprise Software Overhead**: Enterprise systems (ERP) and heavy relational database management systems (RDBMS like Oracle or MySQL) require dedicated server infrastructure, database administration, complex connection drivers (JDBC), and recurring licensing costs, making them unsuitable for lightweight, offline, or resource-constrained deployments.

## 2. Problem Formulation

There is a critical need for a **lightweight, reliable, platform-independent, and self-contained Inventory & Sales Management System**.

The system must:
1. Provide persistent storage without relying on external database engines or network-dependent services.
2. Maintain strict data integrity through domain validation and custom business exceptions.
3. Automatically adjust inventory levels during sales and stock replenishment.
4. Enforce role-based access control (Admin vs. Cashier/Staff) to secure administrative functionalities.
5. Generate actionable operational reports and analytics (low-stock alerts, sales revenues, top-selling items).
6. Be implemented strictly adhering to clean Object-Oriented Programming (OOP) principles, standard Java libraries, and clean layered architecture suitable for academic evaluation in the **Programming in Java** curriculum.
