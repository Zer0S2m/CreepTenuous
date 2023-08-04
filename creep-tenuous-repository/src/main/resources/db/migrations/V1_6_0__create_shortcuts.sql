CREATE TABLE IF NOT EXISTS "shortcuts"
(
    id                             SERIAL PRIMARY KEY            NOT NULL,
    user_id                        BIGINT REFERENCES "user" (id) NOT NULL,
    to_attached_file_system_object uuid                          NOT NULL,
    attached_file_system_object    uuid                          NOT NULL
)
