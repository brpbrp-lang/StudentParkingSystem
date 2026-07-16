-- ============================================================
-- Student Vehicle Parking Management System - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS parking_system;
USE parking_system;

-- ---------------------------------------------------------
-- Administrator
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS administrator (
    admin_id   INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL   -- SHA-256 hash
);

-- Default admin account: username = admin, password = admin123
-- (hash generated with SHA-256; see AuthenticationService.hash())
INSERT INTO administrator (username, password)
VALUES ('admin', '123456')
ON DUPLICATE KEY UPDATE username = username;

-- ---------------------------------------------------------
-- Student
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS student (
    student_id  VARCHAR(20)  PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    course      VARCHAR(100) NOT NULL,
    year_level  INT          NOT NULL,
    photo       VARCHAR(255),
    qr_code     VARCHAR(255)
);

-- ---------------------------------------------------------
-- Vehicle (each student may register one or more vehicles)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id    INT AUTO_INCREMENT PRIMARY KEY,
    student_id    VARCHAR(20) NOT NULL,
    plate_number  VARCHAR(20) NOT NULL,
    brand         VARCHAR(50),
    color         VARCHAR(30),
    type          VARCHAR(30),
    FOREIGN KEY (student_id) REFERENCES student(student_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ---------------------------------------------------------
-- Parking Log
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS parking_log (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    student_id  VARCHAR(20) NOT NULL,
    vehicle_id  INT NOT NULL,
    date        DATE NOT NULL,
    time_in     TIME,
    time_out    TIME NULL,
    status      VARCHAR(10) NOT NULL, -- 'ENTRY' or 'EXIT'
    FOREIGN KEY (student_id) REFERENCES student(student_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Helpful indexes for the Scanner Kiosk / Reports lookups
CREATE INDEX idx_parking_log_student ON parking_log(student_id);
CREATE INDEX idx_parking_log_status  ON parking_log(status);
CREATE INDEX idx_parking_log_date    ON parking_log(date);

-- ---------------------------------------------------------
-- Sample data (optional - safe to delete)
-- ---------------------------------------------------------
INSERT INTO student (student_id, name, course, year_level, photo, qr_code)
VALUES ('2023-00123', 'Juan Dela Cruz', 'BS Computer Science', 3, NULL, NULL)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO vehicle (student_id, plate_number, brand, color, type)
VALUES ('2023-00123', 'ABC-1234', 'Honda', 'Black', 'Motorcycle')
ON DUPLICATE KEY UPDATE plate_number = plate_number;
