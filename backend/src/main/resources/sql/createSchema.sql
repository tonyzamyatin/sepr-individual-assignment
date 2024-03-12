CREATE TABLE IF NOT EXISTS breed
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS horse
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255)            NOT NULL,
    -- Instead of an ENUM (H2 specific) this could also be done with a character string type and a check constraint.
    sex           ENUM ('MALE', 'FEMALE') NOT NULL,
    date_of_birth DATE                    NOT NULL,
    height        NUMERIC(4, 2)           NOT NULL,
    weight        NUMERIC(7, 2)           NOT NULL,
    breed_id      BIGINT,
    FOREIGN KEY (breed_id) REFERENCES breed (id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tournament
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    start_date DATE         NOT NULL,
    end_date   DATE         NOT NULL,
    horse1     BIGINT,
    CHECK (start_date <= end_date)
);

CREATE TABLE IF NOT EXISTS tournament_participant
(
    tournament_id BIGINT,
    horse_id      BIGINT,
    PRIMARY KEY (tournament_id, horse_id),
    FOREIGN KEY (tournament_id) REFERENCES tournament (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (horse_id) REFERENCES horse (id) ON UPDATE CASCADE -- participating horses must not be deleted
);

CREATE TABLE IF NOT EXISTS tournament_match
(
    match_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id   BIGINT,
    horse1_id       BIGINT,
    horse2_id       BIGINT,
    round_number    INT,
    match_number    INT,
    winner_horse_id BIGINT NULL,                                     -- Can be NULL if the match has not been played yet
    FOREIGN KEY (tournament_id) REFERENCES tournament (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (horse1_id) REFERENCES horse (id) ON UPDATE CASCADE, -- participating horses must not be deleted
    FOREIGN KEY (horse2_id) REFERENCES horse (id) ON UPDATE CASCADE, -- participating horses must not be deleted
    CHECK (round_number > 0),
    CHECK (match_number > 0),
    CHECK (
        winner_horse_id IS NULL OR
        winner_horse_id = horse1_id OR
        winner_horse_id = horse2_id
        ),
    UNIQUE (tournament_id, round_number, match_number)
);


