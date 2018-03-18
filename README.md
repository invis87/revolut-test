# Revolut Test Task
Akka-http realisation of RESTful API for money transfers between accounts.

##### API:
1. GET **/accounts**: get all accounts
2. POST **/accounts**: trying to create new account, format: `{"name": "account_name", "balance": 43}`
3. GET **/transactions**: get all transaction
4. POST **/transfer**: trying to proceed transfer between accounts, format: `{"from": 1, "to": 2, "amount": 43}`


## Setup
To start the program you should have `sbt` and scala with java installed.

## Run (from SBT)
`sbt "run"` it will start the server on localhost:19099

## Run (from JAR)
It is possible to create Fat Jar to run service without SBT. To do it execute `sbt assembly` command. It will create `revolut-test-{VERSION}-jar-with-dependencies.jar` file in `target/scala-2.12/` directory.
To start the server: `java -jar {path-to-jar}`

## Test
To start tests execute `sbt test` in root project folder

## Other
- I add some test data to Database migrations scripts, so 2 accounts ready for your tests.
- In `application.conf` you will find all application settings.

## Request examples
- get all accounts: `curl -X GET localhost:19099/accounts`
- create account: `curl -X POST -H "Content-Type: application/json" --data "{\"name\": \"ChosenOne\", \"balance\": 99999}" localhost:19099/accounts`
- create another account: `curl -X POST -H "Content-Type: application/json" --data "{\"name\": \"ChosenSecondOne\", \"balance\": 0}" localhost:19099/accounts`
- send funds to **ChosenSecondOne**(id=4, because 2 accounts were created by sql script) `curl -X POST -H "Content-Type: application/json" --data "{\"from\": 3, \"to\": 4, \"amount\": 1000}" localhost:19099/transfer`
- get all transactions: `curl -X GET localhost:19099/transactions`
- check accounts balance: `curl -X GET localhost:19099/accounts`