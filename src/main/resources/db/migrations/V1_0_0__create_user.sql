CREATE TABLE IF NOT EXISTS "user" (
    id serial PRIMARY KEY NOT NULL,
    login varchar(30) NOT NULL,
    email varchar(100),
    date_of_birth timestamp NOT NULL DEFAULT NOW()
)
