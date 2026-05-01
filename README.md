# CS210 Car Rental System

JavaFX + JDBC + MySQL desktop application for managing vehicle rentals.

## Database Import

Use database name `car_rental_system`.

In phpMyAdmin, import either:

- `schema.sql` first, then `seed.sql`
- or `src/main/resources/sql/car_rental_system_dataset.sql`

The SQL files include:

```sql
CREATE DATABASE IF NOT EXISTS car_rental_system;
USE car_rental_system;
```

That avoids phpMyAdmin `#1046 No database selected`.

## Test Credentials

Passwords are plain text for this school project because the current login code compares the entered password directly with `accounts.password_hash`.

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `password123` |
| Receptionist | `nigora` | `password123` |
| Worker | `worker` | `password123` |
| Member | `jama` | `pass123` |
| Member | `malika` | `password123` |

## Run

If Maven and Java are on `PATH`:

```powershell
mvn clean package
mvn javafx:run
```

On this machine, IntelliJ's bundled JDK/Maven work with:

```powershell
$env:JAVA_HOME='D:\apps\intelej idea\IntelliJ IDEA 2026.1\jbr'
& 'D:\apps\intelej idea\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd' clean package
& 'D:\apps\intelej idea\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd' javafx:run
```
