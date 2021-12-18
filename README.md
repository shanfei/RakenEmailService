# RakenEmailService
RakenEmailService

* use async process for enrichment and send

API endpoints: 
1. post for create email and send
curl --location --request POST 'http://localhost:8080/api/v1/email/?enrich=true' \
--header 'Content-Type: application/json' \
--data-raw '{
    "from":"aa@gmail.com",
    "to": "bb@gmail.com",
    "subject": "test",
    "body": "test"
}'

2. get email with status
curl --location --request GET 'http://localhost:8080/api/v1/email/1'



more improvements on next sprint
1. add spring security for Authentication and Authorization
2. add api access limit
3. add more unit test for coverage
4. add cc/bcc support
