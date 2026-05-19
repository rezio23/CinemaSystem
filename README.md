# Cinema Management System

A desktop application for managing a cinema, built with **Java Swing** and **Oracle Database**.

> This project was built as a final exam project for a Database (Oracle) course.

## Tech Stack

- **Java 17**
- **Oracle Database** (via JDBC)
- **Java Swing** with [FlatLaf](https://www.formdev.com/flatlaf/) modern look-and-feel
- **Maven** (build tool)
- **OpenPDF** (for report generation)

## Features

- Manage Movies, Halls, Staff, and Customers
- Schedule Movie Shows
- Book tickets with seat selection
- View and manage all bookings
- Generate reports
- Dashboard with analytics

## Prerequisites

1. **Java 17 or later** installed
2. **Oracle Database** running locally (or accessible remotely)
3. **Maven** installed (optional but recommended)
4. An Oracle user/schema with sufficient privileges to create tables

## Project Structure

```
├── src/main/java/com/cinema/      # Java source code
│   ├── Main.java                    # Application entry point
│   ├── dao/                         # Data Access Objects (database operations)
│   ├── model/                       # Entity classes
│   ├── service/                     # Business logic
│   ├── ui/                          # Swing UI panels and components
│   └── util/                        # Utilities (database connection)
├── src/main/resources/              # Configuration files
│   └── db.properties.example        # Database config template
├── lib/                             # JAR dependencies (for run.bat)
│   ├── flatlaf-3.4.1.jar
│   ├── flatlaf-extras-3.4.1.jar
│   ├── ojdbc11-21.20.0.0.jar
│   └── openpdf-2.0.2.jar
├── schema_oracle.sql                # CREATE TABLE statements only
├── setup_oracle.sql                 # DROP + CREATE + INSERT sample data
├── sample_data_oracle.sql           # INSERT statements only
├── drop_tables.sql                  # DROP TABLE statements
├── queries.sql                      # Reporting queries
├── pom.xml                          # Maven configuration
└── run.bat                          # Quick run script (Windows)
```

## Setup Instructions

### 1. Configure the Database

1. Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties`:
   ```bash
   copy src\main\resources\db.properties.example src\main\resources\db.properties
   ```
2. Edit `db.properties` with your Oracle credentials:
   ```properties
   jdbc.url=jdbc:oracle:thin:@localhost:1521/ORCL
   jdbc.username=your_username
   jdbc.password=your_password
   ```

### 2. Create Database Tables

You can use either of these approaches:

**Option A: Full Setup (Recommended)**
Run `setup_oracle.sql` in your Oracle client (SQL Developer, SQL*Plus, etc.).
This drops existing tables, recreates them, and inserts sample data.

**Option B: Step by Step**
1. Run `drop_tables.sql` (optional, if resetting)
2. Run `schema_oracle.sql` (creates tables)
3. Run `sample_data_oracle.sql` (inserts sample data)

**Option C: Run Queries Only**
After setting up data, run `queries.sql` to execute reporting queries.

### 3. Build and Run

**Option A: Using Maven (Recommended)**

If you have Maven installed:
```bash
mvn compile exec:java
```

**Option B: Using run.bat (Windows)**

If you do not have Maven, ensure the `lib/` folder contains all required JARs:
- `flatlaf-3.4.1.jar`
- `flatlaf-extras-3.4.1.jar`
- `ojdbc11-21.20.0.0.jar`
- `openpdf-2.0.2.jar`

Then run:
```bash
run.bat
```

The script will compile the source code and start the application.

## Database Schema

### Tables

| Table          | Description                          |
|----------------|--------------------------------------|
| `MOVIE`        | Movie information                    |
| `HALL`         | Cinema halls with capacity           |
| `STAFF`        | Staff members                        |
| `CUSTOMER`     | Registered customers                 |
| `MOVIE_SHOW`   | Scheduled movie shows              |
| `BOOKING`      | Ticket bookings                      |
| `BOOKING_SEAT` | Individual seats per booking         |

### Relationships

- A `MOVIE` can have many `MOVIE_SHOW`s
- A `HALL` can host many `MOVIE_SHOW`s
- A `CUSTOMER` can make many `BOOKING`s
- A `MOVIE_SHOW` can have many `BOOKING`s
- A `BOOKING` contains multiple `BOOKING_SEAT`s
- A `STAFF` member can process many `BOOKING`s

## License

This project is for educational purposes only.
