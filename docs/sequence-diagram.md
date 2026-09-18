# Sequence Diagrams

This document illustrates the execution sequence, message passing, and lifecycle interactions across the system layers for critical use cases in the **Inventory & Sales Management System**.

---

## 1. User Authentication Sequence

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant LoginUI
    participant AuthService as AuthenticationService
    participant UserRepo as UserRepository
    participant FileUtil
    participant Logger as LoggerUtil

    User->>LoginUI: Enter username & password
    LoginUI->>AuthService: login(username, password)
    AuthService->>UserRepo: findByUsername(username)
    UserRepo->>FileUtil: readAllLines("data/users.txt")
    FileUtil-->>UserRepo: List of pipe-separated records
    UserRepo-->>AuthService: Optional<User>
    alt User not found or password mismatch
        AuthService->>Logger: logWarning("Failed login attempt for user: " + username)
        AuthService-->>LoginUI: throws ValidationException("Invalid username or password")
        LoginUI-->>User: Display error message
    else Credentials match
        AuthService->>AuthService: setCurrentUser(user)
        AuthService->>Logger: logInfo("User '" + username + "' successfully logged in.")
        AuthService-->>LoginUI: return User
        LoginUI-->>User: Redirect to MainMenuUI
    end
```

---

## 2. Product Creation & Validation Sequence

```mermaid
sequenceDiagram
    autonumber
    actor Admin
    participant ProductUI
    participant InputValidator
    participant ProductService
    participant ProductRepo as ProductRepository
    participant SupplierRepo as SupplierRepository
    participant FileUtil

    Admin->>ProductUI: Select "Add New Product"
    ProductUI->>Admin: Prompt Product ID, Name, Category, Price, Qty, Threshold, SupplierID
    ProductUI->>InputValidator: validateRequiredText, validatePositiveDouble, validateNonNegativeInt
    InputValidator-->>ProductUI: Validated inputs
    ProductUI->>ProductService: addProduct(Product)
    ProductService->>ProductRepo: existsById(productId)
    ProductRepo-->>ProductService: false (not duplicate)
    opt Supplier ID provided
        ProductService->>SupplierRepo: existsById(supplierId)
        SupplierRepo-->>ProductService: true/false
    end
    ProductService->>ProductRepo: save(Product)
    ProductRepo->>FileUtil: appendLine("data/products.txt", serializedProduct)
    FileUtil-->>ProductRepo: true
    ProductRepo-->>ProductService: true
    ProductService-->>ProductUI: return saved Product
    ProductUI-->>Admin: Display success confirmation
```

---

## 3. End-to-End Sales Processing Sequence

```mermaid
sequenceDiagram
    autonumber
    actor Staff as Cashier / Admin
    participant SalesUI
    participant SalesService
    participant InventoryService
    participant ProductRepo as ProductRepository
    participant SaleRepo as SaleRepository
    participant FileUtil
    participant Logger as LoggerUtil

    Staff->>SalesUI: Enter Customer ID & add items (ProdId, Qty)
    SalesUI->>SalesUI: Build Sale instance with List<SaleItem>
    Staff->>SalesUI: Select PaymentMethod & Confirm Checkout
    SalesUI->>SalesService: processSale(Sale)

    %% Step 1: Pre-flight stock verification
    loop Verify stock for every item
        SalesService->>ProductRepo: findById(productId)
        ProductRepo-->>SalesService: Product
        alt Requested Qty > Available Qty
            SalesService-->>SalesUI: throw InsufficientStockException
            SalesUI-->>Staff: Transaction Aborted: Not enough stock
        end
    end

    %% Step 2: Atomic stock deduction
    loop Deduct stock for every item
        SalesService->>InventoryService: decreaseStock(productId, itemQty)
        InventoryService->>ProductRepo: update(product)
        ProductRepo->>FileUtil: overwriteFile("data/products.txt", updatedLines)
        FileUtil-->>ProductRepo: true
    end

    %% Step 3: Record sale
    SalesService->>SaleRepo: save(Sale)
    SaleRepo->>FileUtil: appendLine("data/sales.txt", serializedSale)
    FileUtil-->>SaleRepo: true

    %% Step 4: Audit logging
    SalesService->>Logger: logAudit("SALE_COMPLETED", saleId, totalAmount)
    SalesService-->>SalesUI: return completed Sale

    %% Step 5: Render invoice
    SalesUI-->>Staff: Print formatted receipt with totals and payment method
```
