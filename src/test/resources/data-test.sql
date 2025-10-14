-- ===============================
-- USERS
-- ===============================
INSERT INTO user (firstname, lastname, password, email, phone, profile_picture, created_at, updated_at)
VALUES
    ('Jean', 'Dupont', '$2y$10$pN5sgqp.gbzAJcoh04/1xeAsv/trTPxeNoep06U4i1WUWooSgc0Su', 'jean.dupont@example.com', '0601020304', NULL, NOW(), NOW()),
    ('Admin', 'User', '$2y$10$dUo1l4qgxCAgCetO4Y04U./8jQGw5wW6ZC2tv/G9tP2K3Q4uGbW6i', 'admin@example.com', '0601020305', NULL, NOW(), NOW()),
    ('Coach', 'Rico', '$2y$10$dUo1l4qgxCAgCetO4Y04U./8jQGw5wW6ZC2tv/G9tP2K3Q4uGbW6i', 'coach@example.com', '0602030408', NULL, NOW(), NOW());

-- ===============================
-- USER ROLES
-- ===============================
INSERT INTO user_roles (user_id, roles)
VALUES
    ((SELECT id FROM user WHERE email='jean.dupont@example.com'), 'ROLE_USER'),
    ((SELECT id FROM user WHERE email='admin@example.com'), 'ROLE_USER'),
    ((SELECT id FROM user WHERE email='admin@example.com'), 'ROLE_ADMIN'),
    ((SELECT id FROM user WHERE email='coach@example.com'), 'ROLE_COACH');

-- ===============================
-- BOX
-- ===============================
INSERT INTO box (name, address, email, phone_number, city, zipcode, schedule, updated_at)
VALUES
    ('Pieds croisés Paris', '12 Champs-Élysée', 'pied.croises@example.com', '01 02 03 04 05', 'Paris', '72000', 'Lun-Ven 08h-18h', NOW());

-- ===============================
-- MESSAGES
-- ===============================
INSERT INTO message (title, content, message_type, start_date, expiration_date, created_at, updated_at)
VALUES
    ('Oyez Oyez info', 'Contenu du message 1', 'INFORMATION', '2025-05-17', '2025-06-01', '2025-05-01T10:00:00', '2025-05-01T10:00:00'),
    ('Oyez Oyez alert', 'Contenu du message 2', 'ALERT', '2025-08-17', '2025-12-01', '2025-05-01T10:00:00', '2025-05-01T10:00:00'),
    ('Oyez Oyez event', 'Contenu du message 3', 'EVENT', '2025-05-17', '2025-12-01', '2025-05-01T10:00:00', '2025-05-01T10:00:00'),
    ('Oyez Oyez rappel', 'Accusamus molestias fugiat asperiores quas neque laboriosam non magnam dolorem. Totam repellat veniam nihil corporis in. Facilis veniam ducimus. Veritatis maiores explicabo doloribus quisquam odio architecto. Est at ullam earum et repellat neque delectus tempore eius. Quisquam suscipit dolor dolores tempora voluptatum.', 'REMINDER', '2025-05-17', '2025-12-01', '2025-05-01T10:00:00', '2025-05-01T10:00:00');

-- ===============================
-- SUBSCRIPTIONS
-- ===============================
INSERT INTO subscription (name, price, duration, freeze_days_allowed, session_per_week, termination_conditions)
VALUES
    ('Classique', 19.99, 31, 5, 2, 'blablabla'),
    ('Premium', 39.99, 31, 5, 4, 'blablabla'),
    ('Ultimate', 99.99, 31, 5, NULL, 'blablabla');

-- ===============================
-- EXERCICES
-- ===============================
INSERT INTO exercice (name, measure_type)
VALUES
    ('Pompes', 'REPETITION'),
    ('Front squat', 'WEIGHT'),
    ('Back squat', 'WEIGHT');

-- ===============================
-- COURSES
-- ===============================
INSERT INTO course (title, description, start_datetime, duration, person_limit, status, created_at, updated_at, coach_id)
VALUES
    ('WOD CARDIO', 'Bla Bla', '2025-08-28T10:00:00', 60, 12, 'OPEN', NOW(), NOW(), (SELECT id FROM user WHERE email='coach@example.com'));

-- ===============================
-- USER SUBSCRIPTIONS
-- ===============================
INSERT INTO user_subscription (start_date, end_date, freeze_days_remaining, user_id, subscription_id)
VALUES
    (NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), 5,
     (SELECT id FROM user WHERE email='jean.dupont@example.com'),
     (SELECT id FROM subscription WHERE name='Classique'));

-- ===============================
-- WEIGHT HISTORY
-- ===============================
INSERT INTO weight_history (weight, date, user_id)
VALUES
    (55, '2025-01-01', (SELECT id FROM user WHERE email='admin@example.com')),
    (57, '2025-02-01', (SELECT id FROM user WHERE email='admin@example.com')),
    (60, '2025-03-01', (SELECT id FROM user WHERE email='admin@example.com')),
    (62, '2025-04-01', (SELECT id FROM user WHERE email='admin@example.com')),
    (65, '2025-05-01', (SELECT id FROM user WHERE email='admin@example.com'));

-- ===============================
-- PERFORMANCE HISTORY
-- ===============================
INSERT INTO performance_history (date, measured_value, exercice_id, user_id)
VALUES
    ('2025-02-01', 33, (SELECT id FROM exercice WHERE name='Front squat'), (SELECT id FROM user WHERE email='admin@example.com')),
    ('2025-03-01', 36, (SELECT id FROM exercice WHERE name='Front squat'), (SELECT id FROM user WHERE email='admin@example.com')),
    ('2025-04-01', 38, (SELECT id FROM exercice WHERE name='Front squat'), (SELECT id FROM user WHERE email='admin@example.com')),
    ('2025-05-01', 41, (SELECT id FROM exercice WHERE name='Front squat'), (SELECT id FROM user WHERE email='admin@example.com'));

-- ===============================
-- USER ↔ COURSE
-- ===============================
INSERT INTO user_course (user_id, course_id, status, created_at)
VALUES (
           (SELECT id FROM user WHERE email = 'jean.dupont@example.com'),
           (SELECT id FROM course WHERE title = 'WOD CARDIO'),
           'REGISTERED',
           NOW()
);
