-- NISIRCOP-LE Test Data Setup for SQLite
-- This script creates test data for all user roles and sample incidents

-- Insert test users
INSERT INTO users (id, username, password, email, role, created_by, is_active) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfF4Vw8Kz8Q8Kz8Q8Kz8Q8Kz8', 'admin@nisircop.et', 'SUPER_USER', NULL, 1),
(2, 'station_commander', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfF4Vw8Kz8Q8Kz8Q8Kz8Q8Kz8', 'commander@station1.et', 'POLICE_STATION', 1, 1),
(3, 'officer_001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfF4Vw8Kz8Q8Kz8Q8Kz8Q8Kz8', 'officer1@station1.et', 'OFFICER', 2, 1),
(4, 'officer_002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfF4Vw8Kz8Q8Kz8Q8Kz8Q8Kz8', 'officer2@station1.et', 'OFFICER', 2, 1);

-- Insert user profiles
INSERT INTO user_profiles (id, first_name, last_name, phone, badge_number, boundary, user_id) VALUES
(1, 'System', 'Administrator', '+251911000001', 'ADMIN001', 'POLYGON((38.7 9.0, 38.8 9.0, 38.8 9.1, 38.7 9.1, 38.7 9.0))', 1),
(2, 'Commander', 'Station One', '+251911000002', 'CMD001', 'POLYGON((38.7 9.0, 38.8 9.0, 38.8 9.1, 38.7 9.1, 38.7 9.0))', 2),
(3, 'Officer', 'One', '+251911000003', 'OFF001', 'POLYGON((38.7 9.0, 38.8 9.0, 38.8 9.1, 38.7 9.1, 38.7 9.0))', 3),
(4, 'Officer', 'Two', '+251911000004', 'OFF002', 'POLYGON((38.7 9.0, 38.8 9.0, 38.8 9.1, 38.7 9.1, 38.7 9.0))', 4);

-- Insert sample incidents
INSERT INTO incidents (id, title, description, incident_type, priority, latitude, longitude, reported_by, occurred_at) VALUES
(1, 'Theft at Piassa Market', 'Reported theft of mobile phone from vendor stall', 'Theft', 'High', 9.02497, 38.74689, 3, '2024-01-15 10:30:00'),
(2, 'Traffic Accident on Bole Road', 'Two vehicle collision near Bole Airport', 'Traffic Accident', 'Medium', 9.0215, 38.7612, 3, '2024-01-15 14:20:00'),
(3, 'Vandalism at Meskel Square', 'Graffiti found on public property', 'Vandalism', 'Low', 9.0200, 38.7500, 4, '2024-01-16 08:15:00'),
(4, 'Suspicious Activity near Stadium', 'Unusual behavior reported by residents', 'Suspicious Activity', 'Medium', 9.0300, 38.7600, 3, '2024-01-16 16:45:00'),
(5, 'Drug Possession Arrest', 'Individual found with illegal substances', 'Drug Crime', 'High', 9.0250, 38.7550, 4, '2024-01-17 12:30:00'),
(6, 'Domestic Dispute', 'Neighbor dispute over property boundary', 'Domestic Dispute', 'Medium', 9.0220, 38.7480, 3, '2024-01-17 19:20:00'),
(7, 'Burglary Attempt', 'Attempted break-in at residential property', 'Burglary', 'High', 9.0280, 38.7620, 4, '2024-01-18 02:15:00'),
(8, 'Public Disturbance', 'Loud party disturbing neighbors', 'Public Disturbance', 'Low', 9.0260, 38.7580, 3, '2024-01-18 22:30:00'),
(9, 'Assault Report', 'Physical altercation between individuals', 'Assault', 'High', 9.0240, 38.7520, 4, '2024-01-19 15:45:00'),
(10, 'Fraud Investigation', 'Suspected financial fraud case', 'Fraud', 'Medium', 9.0270, 38.7590, 3, '2024-01-19 11:20:00');

-- Update sequences for auto-increment
UPDATE sqlite_sequence SET seq = 10 WHERE name = 'incidents';
UPDATE sqlite_sequence SET seq = 4 WHERE name = 'users';
UPDATE sqlite_sequence SET seq = 4 WHERE name = 'user_profiles';