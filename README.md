# Student Vehicle Parking Management System

A JavaFX desktop application that automates campus vehicle entry/exit using QR codes,
built with an MVC architecture on top of MySQL/JDBC.

## Tech Stack
- Java 17 (or 21)
- JavaFX 21 (Controls, FXML)
- MySQL + JDBC (mysql-connector-j)
- ZXing (QR code generation & decoding)
- Apache PDFBox (PDF export)
- Apache POI (Excel export)
- Maven

## Project Structure
```
StudentParkingSystem
├── pom.xml
├── sql/schema.sql
└── src/main
    ├── java/com/parking
    │   ├── Main.java
    │   ├── controller/   (LoginController, DashboardController, StudentController,
    │   │                  VehicleController, ParkingLogController, ScannerController,
    │   │                  ReportController, Session)
    │   ├── model/        (User, Administrator, Student, Vehicle, ParkingLog, Gate)
    │   ├── service/       (AuthenticationService, ParkingService, QRService, ReportService,
    │   │                  ReportGenerator + DailyReport/WeeklyReport/MonthlyReport)
    │   ├── database/      (DBConnection)
    │   └── util/          (QRGenerator, QRScanner, AlertHelper)
    └── resources
        ├── view/*.fxml
        └── css/style.css
```

## 1. Set Up the Database
1. Make sure MySQL Server is running locally.
2. Run the schema script:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```
   This creates the `parking_system` database, all tables, a default admin account,
   and one sample student/vehicle.
3. Default login: **username `admin` / password `admin123`**.

## 2. Configure the Connection
Open `src/main/java/com/parking/database/DBConnection.java` and update:
```java
private static final String USER = "root";
private static final String PASSWORD = "";  // <- put your MySQL password here
```

## 3. Build & Run
This project uses Maven, but dependencies (JavaFX, MySQL connector, ZXing, PDFBox, POI)
must be downloaded from Maven Central the first time you build — make sure you have
internet access, then run:

```bash
mvn clean javafx:run
```

Or build a runnable jar and launch it with the JavaFX plugin of your IDE (IntelliJ /
Eclipse with e(fx)clipse / Scene Builder for editing the FXML files visually).

## 4. Using the App

### Administrator flow
1. Log in from the Login screen.
2. From the sidebar: manage **Students** (Add/Update/Delete/Search, upload photo,
   Generate QR — saved to `src/main/resources/qrcodes/{studentID}.png`), manage
   **Vehicles** (linked to a Student ID), review **Parking Logs** (filter by date /
   student / vehicle), and generate **Reports** (Daily / Weekly / Monthly, exportable
   to PDF or Excel under `src/main/resources/exports/`).
2. Click **Scanner Kiosk** to open the student-facing kiosk window.

### Scanner Kiosk flow (student)
The kiosk's QR input field is designed to work with a real USB QR scanner, which acts
like a keyboard: it types the QR code's text and presses Enter automatically. You can
also test it manually by typing a Student ID (e.g. `2023-00123` from the sample data)
into the field and pressing Enter. The workflow then runs automatically:

```
Scan QR → Verify QR → Verify Student → Verify Vehicle → Check Active Log
   → Record Entry (or Exit if already inside) → Open Gate → Access Granted → Reset
```

If any step fails, the kiosk shows an explicit message ("Invalid QR Code",
"Vehicle Not Registered", etc.) and resets after a few seconds.

## Notes / Possible Extensions
- Passwords are stored as SHA-256 hashes (see `AuthenticationService.hash()`); consider
  bcrypt/argon2 with a proper salt for production use.
- `QRScanner.decodeQRFromImage()` is included so a webcam snapshot or uploaded QR image
  can also be decoded, in case your kiosk uses a camera instead of a laser/keyboard scanner.
- The `Gate` class currently just logs open/close events to the console — wire it up to
  real hardware (serial/GPIO/relay) as needed.
