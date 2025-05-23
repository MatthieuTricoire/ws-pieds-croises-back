-- Supprimer les données
DELETE
FROM exercice;
DELETE
FROM subscription;
DELETE
FROM box;
DELETE
FROM user;


-- Remettre à zéro les auto-incréments
ALTER TABLE subscription
    AUTO_INCREMENT = 1;
ALTER TABLE exercice
    AUTO_INCREMENT = 1;
ALTER TABLE box
    AUTO_INCREMENT = 1;
ALTER TABLE user
    AUTO_INCREMENT = 1;


-- Peupler la table Box
INSERT INTO box (name, address, city, zipcode, schedule, created_at, updated_at)
VALUES ('Pieds croisés Paris', '12 Champs-Élysée', 'Paris', '72000', 'Lun-Ven 08h-18h', NOW(), NOW());
INSERT INTO box (name, address, city, zipcode,
                 schedule, created_at, updated_at)
VALUES ('Pieds croisés Pau', '1 rue des Pyrénées', 'Pau', '64000', 'Lun-Ven 08h-18h', NOW(), NOW());

-- Peupler la table Subscription
INSERT INTO subscription (name, price, duration, session_per_week, termination_conditions, box_id)
VALUES ('Classique', 19.99, 1, 2, 'blablabla', 1);
INSERT INTO subscription (name, price, duration, session_per_week, termination_conditions, box_id)
VALUES ('Premium', 39.99, 1, 4, 'blablabla', 1);
INSERT INTO subscription (name, price, duration, session_per_week, termination_conditions, box_id)
VALUES ('Ultimate', 69.99, 1, 0, 'blablabla', 1);

-- Peupler la table Exercice
INSERT INTO exercice (name, measure_type)
VALUES ('Pompes', 'REPETITION');

INSERT INTO exercice (name, measure_type)
VALUES ('Front squat', 'WEIGHT');

INSERT INTO exercice (name, measure_type)
VALUES ('Back squat', 'WEIGHT');