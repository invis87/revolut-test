CREATE TABLE "accounts" (
    "id" SERIAL NOT NULL,
    "name" VARCHAR NOT NULL,
    "balance" BIGINT NULL,
    PRIMARY KEY("id")
);
CREATE UNIQUE INDEX id_idx on "accounts" ("id");
CREATE UNIQUE INDEX name_idx on "accounts" ("name");
