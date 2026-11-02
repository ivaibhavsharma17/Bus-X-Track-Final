# BusXTrack Mid-Term Demo Script

Use this order during the live demonstration. Keep the database and project open before the examiner arrives.

## 1. Start and explain

Say: "BusXTrack is a Java Swing and MySQL desktop prototype for college bus schedules. It has two users. Admin maintains the data. Student searches bus information and sees a schedule-based simulated ETA. It does not use GPS or an external API."

## 2. Student workflow

1. Run `busxtrack.Main`.
2. Click `Continue as Student`.
3. Open `Search Bus` and enter `BX101`.
4. Point out the route, driver, trip type, departure, status, next stop and ETA columns.
5. Open `View Details` and explain that driver details and route data came from related MySQL tables.
6. Go back and open `Search by Stop`.
7. Select or enter `Market Road` and point out the buses serving that stop.

Say: "If the timetable departure is 08:00 and Market Road is 15 minutes from the route start, the expected arrival is 08:15. If the computer time is 08:10, the result is about 5 minutes. The current time comes from Java `LocalTime.now()`, so the student does not type the current time."

## 3. Admin workflow

1. Return to login.
2. Use username `admin` and password `admin123`.
3. Open the admin dashboard.
4. Open Bus Management and show the table and Add form.
5. Open Driver Management and Route/Stop Management.
6. Open Timetable Management and point out departure time and trip type.

Explain that CRUD changes are sent through DAO classes using JDBC prepared statements.

## 4. Database and design

Open `database/busxtrack.sql` or MySQL Workbench.

Explain the relationship: one route has many stops, a bus belongs to a route and driver, and a bus can have multiple timetable entries. Primary keys identify records and foreign keys connect them.

## 5. Close

Say: "The prototype is intentionally schedule-based. A future version could replace the simulated position with GPS data, but the current version demonstrates the complete GUI, database and calculation workflow required for this semester project."
