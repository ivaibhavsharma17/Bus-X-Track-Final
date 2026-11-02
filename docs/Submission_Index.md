# BusXTrack Mid-Term Submission Index

## Submit or show

- `BusXTrack_Project_Report.docx` - integrated project design and database report.
- `BusXTrack_Project_Report.pdf` - ready-to-print report export.
- `BusXTrack_Midterm_Presentation.pptx` - 10-slide presentation.
- `architecture.png` - system architecture diagram.
- `use_case.png` - use-case diagram.
- `eta_flow.png` - ETA calculation flow.
- `er_diagram.png` - ER-style database diagram.
- `../database/busxtrack.sql` - executable schema and sample data.
- `SQL_Sample_Queries.sql` - extra queries for the MySQL demonstration.

## Explain the project in one sentence

BusXTrack is a Java Swing and MySQL college bus information system that lets an admin maintain schedules and lets students search routes, stops, drivers and schedule-based simulated ETA.

## Important limitation to state clearly

The prototype does not use GPS. Java reads the computer's current time and calculates the expected stop time from the timetable departure plus the stop's configured minutes.

## Demo data

- Admin username: `admin`
- Admin password: `admin123`
- Bus search: `BX101`
- Stop search: `Market Road`

## Build check

From the project root:

```bash
javac -cp lib/mysql-connector-j-8.4.0.jar -d out $(find src -name '*.java')
```

Run `busxtrack.Main` from IntelliJ IDEA after importing the project and adding the MySQL connector library.
