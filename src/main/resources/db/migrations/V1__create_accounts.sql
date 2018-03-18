CREATE TABLE "accounts" (
    "id" SERIAL NOT NULL,
    "name" VARCHAR NOT NULL,
    "balance" BIGINT NOT NULL,
    PRIMARY KEY("id")
);
CREATE UNIQUE INDEX accounts_name_idx on "accounts" ("name");
