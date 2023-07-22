CREATE TABLE IF NOT EXISTS "user_color_categories"
(
    id                 SERIAL PRIMARY KEY                              NOT NULL,
    user_id            BIGINT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    file_system_object uuid                                            NOT NULL,
    color              VARCHAR(9)                                      NOT NULL
);

comment on column user_color_categories.color is 'Color is specified in HEX format'
