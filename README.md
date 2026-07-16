#  Student Vehicle Parking Management System

A **JavaFX desktop application** designed to automate campus vehicle entry and exit using **QR Code authentication**. The system allows administrators to manage students, vehicles, parking logs, and reports while providing a QR Scanner Kiosk that uses a laptop webcam to verify student QR codes.

Built using **JavaFX**, **MySQL**, **JDBC**, and follows the **Model-View-Controller (MVC)** architectural pattern.

---

#  Features

## Administrator

- Secure Administrator Login (SHA-256 password authentication)
- Dashboard with real-time statistics
- Student Management (Add, Update, Delete, Search)
- Vehicle Management
- Parking Log Management
- Daily, Weekly, and Monthly Reports
- Upload Student Photos
- Generate QR Codes
- QR Code Preview
- Save QR Code (Save As...)
- Export Reports to PDF
- Export Reports to Excel

---

## Scanner Kiosk

- Automatic Webcam Initialization
- Live Camera Preview
- QR Code Detection using Laptop Webcam
- Student Verification
- Vehicle Verification
- Automatic Entry Recording
- Automatic Exit Recording
- Gate Simulation
- Automatic Reset after Successful Scan

---

# Tech Stack

- Java 17
- JavaFX 21
- Maven
- MySQL
- JDBC (MySQL Connector/J)
- ZXing (QR Code Generation & Decoding)
- Webcam Capture API (sarxos)
- Apache PDFBox
- Apache POI

---

# Project Structure

```
StudentParkingSystem
│
├── pom.xml
├── sql/
│   └── schema.sql
│
└── src/
    └── main/
        ├── java/
        │   └── com.parking/
        │       ├── controller/
        │       ├── model/
        │       ├── service/
        │       ├── database/
        │       ├── util/
        │       └── Main.java
        │
        └── resources/
            ├── css/
            ├── photos/
            ├── qrcodes/
            ├── exports/
            └── view/
```

---

# Database Setup

Start your MySQL server.

Execute the schema:

```bash
mysql -u root -p < sql/schema.sql
```

This will create:

- parking_system database
- administrator table
- student table
- vehicle table
- parking_log table
- sample student
- sample vehicle
- default administrator account

---

# Default Administrator Account

Username

```
admin
```

Password

```
123456
```

---

# Configure Database Connection

Open

```
src/main/java/com/parking/database/DBConnection.java
```

Update:

```java
private static final String USER = "root";
private static final String PASSWORD = "";
```

Replace the password with your MySQL password.

---

# Running the Project

Using Maven:

```bash
mvn clean javafx:run
```

Or run the project directly from IntelliJ IDEA.

The required libraries will automatically be downloaded from Maven Central.

---

# Administrator Workflow

```
Administrator Login
        │
        ▼
Dashboard
        │
        ├── Manage Students
        ├── Manage Vehicles
        ├── Parking Logs
        ├── Reports
        └── Scanner Kiosk
```

---

# Student Management Workflow

```
Add Student
      │
      ▼
Upload Photo
      │
      ▼
Generate QR Code
      │
      ▼
QR Saved
      │
      ▼
QR Preview
      │
      ├── Save As...
      └── Close
```

Generated QR Codes are stored in:

```
src/main/resources/qrcodes/
```

Each QR Code stores the Student ID.

Example:

```
2023-00123
```

---

#  Scanner Kiosk Workflow

When the Scanner Kiosk opens:

```
Open Scanner
      │
      ▼
Laptop Webcam Starts
      │
      ▼
Live Camera Preview
      │
      ▼
Detect QR Code
      │
      ▼
Verify Student
      │
      ▼
Verify Registered Vehicle
      │
      ▼
Check Active Parking Log
      │
      ▼
ENTRY or EXIT Recorded
      │
      ▼
Gate Opens
      │
      ▼
System Resets
```

---

#  Reports

The system supports:

- Daily Reports
- Weekly Reports
- Monthly Reports

Export Formats:

- PDF
- Excel (.xlsx)

Reports are saved inside:

```
src/main/resources/exports/
```

---

# QR Code System

Each student is assigned a unique QR Code.

The administrator can:

- Generate QR Code
- Preview QR Code
- Save As... (Copy QR anywhere)
- Reprint the QR Code anytime if the student loses it

The QR Code always contains the Student ID, allowing quick identification at the Scanner Kiosk.

---

# Security

- SHA-256 password hashing
- Prepared Statements
- JDBC parameterized queries
- MVC Architecture
- MySQL Foreign Key Constraints

---

# Future Enhancements

- QR Code Printing
- Multi-Administrator Accounts
- Camera Selection (Front/Rear/USB)
- Parking Slot Monitoring
- Email QR Codes to Students
- Backup & Restore Database
- Cloud Database Integration
- Gate Hardware Integration
- Student Mobile Application

---

#  Developed By

**John Emmanuel B. Montemayor**

Bachelor of Science in Information Technology (BSIT)

Cebu Institute of Technology – University (CIT-U)

---

## License

This project was developed for academic purposes.