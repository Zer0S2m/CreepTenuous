CREATE TABLE IF NOT EXISTS "user_file_objects_exclusions"
(
    id                 SERIAL PRIMARY KEY                              NOT NULL,
    user_id            BIGINT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    file_system_object uuid                                            NOT NULL
)