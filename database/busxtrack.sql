CREATE DATABASE IF NOT EXISTS busxtrack;
USE busxtrack;

DROP TABLE IF EXISTS timetable;
DROP TABLE IF EXISTS buses;
DROP TABLE IF EXISTS stops;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS drivers;
DROP TABLE IF EXISTS admins;

CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE drivers (
    driver_id INT PRIMARY KEY AUTO_INCREMENT,
    driver_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    license_number VARCHAR(50) NOT NULL
);

CREATE TABLE routes (
    route_id INT PRIMARY KEY AUTO_INCREMENT,
    route_name VARCHAR(100) NOT NULL,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL
);

CREATE TABLE stops (
    stop_id INT PRIMARY KEY AUTO_INCREMENT,
    route_id INT NOT NULL,
    stop_name VARCHAR(100) NOT NULL,
    stop_order INT NOT NULL,
    minutes_from_start INT NOT NULL,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE
);

CREATE TABLE buses (
    bus_id INT PRIMARY KEY AUTO_INCREMENT,
    bus_number VARCHAR(20) NOT NULL UNIQUE,
    route_id INT NOT NULL,
    driver_id INT NOT NULL,
    capacity INT NOT NULL,
    FOREIGN KEY (route_id) REFERENCES routes(route_id),
    FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
);

CREATE TABLE timetable (
    timetable_id INT PRIMARY KEY AUTO_INCREMENT,
    bus_id INT NOT NULL,
    route_id INT NOT NULL,
    departure_time TIME NOT NULL,
    trip_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (bus_id) REFERENCES buses(bus_id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(route_id)
);

INSERT INTO admins (username, password) VALUES ('admin', 'admin123');

INSERT INTO drivers (driver_name, phone_number, license_number) VALUES
('Ramesh Kumar', '9876543210', 'DL-KA-2021-1001'),
('Suresh Patil', '9876501234', 'DL-KA-2020-2231'),
('Mahesh Rao', '9876512345', 'DL-KA-2019-3478');

INSERT INTO routes (route_name, source, destination) VALUES
('Route A', 'College Campus', 'City Center'),
('Route B', 'College Campus', 'Railway Station'),
('Route C', 'College Campus', 'Tech Park');

INSERT INTO stops (route_id, stop_name, stop_order, minutes_from_start) VALUES
(1, 'College Campus', 1, 0),
(1, 'Main Gate', 2, 5),
(1, 'Market Road', 3, 15),
(1, 'City Hospital', 4, 25),
(1, 'City Center', 5, 35),
(2, 'College Campus', 1, 0),
(2, 'Library Circle', 2, 8),
(2, 'Old Bus Stand', 3, 18),
(2, 'Railway Station', 4, 30),
(3, 'College Campus', 1, 0),
(3, 'Sports Complex', 2, 7),
(3, 'Ring Road', 3, 20),
(3, 'Tech Park', 4, 32);

INSERT INTO buses (bus_number, route_id, driver_id, capacity) VALUES
('BX101', 1, 1, 45),
('BX102', 2, 2, 50),
('BX103', 3, 3, 45);

INSERT INTO timetable (bus_id, route_id, departure_time, trip_type) VALUES
(1, 1, '08:00:00', 'MORNING'),
(1, 1, '16:30:00', 'EVENING'),
(2, 2, '08:10:00', 'MORNING'),
(2, 2, '16:40:00', 'EVENING'),
(3, 3, '08:20:00', 'MORNING'),
(3, 3, '16:50:00', 'EVENING');
