CREATE TABLE IF NOT EXISTS "user_color_directories"
(
    id        SERIAL PRIMARY KEY                              NOT NULL,
    user_id   BIGINT REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    directory uuid                                            NOT NULL,
    color     VARCHAR(9)                                      NOT NULL
);

comment on column user_color_directories.color is 'Color is specified in HEX format'
