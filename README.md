# Booking Service


## Summary
This is a service for managing bookings and blocks for properties. The api is intended to be used internally. The service
is built on SpringWebFlux

## Running the service locally
* First compile and run the tests: `./mvnw test`
* Run with  `./mvnw spring-boot:run`

## API
Run the service and see: http://localhost:8080/swagger-ui.html 

## Database
The service uses H2 in memory database. You can preview the items while the service
is running from  http://localhost:8080/h2-console When visiting for the first time set
the JDBC url to be the same as in `application.properties` file.