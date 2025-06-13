-- Peupler la table Box
INSERT INTO box (name, address, city, zipcode, schedule, created_at, updated_at)
VALUES ('Pieds croisés Paris', '12 Champs-Élysée', 'Paris', '72000', 'Lun-Ven 08h-18h', NOW(), NOW());
INSERT INTO box (name, address, city, zipcode, schedule, created_at, updated_at)
VALUES ('Pieds croisés Pau', '1 rue des Pyrénées', 'Pau', '64000', 'Lun-Ven 08h-18h', NOW(), NOW());

-- Peupler la table Message
INSERT INTO message (title, content, message_type, start_date, expiration_date,  created_at, updated_at)
VALUES ('Oyez Oyez info', 'Venez nombreux à notre super évent', 'INFORMATION', '2025-05-17', '2025-06-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');
INSERT INTO message (title, content, message_type, start_date, expiration_date,  created_at, updated_at)
VALUES ('Oyez Oyez alert', 'Venez nombreux à notre super évent', 'ALERT', '2025-08-17', '2025-12-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');
INSERT INTO message (title, content, message_type, start_date, expiration_date,  created_at, updated_at)
VALUES ('Oyez Oyez event', 'Venez nombreux à notre super évent', 'EVENEMENT', '2025-05-17', '2025-12-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');
INSERT INTO message (title, content, message_type, start_date, expiration_date,  created_at, updated_at)
VALUES ('Oyez Oyez rappel', 'Venez nombreux à notre super évent', 'RAPPEL', '2025-05-17', '2025-12-01',
        '2025-05-01T10:00:00', '2025-05-01T10:00:00');

    -- Rajouter une enom monthly
-- Peupler la table Subscription
INSERT INTO subscription (name, price, duration, freeze_days_allowed, session_per_week, termination_conditions)
VALUES ('Classique', 19.99, 31, 5, 2, 'blablabla');
INSERT INTO subscription (name, price, duration, freeze_days_allowed, session_per_week, termination_conditions)
VALUES ('Premium', 39.99, 31, 5, 4, 'blablabla');
INSERT INTO subscription (name, price, duration, freeze_days_allowed, session_per_week, termination_conditions)
VALUES ('Ultimate', 99.99, 31, 5, 0, 'blablabla');

-- Peupler la table Exercice
INSERT INTO exercice (name, measure_type)
VALUES ('Pompes', 'REPETITION');
INSERT INTO exercice (name, measure_type)
VALUES ('Front squat', 'WEIGHT');
INSERT INTO exercice (name, measure_type)
VALUES ('Back squat', 'WEIGHT');

-- Peupler la table user
INSERT INTO user (firstname, lastname, password, email, phone, profile_picture, created_at, updated_at)
VALUES ('Jean', 'Dupont', '{noop}user123', 'jean.dupont@example.com', '0601020304', NULL,
        NOW(), NOW()),
       ('Admin', 'User', '{noop}admin123', 'admin@example.com', '0602030405', NULL,
        NOW(), NOW());

-- Peupler la table user_roles (si @ElementCollection utilise une table "user_roles" avec user_id et roles)
INSERT INTO user_roles (user_id, roles)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN');

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