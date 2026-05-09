# 🎓 Scholarship and Financial Aid Tracker

A Java console application that helps students apply for scholarships, track their application status, and allows administrators to manage sponsors and scholarship records — all backed by a MySQL database.

## 👥 Group Members: Solo

| Full Name | CMS / Student ID | Section |
|-----------|-----------------|---------|
| Marwa Karim | 023-25-0221    | B       |


---

## 🎥 Demo Video

**Drive Link:** 
---

## 📌 GitHub Repository

**GitHub URL: https://github.com/karimmarwa651-wq/Scholarship-Tracker** 

---

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [OOP Concepts Used](#oop-concepts-used)
- [Database Schema](#database-schema)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [How to Run](#how-to-run)
- [Usage Guide](#usage-guide)
- [Sample Data](#sample-data)

---

## Overview

The **Scholarship and Financial Aid Tracker** is a command-line Java application that connects to a MySQL database to manage scholarship applications. Students can apply for scholarships, and the system automatically checks their eligibility based on CGPA and family income. Administrators can view all applications, manage sponsors, and generate student reports.

---

## Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | **View Applied Students** | Lists all students who have applied, with scholarship and status details |
| 2 | **View All Scholarships** | Displays available scholarships along with sponsor information |
| 3 | **Apply for a Scholarship** | Allows a student to register and apply; eligibility is checked automatically |
| 4 | **My Application Report** | Students can view their full report using their Student ID |
| 5 | **Add New Sponsor** | Adds a new scholarship sponsor to the database |

---

## Project Structure

```
project/
├── ScholarshipTracker.java     # Main application file (all classes)
├── ScholarshipTracker.class    # Compiled main class
├── Student.class               # Compiled Student class
├── Sponsor.class               # Compiled Sponsor class
├── Person.class                # Compiled abstract Person class
├── Displayable.class           # Compiled interface
├── scholarship_db.sql          # Database setup script (schema + seed data)
└── lib/
    └── mysql-connector-j-9.7.0.jar  # MySQL JDBC driver
```

---

## OOP Concepts Used

This project demonstrates core Object-Oriented Programming principles in Java:

### 1. Abstraction — `abstract class Person`
An abstract class that holds shared fields (`name`, `department`) and declares an abstract method `showInfo()` that all subclasses must implement.

### 2. Inheritance
- `Student extends Person` — inherits name and department fields
- `Sponsor extends Person` — inherits name; uses `"Sponsor"` as fixed department

### 3. Polymorphism
Both `Student` and `Sponsor` override the abstract `showInfo()` method from `Person`, providing their own implementations.

### 4. Interface — `Displayable`
Both `Student` and `Sponsor` implement the `Displayable` interface, which requires a `display()` method for a compact one-line summary.

### 5. Encapsulation
Private fields (`cgpa`, `familyIncome`, `budget`) are accessed only via public getter methods.

---

## Database Schema

The application uses a MySQL database named `scholarship_db` with 4 tables:

```
Sponsors
  └── SponsorID (PK), Name, Budget

Students
  └── StudentID (PK), Name, Department, CGPA, FamilyIncome

Scholarships
  └── ScholarshipID (PK), Name, MinCGPA, MaxIncome, Amount, SponsorID (FK), ScholarshipType

ScholarshipRecords
  └── AppID (PK), StudentID (FK), ScholarshipID (FK), ApplyDate, Status
```

**Scholarship Types:** `MERIT_BASED`, `NEED_BASED`, `BOTH`  
**Application Status:** `Pending`, `Approved`, `Rejected`

---

## Prerequisites

- **Java JDK 8+** — to compile and run the application
- **MySQL Server** — running locally on port `3306`
- **MySQL Connector/J** — already included at `lib/mysql-connector-j-9.7.0.jar`

---

## Setup & Installation

### Step 1 — Set up the database

Log into MySQL and run the SQL script:

```bash
mysql -u root -p < scholarship_db.sql
```

Or paste the contents of `scholarship_db.sql` directly into your MySQL client. This will:
- Create the `scholarship_db` database
- Create all 4 tables
- Insert sample sponsors, students, scholarships, and applications
- Automatically update application statuses based on eligibility rules

### Step 2 — Update database credentials

Open `ScholarshipTracker.java` and update these lines if your MySQL credentials differ:

```java
static String DB_URL  = "jdbc:mysql://localhost:3306/scholarship_db";
static String DB_USER = "root";
static String DB_PASS = "your_password_here";
```

### Step 3 — Compile the application

```bash
javac -cp ".;lib/mysql-connector-j-9.7.0.jar" ScholarshipTracker.java
```

> On macOS/Linux, use `:` instead of `;` as the classpath separator:
> ```bash
> javac -cp ".:lib/mysql-connector-j-9.7.0.jar" ScholarshipTracker.java
> ```

---

## How to Run

```bash
java -cp ".;lib/mysql-connector-j-9.7.0.jar" ScholarshipTracker
```

> On macOS/Linux:
> ```bash
> java -cp ".:lib/mysql-connector-j-9.7.0.jar" ScholarshipTracker
> ```

---

## Usage Guide

Once running, you will see the main menu:

```
============================================================
     SCHOLARSHIP AND FINANCIAL AID TRACKER
============================================================

MAIN MENU
------------------------------------------------------------
1. View Students Who Applied for Scholarships
2. View All Scholarships with Sponsor Info
3. Apply for a Scholarship
4. My Application Report (Enter Student ID)
5. Add New Sponsor
0. Exit
------------------------------------------------------------
```

**Option 3 — Applying for a Scholarship:**
1. Enter your name, department, CGPA, and family income
2. The system saves you as a new student and shows your Student ID
3. A list of available scholarships is displayed
4. Enter the Scholarship ID you want to apply for
5. The system checks your eligibility automatically and saves the result

**Option 4 — Checking your Report:**
- Enter the Student ID you received when applying
- Your full application report (student info, scholarship details, sponsor, and status) will be displayed

---

## Sample Data

The database comes pre-loaded with:

- **4 Sponsors:** OGDCL CS Department, HEC Need-Based Fund, Sindh Education Foundation, Ehsaas Program NGO
- **10 Students** across Computer Science, Software Engineering, Information Technology, and Electrical Engineering
- **4 Scholarships** ranging from Rs 40,000 to Rs 80,000
- **10 Sample Applications** with statuses automatically assigned based on eligibility rules
