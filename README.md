# vouchr-demo-auth
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Minimal [Spring Boot](http://projects.spring.io/spring-boot/) based app for generating vouchr jwt credentials.

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Prerequisites

1. Create a private key used for signing your jwt, and a public key that will be shared with Vouchr.  Please do this in
a way that complies with your organization's policies and standards.

For demonstration purposes, using openssl you can create a private key with openssl using:

```
openssl genrsa -out privkey.pem 2048
```

and from that create a public key for distribution with:

```
openssl rsa -in privkey.pem -pubout > key.pub
```

2. Modify `src/main/resources/application.properties` to include

```
vouchr.jwt.key.pem=file:privkey.pem
vouchr.jwt.key.expiry.seconds=900
vouchr.jwt.sub.hash.key=
```

* `vouchr.jwt.key.pem` - the location of your private key stored and protected as per organization policies
* `vouchr.jwt.key.expiry.seconds` - the number of seconds the jwt should remain valid.  This only needs to
span the time from a user being given a link to the vouchr experience and them following the link
* `vouchr.jwt.sub.hash.key` - used to hash the customer id in a way that the customer id remains hidden to the vouchr
system while still preserving an id from one access to the next.  We recommend a randomized string of at least 32 
alphanumeric chracters

3. Choose the most appropriate option between:
  * modify `MyCustomerService.java` to retrieve User Details from a client's bearer token - assuming Bearer Token authentication 
    via the Authorization header
  * modify the standard Spring `SecurityConfig.java` to make sure the user is authorized via some other manner consistent with
    your organization
  * for demonstration purpsoses only, switch `MyCustomerService.IGNORE_TOKEN_RETURN_RANDOM_USER` to `true`  (** not suitable 
  for production use **) 

4. Modify `VouchrJwtService.LOAD_PRIVATE_KEY` to `true` to acknowledge that you're storing and protecting your private
  key in a way consistent with your organization's policies and standards;
  



## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.vouchrtech.demo.auth.AuthApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Retrieve a jwt token

create a POST request to /vouchr/jwt and retrieve your jwt / idToken from the result

```
curl -X POST \
  http://localhost:8080/vouchr/jwt \
  -H 'Authorization: Bearer p9pVVt1LlFI8KYc6HrZBR64MA0M' \
  -H 'cache-control: no-cache'
```

example response:

```json
{
    "idToken": "eyJhbGciOiJQUzI1NiJ9.eyJzdWIiOiJaZy0xbmJjb1Y2dHltcnEyRks2Z285bmoyN2FhRzA0WUlHaFd4cDBUVFl3IiwiZXhwIjoxNjA1MTk4MzAxfQ.eqv9wqRxZcbylCEJyFHNQOdq8HE_ON6A1i0th2d5W5x13RrWWPjEKnk2yt_OuiROVRD5eqBsQd0-ucCn_uYW7Mb3OXUI7z4sHx9PE8PAQqYcQaaSj1uhZckMWWR6Laqq1pqY91YorcjsZje8UJdWcVPOD8_kfv-Nd0E9LWpS6OfpyFLB5XMFFwZ7hEkBipzqrUpSbsmdZ7hI5m2doGY7bJ1YlAQ_Mhi2y80bAlQ5pAGYFxnAGArTVQyibY16V2DC5uN6i8fV1ZEzAqwiXymnTr2ioru4k6Oeif8rI6WaJku0d2rdqz3lS9zqi-ef6x9JL-O_hIwFXJFLAVr7b6-uaQ"
}
```

Now, when constructing the link for the Vouchr create experience, you can add the above token to the urk as #tkn=<idToken> to
grant limited-time access.

## Copyright

Released under the Apache License 2.0. See the [LICENSE](https://github.com/vouchrapp/vouchr-demo-auth/blob/master/LICENSE) file.