CREATE TABLE "transactions" (
    "id" SERIAL NOT NULL,
    "from" BIGINT NOT NULL,
    "to" BIGINT NOT NULL,
    "amount" BIGINT NOT NULL,
    PRIMARY KEY("id"),
    FOREIGN KEY ("from") REFERENCES "accounts" ("id"),
    FOREIGN KEY ("to") REFERENCES "accounts" ("id")
);
CREATE INDEX transactions_from_idx on "transactions" ("from");
CREATE INDEX transactions_to_idx on "transactions" ("to");