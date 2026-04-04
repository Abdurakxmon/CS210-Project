# CS210 Car Rental System

This is a JavaFX and MySQL-based application designed for a Car Rental System. It provides a user interface to interact with a MySQL database seamlessly using the DAO (Data Access Object) pattern.

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL Server 8.0+

## Configuration

> **Note:** `DatabaseManager.java` is intentionally ignored in Git to prevent accidentally sharing database credentials. You will need to create this file locally.

Create the file `src/main/java/com/cs210/project/DatabaseManager.java` and paste the following snippet. Ensure you update the `URL`, `USER`, and `PASSWORD` to match your local MySQL configuration:

```java
package com.cs210.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USER = "your_mysql_username";
    private static final String PASSWORD = "your_mysql_password";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}
```

## How to Run

1. Open a terminal or command prompt in the project root directory.
2. Compile and run the application using Maven:
   ```bash
   mvn clean javafx:run
   ```

## Current Features
- **Database Connection Tester:** Verify that the Java application is properly hooked up to your MySQL server.
- **Vehicle Registration:** Fill out a JavaFX form to dynamically insert vehicles into the heavily featured `vehicles` database schema. Includes optional fields and safe null handling.
