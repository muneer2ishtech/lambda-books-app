# lambda-books-app

Single Java 17 Maven project containing multiple AWS Lambda handlers for DynamoDB `books` table.

Package: `fi.ishtech.practice.bookapp.lambda`

Handlers:
- CreateBookHandler
- FindBookByIdHandler
- FindAllBooksHandler
- UpdateBookHandler
- DeleteBookByIdHandler
- FindByConditionsHandler

## Build

```bash
mvn clean package
```

target/lambda-books-app.jar (shaded) will be produced

## Deploy

### Using AWS SAM (Serverless Application Model)

- `awscli` and `aws-sam-cli` already installed

```
sam build
sam deploy --guided
```

- If you need to use aws profile

```
sam deploy --guided --profile muneer2ishtech
```


### Using AWS Console

- See [AWS Console - Create Lambda](AWS-CONSOLE-LAMBDA.md)


Upload the JAR to AWS Lambda (Upload from .zip or .jar). Set handler for each function, e.g.:
`fi.ishtech.practice.bookapp.lambda.CreateBookHandler::handleRequest`

Environment variables (set in Lambda configuration):
- BOOK_TABLE = books
- AWS_REGION = <your-region>

Notes:
- Uses AWS SDK v2 (DynamoDB).
- This project places all common code (model + Dynamo client) in same artifact so you can upload a single JAR and create multiple Lambda functions referencing different handlers.
