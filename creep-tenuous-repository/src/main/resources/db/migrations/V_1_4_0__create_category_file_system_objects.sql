CREATE TABLE IF NOT EXISTS "category_file_system_objects"
(
    id                 SERIAL PRIMARY KEY                                         NOT NULL,
    user_id            BIGINT REFERENCES "user" (id) ON DELETE CASCADE            NOT NULL,
    user_category_id   BIGINT REFERENCES "user_categories" (id) ON DELETE CASCADE NOT NULL,
    file_system_object uuid                                                       NOT NULL
)
