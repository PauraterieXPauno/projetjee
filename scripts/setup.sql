-- =====================================================
--  Base de données : outil de gestion BDE
--  SGBD : PostgreSQL (voir notes de compatibilité MySQL en bas)
-- =====================================================

-- CREATE DATABASE bde;
-- \c bde

-- Nettoyage (ordre inverse des dépendances)
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS inscription;
DROP TABLE IF EXISTS evenement;
DROP TABLE IF EXISTS staff_type;
DROP TABLE IF EXISTS lieu;
DROP TABLE IF EXISTS compte;
DROP TABLE IF EXISTS role;

-- -----------------------------------------------------
-- role : rôles applicatifs (admin, membre BDE, étudiant...)
-- -----------------------------------------------------
CREATE TABLE role (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- -----------------------------------------------------
-- compte : utilisateurs de l'application
-- -----------------------------------------------------
CREATE TABLE compte (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,           -- hash (BCrypt), jamais en clair
    nom           VARCHAR(100) NOT NULL,
    prenom        VARCHAR(100) NOT NULL,
    telephone     VARCHAR(20),
    role_id       BIGINT       NOT NULL REFERENCES role(id),
    actif         BOOLEAN      NOT NULL DEFAULT TRUE,
    cree_le       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_compte_role ON compte(role_id);

-- -----------------------------------------------------
-- lieu : salles où se déroulent les événements
-- -----------------------------------------------------
CREATE TABLE lieu (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom       VARCHAR(150) NOT NULL UNIQUE,
    adresse   VARCHAR(255),
    capacite  INT CHECK (capacite IS NULL OR capacite > 0)
);

-- -----------------------------------------------------
-- evenement
-- -----------------------------------------------------
CREATE TABLE evenement (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titre         VARCHAR(200) NOT NULL,
    description   TEXT,
    date_debut    TIMESTAMP    NOT NULL,
    date_fin      TIMESTAMP    NOT NULL,
    lieu_id       BIGINT       NOT NULL REFERENCES lieu(id),
    capacite_max  INT CHECK (capacite_max IS NULL OR capacite_max > 0),
    prix          NUMERIC(8,2) NOT NULL DEFAULT 0 CHECK (prix >= 0),
    statut        VARCHAR(20)  NOT NULL DEFAULT 'BROUILLON'
                  CHECK (statut IN ('BROUILLON', 'PUBLIE', 'ANNULE', 'TERMINE')),
    createur_id   BIGINT       NOT NULL REFERENCES compte(id),
    cree_le       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_evenement_dates CHECK (date_fin > date_debut)
);

CREATE INDEX idx_evenement_lieu  ON evenement(lieu_id);
CREATE INDEX idx_evenement_date  ON evenement(date_debut);

-- -----------------------------------------------------
-- inscription : un compte s'inscrit à un événement
-- -----------------------------------------------------
CREATE TABLE inscription (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evenement_id      BIGINT      NOT NULL REFERENCES evenement(id) ON DELETE CASCADE,
    compte_id         BIGINT      NOT NULL REFERENCES compte(id)    ON DELETE CASCADE,
    date_inscription  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut            VARCHAR(20) NOT NULL DEFAULT 'CONFIRMEE'
                      CHECK (statut IN ('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE')),
    present           BOOLEAN     NOT NULL DEFAULT FALSE,   -- pointage à l'entrée
    CONSTRAINT uq_inscription UNIQUE (evenement_id, compte_id)
);

CREATE INDEX idx_inscription_compte ON inscription(compte_id);

-- -----------------------------------------------------
-- staff_type : postes possibles (bar, eau, sécurité...)
-- -----------------------------------------------------
CREATE TABLE staff_type (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- -----------------------------------------------------
-- staff : qui occupe quel poste sur quel événement
-- -----------------------------------------------------
CREATE TABLE staff (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evenement_id   BIGINT    NOT NULL REFERENCES evenement(id)  ON DELETE CASCADE,
    compte_id      BIGINT    NOT NULL REFERENCES compte(id)     ON DELETE CASCADE,
    staff_type_id  BIGINT    NOT NULL REFERENCES staff_type(id),
    heure_debut    TIMESTAMP,          -- optionnel : créneau du poste
    heure_fin      TIMESTAMP,
    CONSTRAINT uq_staff UNIQUE (evenement_id, compte_id, staff_type_id),
    CONSTRAINT chk_staff_heures CHECK (heure_fin IS NULL OR heure_debut IS NULL OR heure_fin > heure_debut)
);

CREATE INDEX idx_staff_evenement ON staff(evenement_id);
CREATE INDEX idx_staff_compte    ON staff(compte_id);

-- =====================================================
--  Données initiales
-- =====================================================
INSERT INTO role (nom, description) VALUES
    ('ADMIN',  'Administrateur de l''application'),
    ('MEMBRE', 'Membre du bureau / du BDE'),
    ('ETUDIANT', 'Étudiant adhérent');

INSERT INTO staff_type (nom, description) VALUES
    ('BAR',        'Service au bar'),
    ('EAU',        'Point d''eau / prévention'),
    ('SECURITE',   'Sécurité'),
    ('ENTREE',     'Accueil et contrôle des entrées'),
    ('VESTIAIRE',  'Gestion du vestiaire');

-- =====================================================
--  Notes de compatibilité MySQL / MariaDB
--  - Remplacer "GENERATED ALWAYS AS IDENTITY" par "AUTO_INCREMENT"
--  - Remplacer TIMESTAMP par DATETIME si besoin de dates > 2038
--  - Ajouter ENGINE=InnoDB à la fin de chaque CREATE TABLE
-- =====================================================
