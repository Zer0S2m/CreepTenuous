CREATE TABLE IF NOT EXISTS user_categories
(
    id      SERIAL PRIMARY KEY                              NOT NULL,
    user_id BIGINT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    title   VARCHAR(128)                                    NOT NULL
)