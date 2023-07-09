CREATE TABLE IF NOT EXISTS "comments"
(
    id                 SERIAL PRIMARY KEY            NOT NULL,
    comment            VARCHAR(255),
    user_id            BIGINT REFERENCES "user" (id) NOT NULL,
    file_system_object uuid                          NOT NULL
)