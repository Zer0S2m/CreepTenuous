ALTER TABLE "user_color_directories"
    DROP COLUMN IF EXISTS color;
ALTER TABLE "user_color_directories"
    ADD user_color_id BIGINT;

ALTER TABLE "user_color_directories"
    ADD CONSTRAINT fk_user_color_directories_user_colors FOREIGN KEY (user_color_id) REFERENCES "user_colors";
