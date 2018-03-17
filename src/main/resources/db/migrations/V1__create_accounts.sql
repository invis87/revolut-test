CREATE TABLE "accounts" (
    "id" BIGINT NOT NULL,
    "name" VARCHAR NOT NULL,
    "amount" BIGINT NULL,
    PRIMARY KEY("id")
);
CREATE UNIQUE INDEX id_idx on "accounts" ("id");
CREATE INDEX name_idx on "accounts" ("name");
