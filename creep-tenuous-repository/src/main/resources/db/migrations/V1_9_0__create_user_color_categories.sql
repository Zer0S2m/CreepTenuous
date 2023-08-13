CREATE TABLE IF NOT EXISTS "user_color_categories"
(
    id               SERIAL PRIMARY KEY                                         NOT NULL,
    user_id          BIGINT REFERENCES "user" (id) ON DELETE CASCADE            NOT NULL,
    user_color_id    BIGINT REFERENCES "user_colors" (id) ON DELETE CASCADE     NOT NULL,
    user_category_id BIGINT REFERENCES "user_categories" (id) ON DELETE CASCADE NOT NULL
)
