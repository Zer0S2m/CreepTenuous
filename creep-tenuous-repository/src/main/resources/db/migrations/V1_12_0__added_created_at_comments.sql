ALTER TABLE "comments"
    ADD COLUMN created_at timestamp default now() NOT NULL;
