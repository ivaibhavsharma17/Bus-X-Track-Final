# BusXTrack

BusXTrack is a simple Java Swing and MySQL mini-project for a college bus tracking and ETA simulation system.

The project does not use GPS, Google Maps, or any external tracking API. ETA is calculated from:

- current system time
- bus departure time
- stop minutes from route start

## Technologies

- Java
- Swing
- JDBC
- MySQL

## IntelliJ IDEA Setup

1. Open this folder in IntelliJ IDEA.
2. Make sure the project SDK is selected.
3. The MySQL connector jar is already placed in `lib/mysql-connector-j-8.4.0.jar` and linked in the IntelliJ module file.
4. Create the database using `database/busxtrack.sql`.
5. Update database username/password in `src/busxtrack/db/DBConnection.java` if your MySQL setup is different.
6. Run `busxtrack.Main`.

## Default Admin Login

```text
Username: admin
Password: admin123
```

## Main Modules

- Admin login
- Bus management
- Driver management
- Route management
- Stop management
- Timetable management
- Student bus search
- Student stop search
- Route viewing
- Simulated ETA and bus status

## Project Notes

This project is intentionally kept beginner-friendly. It uses simple classes, Swing frames, JDBC DAO classes, and basic SQL tables. The ETA logic is in `ETAService`, so it can be explained separately during viva.
