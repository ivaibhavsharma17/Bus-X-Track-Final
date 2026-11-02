# BusXTrack Viva Quick Answers

## What does the project do?

It stores college bus transport data and lets students search buses or stops. It shows routes, drivers, timetables, bus status and an estimated arrival based on the stored schedule.

## Is it real-time GPS tracking?

No. It is a schedule-based simulation. The prototype does not use GPS, maps or external APIs.

## Where does current time come from?

Java reads it from the computer running the application using `LocalTime.now()`. The user does not enter the current time.

## What is the ETA formula?

Expected arrival = departure time + stop minutes from route start. Remaining ETA = expected arrival - current system time.

Example: departure 08:00, Market Road 15 minutes from start, current time 08:10. Expected arrival is 08:15, so the remaining ETA is approximately 5 minutes.

## What does RUNNING mean?

The current time is between the first scheduled departure and the last stop time. Before departure is NOT STARTED. After the last stop is COMPLETED.

## Why use Swing?

Swing is part of core Java and is suitable for a simple desktop GUI prototype. It lets us demonstrate forms, buttons, tables, dialogs and screen navigation.

## Why use JDBC?

JDBC connects Java to MySQL. DAO classes use JDBC to run SQL queries and return model objects to the GUI.

## Why are there separate DAO classes?

Each DAO handles database operations for one entity. This keeps SQL separate from the Swing forms and makes the code easier to understand.

## What is the innovation?

The project combines timetable management with a transparent ETA simulation for a college bus workflow. It is a low-cost prototype that can later be connected to real GPS data.

## What are the limitations?

It does not know the physical bus location, traffic conditions or delays. It assumes the bus follows the configured schedule.

## What are the future enhancements?

GPS hardware or a mobile location source, online deployment, student accounts, notifications, delay reporting and better validation.
