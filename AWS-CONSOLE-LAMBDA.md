## Upload to AWS Lambda (Console)

- Need **lambda-books-app.jar**, see [Build](./README.md#Build)

- For `CreateBookHandler`
    1. Go to AWS Console → Lambda → Create function
    2. Choose *Author from scratch*
        - Function Name: lambda-books-create
        - Runtime: Java 17
        - Architecture: x86_64
    3. Click Create function
    4. After creation → open Code tab → Upload from → .zip or .jar file
        - Upload target/lambda-books-app.jar
    5. Under Runtime settings → Edit handler, set:
        - `fi.ishtech.practice.bookapp.lambda.CreateBookHandler::handleRequest`
    6. Add environment variables:
        - `BOOK_TABLE = books`
        - `AWS_REGION = eu-west-1`
    7. Save and Test

- Repeat steps 1–7 for others (just change function name + handler):

| Lambda Function Name | Handler |
|----------------------|---------|
| lambda-books-findById | fi.ishtech.practice.bookapp.lambda.FindBookByIdHandler::handleRequest |
| lambda-books-findAll | fi.ishtech.practice.bookapp.lambda.FindAllBooksHandler::handleRequest |
| lambda-books-update | fi.ishtech.practice.bookapp.lambda.UpdateBookHandler::handleRequest |
| lambda-books-deleteById | fi.ishtech.practice.bookapp.lambda.DeleteBookByIdHandler::handleRequest |
| lambda-books-findByConditions | fi.ishtech.practice.bookapp.lambda.FindByConditionsHandler::handleRequest |
