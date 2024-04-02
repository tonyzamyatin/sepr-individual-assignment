CREATE TABLE IF NOT EXISTS breed
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS horse
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255)       NOT NULL,
    -- Instead of an ENUM (H2 specific) this could also be done with a character string type and a check constraint.
    sex           ENUM ('MALE', 'FEMALE') NOT NULL,
    date_of_birth DATE                    NOT NULL,
    height        NUMERIC(3, 2)           NOT NULL,
    weight        NUMERIC(5, 2)           NOT NULL,
    breed_id      BIGINT,
    FOREIGN KEY (breed_id) REFERENCES breed (id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tournament
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL, -- Stores up to 255 BYTES of characters (only for 8 byte encodings 255 chars)
    start_date   DATE              NOT NULL,
    end_date     DATE              NOT NULL,
    CHECK (start_date <= end_date)
);


CREATE TABLE IF NOT EXISTS participant
(
    tournament_id BIGINT NOT NULL,
    horse_id BIGINT NOT NULL,
    entry_number INT NOT NULL,
    round_reached INT NOT NULL,
    PRIMARY KEY (tournament_id, horse_id),
    FOREIGN KEY (tournament_id) REFERENCES tournament (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (horse_id) REFERENCES horse (id) ON UPDATE CASCADE ON DELETE CASCADE
);

