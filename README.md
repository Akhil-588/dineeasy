# DineEasy – Food Ordering & Billing System

A complete Java console application built with Core Java, JDBC, and MySQL.

---

## Project Structure

```
DineEasy/
├── sql/
│   └── dineeasy_schema.sql              ← Database schema + sample data
├── lib/
│   └── mysql-connector-j-8.x.x.jar     ← Place MySQL JDBC driver here
└── src/
    └── main/
        └── java/
            └── com/dineeasy/
                ├── model/
                │   ├── User.java
                │   ├── Product.java
                │   ├── CartItem.java
                │   ├── Order.java
                │   └── OrderItem.java
                ├── dao/
                │   ├── UserDAO.java          ← Interface
                │   ├── UserDAOImpl.java      ← JDBC implementation
                │   ├── ProductDAO.java       ← Interface
                │   ├── ProductDAOImpl.java   ← JDBC implementation
                │   ├── OrderDAO.java         ← Interface
                │   └── OrderDAOImpl.java     ← JDBC implementation
                ├── service/
                │   ├── UserService.java
                │   ├── ProductService.java
                │   ├── CartService.java
                │   └── OrderService.java
                ├── util/
                │   ├── DatabaseConnection.java
                │   └── InputValidator.java
                └── main/
                    └── DineEasyApp.java      ← Entry point
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK    | 17 or above (uses switch expressions & records) |
| MySQL       | 8.0 or above |
| MySQL JDBC Driver | mysql-connector-j 8.x |

---

## Step-by-Step Setup

### Step 1 – Install MySQL and create the database

1. Start MySQL Server.
2. Open MySQL Workbench or the MySQL CLI:

```bash
mysql -u root -p
```

3. Run the SQL script:

```sql
SOURCE /path/to/DineEasy/sql/dineeasy_schema.sql;
```

Or paste the entire file content into Workbench and execute it.

This creates the `dineeasy` database with four tables and inserts sample data.

---

### Step 2 – Configure the database connection

Open `src/main/java/com/dineeasy/util/DatabaseConnection.java` and update:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/dineeasy?useSSL=false&serverTimezone=UTC";
private static final String USERNAME = "root";     // ← your MySQL username
private static final String PASSWORD = "root";     // ← your MySQL password
```

---

### Step 3 – Download the MySQL JDBC Driver

Download `mysql-connector-j-8.x.x.jar` from:
https://dev.mysql.com/downloads/connector/j/

Place the JAR inside the `lib/` folder.

---

### Step 4 – Compile the project

From the **DineEasy root directory**:

```bash
# Windows
javac -cp "lib/mysql-connector-j-8.x.x.jar" -d out -sourcepath src/main/java ^
    src/main/java/com/dineeasy/util/*.java ^
    src/main/java/com/dineeasy/model/*.java ^
    src/main/java/com/dineeasy/dao/*.java ^
    src/main/java/com/dineeasy/service/*.java ^
    src/main/java/com/dineeasy/main/*.java
```

```bash
# Linux / macOS
javac -cp "lib/mysql-connector-j-8.x.x.jar" -d out \
    $(find src -name "*.java")
```

---

### Step 5 – Run the application

```bash
# Windows
java -cp "out;lib/mysql-connector-j-8.x.x.jar" com.dineeasy.main.DineEasyApp

# Linux / macOS
java -cp "out:lib/mysql-connector-j-8.x.x.jar" com.dineeasy.main.DineEasyApp
```

---

### Step 6 – Using an IDE (IntelliJ / Eclipse)

1. **IntelliJ IDEA**
   - File → New → Project from Existing Sources → select the `DineEasy` folder.
   - Right-click `lib/mysql-connector-j-8.x.x.jar` → Add as Library.
   - Run `DineEasyApp.java`.

2. **Eclipse**
   - File → Import → Existing Java Project.
   - Right-click project → Build Path → Add External JARs → select the JDBC jar.
   - Run `DineEasyApp.java` as Java Application.

---

## Sample Login Credentials

| Username | Password    | Role  |
|----------|-------------|-------|
| admin    | admin123    | ADMIN |
| alice    | alice123    | USER  |
| bob      | bob123      | USER  |
| charlie  | charlie123  | USER  |

---

## Feature Walkthrough

### User Features
1. **Register** – Create a new account (username min 3 chars, password min 6 chars).
2. **Login** – Authenticate with username & password.
3. **View Menu** – See all food items with ID, name, price, and stock.
4. **Add to Cart** – Add items by product ID and quantity.
5. **View Cart** – See all cart items with sub-totals and grand total.
6. **Update Quantity** – Change quantity of an existing cart item.
7. **Remove from Cart** – Remove a specific item.
8. **Checkout** – Confirm order, choose payment method, generate receipt.
9. **Order History** – View all past orders with itemised details.

### Admin Features
1. **Add Food Item** – Insert new menu items with name, price, and stock.
2. **Update Food Item** – Edit existing item's name, price, or quantity.
3. **Delete Food Item** – Remove an item from the menu.
4. **View All Orders** – See every order placed by all customers.

---

## OOP Concepts Used

| Concept | Where Applied |
|---------|---------------|
| **Encapsulation** | All model classes (User, Product, Order, CartItem, OrderItem) use private fields with getters/setters |
| **Inheritance** | DAOImpl classes implement their respective DAO interfaces; User has an `isAdmin()` helper demonstrating role-based polymorphism |
| **Polymorphism** | DAO interfaces (UserDAO, ProductDAO, OrderDAO) allow swappable implementations |
| **Interfaces** | Full DAO layer defined as interfaces, implemented by JDBC classes |
| **Abstraction** | Service layer abstracts business logic from the UI; UI only calls services |

---

## Architecture Layers

```
┌────────────────────────────────┐
│        DineEasyApp.java        │  ← Console UI / Main
└──────────────┬─────────────────┘
               │ calls
┌──────────────▼─────────────────┐
│  UserService / ProductService  │  ← Business Logic Layer
│  CartService / OrderService    │
└──────────────┬─────────────────┘
               │ calls
┌──────────────▼─────────────────┐
│  UserDAO / ProductDAO          │  ← DAO Interfaces
│  OrderDAO (interfaces)         │
└──────────────┬─────────────────┘
               │ implemented by
┌──────────────▼─────────────────┐
│  UserDAOImpl / ProductDAOImpl  │  ← JDBC Implementations
│  OrderDAOImpl                  │
└──────────────┬─────────────────┘
               │ connects via
┌──────────────▼─────────────────┐
│    DatabaseConnection.java     │  ← Singleton DB Utility
└──────────────┬─────────────────┘
               │
┌──────────────▼─────────────────┐
│         MySQL Database         │  ← users, products, orders, order_items
└────────────────────────────────┘
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | JDBC JAR not on classpath. Re-check the `-cp` flag. |
| `Access denied for user 'root'@'localhost'` | Wrong MySQL credentials in `DatabaseConnection.java`. |
| `Unknown database 'dineeasy'` | SQL script not run yet. Execute `dineeasy_schema.sql`. |
| `java: error: release version 17 not supported` | Use JDK 17+. Check with `java -version`. |
| Compile error on `switch` expression | Ensure you compile with `--release 17` or higher. |
