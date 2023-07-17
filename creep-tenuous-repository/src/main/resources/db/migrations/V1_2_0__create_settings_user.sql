CREATE TABLE IF NOT EXISTS "user_settings"
(
    id                       SERIAL PRIMARY KEY            NOT NULL,
    user_id                  BIGINT REFERENCES "user" (id) NOT NULL,
    transferred_user_id      BIGINT                        REFERENCES "user" (id) ON DELETE SET NULL,
    is_deleting_file_objects BOOLEAN DEFAULT FALSE         NOT NULL
)