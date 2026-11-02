USE busxtrack;

-- 1. Check all available buses with route names
SELECT b.bus_number, r.route_name, r.source, r.destination, b.capacity
FROM buses b
JOIN routes r ON b.route_id = r.route_id;

-- 2. Find all stops on Route A in travel order
SELECT s.stop_name, s.stop_order, s.minutes_from_start
FROM stops s
WHERE s.route_id = 1
ORDER BY s.stop_order;

-- 3. Find buses that serve Market Road
SELECT DISTINCT b.bus_number, r.route_name
FROM buses b
JOIN routes r ON b.route_id = r.route_id
JOIN stops s ON r.route_id = s.route_id
WHERE s.stop_name = 'Market Road';

-- 4. View the schedule and driver for a bus
SELECT b.bus_number, d.driver_name, t.trip_type, t.departure_time
FROM buses b
JOIN drivers d ON b.driver_id = d.driver_id
JOIN timetable t ON b.bus_id = t.bus_id
WHERE b.bus_number = 'BX101';

-- 5. Simulated expected arrival calculation for Market Road.
-- The Java ETAService performs the same addition at runtime.
SELECT b.bus_number, t.departure_time,
       ADDTIME(t.departure_time, SEC_TO_TIME(s.minutes_from_start * 60)) AS expected_arrival
FROM buses b
JOIN timetable t ON b.bus_id = t.bus_id
JOIN stops s ON b.route_id = s.route_id
WHERE b.bus_number = 'BX101' AND s.stop_name = 'Market Road';

-- 6. Count stops by route
SELECT r.route_name, COUNT(s.stop_id) AS number_of_stops
FROM routes r
LEFT JOIN stops s ON r.route_id = s.route_id
GROUP BY r.route_id, r.route_name;
