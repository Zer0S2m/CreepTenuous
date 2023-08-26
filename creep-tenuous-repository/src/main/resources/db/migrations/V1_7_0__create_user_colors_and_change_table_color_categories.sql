CREATE TABLE IF NOT EXISTS "user_colors"
(
    id      SERIAL PRIMARY KEY                              NOT NULL,
    user_id BIGINT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    color   VARCHAR(9)                                      NOT NULL
);
