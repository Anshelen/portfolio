# The Portfolio Project

[![Build Status](https://travis-ci.com/Anshelen/portfolio.svg?branch=master)](https://travis-ci.com/Anshelen/portfolio)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

## About

Private portfolio site hosted on https://shelenkov.site. 

## Features

Registration

- Registration flow with email confirmation
- Option to resend confirmation email
- 'View in a browser' link for email confirmation message
- Register and login with GitHub or Google (OAuth2 support)

Security

- Support for security headers: CORS, HSTS, CSP, Referrer-Policy, Feature-Policy
- SameSite=Lax and secured cookies
- Remember Me tokens are saved in DB
- Spring Actuator with secured endpoints
- Improved logging for access denied attempts
- CSRF protection
- Hierarchical roles
- Restriction on number of user sessions
- Protection against brute force authentication attempts
- Security tests

Other

- Handmade ugly but adaptive design
- Multi language support
- Custom logic of initial language selection
- JS-files templating with Thymeleaf for localizing frontend validation messages
- Spring Boot + bootpag.js pagination
- Send emails via [Sendgrid][SENDGRID]
- Download resume in different formats and languages
- Customize Hibernate to save set of roles as an array of PostgreSQL enum values
- Integration tests using Testcontainers
- Testing with Travis CI, automatic deploy to Heroku
- SSL via Cloudflare

## Technologies

- Spring Boot, MVC, Data JPA, Security, OAuth2 Client, Actuator, Testing
- PostgreSQL, Hibernate, Flyway
- Thymeleaf
- HTML, CSS, JS, jQuery, Bootstrap 4
- Lombok
- JUnit, Mockito, Testcontainers
- Maven, Travis, Heroku, CloudFlare

## Prerequisites

- Java 1.8+
- PostgreSQL 9.0+

## Installation

You may need to override a few properties. It can be done by setting the
following environment variables:

  Overall

  * APPLICATION_BASE_URL
  
  Security
  
  * SECURITY_REMEMBER_ME_KEY
  * SECURITY_REMEMBER_ME_SECURE
  * SECURITY_HEADERS_ACCESS_CONTROL_ALLOW_ORIGIN
  * SERVER_SERVLET_SESSION_COOKIE_SECURE
  
  OAuth2
  
  * SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID
  * SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET
  * SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID
  * SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET
  
  Database
  
  * SPRING_DATASOURCE_URL
  * SPRING_DATASOURCE_PASSWORD
  * SPRING_DATASOURCE_USERNAME
  
    or
    
  * DATABASE_URL
  
  Mail
  
  * SPRING_SENDGRID_API_KEY
  * MAIL_ADMIN_ADDRESS
  * MAIL_ADMIN_NAME

Optionally you can set all these variables in `application.properties`. 

Launch locally:
```bash
./mvnw package -DskipTests
java -jar target/portfolio-*.jar
```

Project can be deployed to Heroku as is.
 

## License

This software is licensed under the [BSD License][BSD]. For more information, read the file [LICENSE](LICENSE).

[BSD]: https://opensource.org/licenses/BSD-3-Clause
[SENDGRID]: https://sendgrid.com/
