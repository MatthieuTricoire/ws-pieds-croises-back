-- Peupler la table Box
INSERT INTO box (name, address, email, phone_number, city, zipcode, schedule, updated_at)
VALUES ('Pieds croisés Paris', '12 Champs-Élysée', 'pied.croises@example.com', '01 02 03 04 05', 'Paris', '72000', 'Lun-Ven 08h-18h', NOW());

-- Peupler la table Message
INSERT INTO message (title, content, message_type, start_date, expiration_date, created_at, updated_at)
VALUES ('Oyez Oyez info', 'Contenu du message 1', 'INFORMATION', '2025-05-17', '2025-06-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');
INSERT INTO message (title, content, message_type, start_date, expiration_date, created_at, updated_at)
VALUES ('Oyez Oyez alert', 'Contenu du message 2', 'ALERT', '2025-08-17', '2025-12-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');
INSERT INTO message (title, content, message_type, start_date, expiration_date, created_at, updated_at)
VALUES ('Oyez Oyez event', 'Contenu du message 3', 'EVENT', '2025-05-17', '2025-12-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');
INSERT INTO message (title, content, message_type, start_date, expiration_date, created_at, updated_at)
VALUES ('Oyez Oyez rappel',
        'Accusamus molestias fugiat asperiores quas neque laboriosam non magnam dolorem. Totam repellat veniam nihil corporis in. Facilis veniam ducimus. Veritatis maiores explicabo doloribus quisquam odio architecto. Est at ullam earum et repellat neque delectus tempore eius. Quisquam suscipit dolor dolores tempora voluptatum.',
        'REMINDER', '2025-05-17', '2025-12-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');

-- Rajouter une enom monthly
-- Peupler la table Subscription
INSERT INTO subscription (name, price, duration, freeze_days_allowed, session_per_week, termination_conditions)
VALUES ('Classique', 19.99, 31, 5, 2, 'blablabla');
INSERT INTO subscription (name, price, duration, freeze_days_allowed, session_per_week, termination_conditions)
VALUES ('Premium', 39.99, 31, 5, 4, 'blablabla');
INSERT INTO subscription (name, price, duration, freeze_days_allowed, session_per_week, termination_conditions)
VALUES ('Ultimate', 99.99, 31, 5, null, 'blablabla');

-- Peupler la table Exercice
INSERT INTO exercice (name, measure_type)
VALUES ('Pompes', 'REPETITION');
INSERT INTO exercice (name, measure_type)
VALUES ('Front squat', 'WEIGHT');
INSERT INTO exercice (name, measure_type)
VALUES ('Back squat', 'WEIGHT');

-- Peupler la table user
INSERT INTO user (firstname, lastname, password, email, phone, profile_picture, created_at, updated_at)
VALUES ('Jean', 'Dupont', '$2y$10$pN5sgqp.gbzAJcoh04/1xeAsv/trTPxeNoep06U4i1WUWooSgc0Su', 'jean.dupont@example.com',
        '0601020304', NULL,
        NOW(), NOW()),
       ('Admin', 'User', '$2y$10$dUo1l4qgxCAgCetO4Y04U./8jQGw5wW6ZC2tv/G9tP2K3Q4uGbW6i', 'admin@example.com',
        '0601020305', NULL,
        NOW(), NOW()),
       ('Coco', 'Rico', '{noop}coach123', 'coach@example.com', '0602030408', NULL,
        NOW(), NOW()),
       ('Matthieu', 'Tricoire', '$2y$10$H9nV.LDccqVGYNar5dHDIeEpu5SsKduKk.c0Mr5feafA8un6E9PMO',
        'matthieutricoire@gmail.com', '0668272972', NULL, NOW(), NOW());
-- Peupler la table Course
INSERT INTO course (title, description, start_datetime, duration, person_limit, status, created_at, updated_at,
                    coach_id)
VALUES ('WOD CARDIO', 'Bla Bla', '2025-08-28T10:00:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('WOD GYM', 'Bla Bla', '2025-08-28T15:30:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('WOD TEAM', 'Bla Bla', '2025-08-31T09:00:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('OPEN GYM', 'Bla Bla', '2025-08-31T16:00:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('Cour', 'Bla Bla', '2025-09-01T10:00:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('Cour', 'Bla Bla', '2025-08-01T10:00:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('Cour2', 'Toujour du Bla Bla', '2025-09-01T10:00:00', 45, 2, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('Cour3', 'Coucou les petits loups !', '2025-07-01T10:00:00', 45, 5, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3);

INSERT INTO course (title, description, start_datetime, duration, person_limit, status, created_at, updated_at,
                    coach_id)
VALUES ('WOD CARDIO', 'Séance intense de cardio', '2025-09-01T06:00:00', 60, 1, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('WOD GYM', 'Renforcement musculaire', '2025-09-01T08:00:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('MOBILITY', 'Travail de mobilité articulaire', '2025-09-01T10:00:00', 45, 10, 'OPEN',
        '2025-06-13T11:42:51.40986', '2025-06-13T11:42:51.40986', 3),
       ('OPEN GYM', 'Accès libre à la salle', '2025-09-01T12:00:00', 120, 20, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('WOD TEAM', 'Séance en équipe', '2025-09-01T15:00:00', 60, 16, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('ENDURANCE', 'Travail cardio longue durée', '2025-09-01T17:00:00', 60, 12, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3),
       ('WOD CARDIO', 'Session HIIT cardio', '2025-09-01T19:00:00', 45, 14, 'OPEN', '2025-06-13T11:42:51.40986',
        '2025-06-13T11:42:51.40986', 3);


-- Peupler la table user_roles (si @ElementCollection utilise une table "user_roles" avec user_id et roles)
INSERT INTO user_roles (user_id, roles)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_USER'),
       (2, 'ROLE_ADMIN'),
       (3, 'ROLE_COACH');

INSERT INTO performance_history(date, value, exercice_id, user_id)
VALUES ('2025-02-01', 33, 2, 2),
       ('2025-03-01', 36, 2, 2),
       ('2025-04-01', 38, 2, 2),
       ('2025-05-01', 41, 2, 2);

-- UserSubscription
INSERT INTO user_subscription (start_date, end_date, freeze_days_remaining, user_id, subscription_id)
VALUES (NOW(),
        DATE_ADD(NOW(), INTERVAL 1 MONTH),
        5,
        1,
        1);


-- Peupler la table weight_history
INSERT INTO weight_history(weight, date, user_id)
VALUES (55, '2025-01-01', 2),
       (57, '2025-02-01', 2),
       (60, '2025-03-01', 2),
       (62, '2025-04-01', 2),
       (65, '2025-05-01', 2),
       (64, '2025-06-01', 2),
       (67, '2025-07-01', 2);
