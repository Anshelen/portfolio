# The Portfolio Project

[![Build Status](https://travis-ci.com/Anshelen/portfolio.svg?branch=master)](https://travis-ci.com/Anshelen/portfolio)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

## About

Source code of private site hosted on https://shelenkov.herokuapp.com. 

## Prerequisites

- Java 1.8+
- Maven 3.3+
- PostgreSQL 9.0+

## Installation

You may need to override a few properties. It can be done by setting the
following environment variables:
  * APPLICATION_BASE_URL
  * SPRING_DATASOURCE_URL
  * SPRING_DATASOURCE_PASSWORD
  * SPRING_DATASOURCE_USERNAME
  * SPRING_SENDGRID_API_KEY
  * MAIL_ADMIN_ADDRESS
  * MAIL_ADMIN_NAME

Optionally you can set all these variables in `application.properties`. Launch
locally:
```bash
mvn package -DskipTests
java -jar target/portfolio-*.jar
```

Project can be deployed to Heroku as is.
 

## License

This software is licensed under the [BSD License][BSD]. For more information, read the file [LICENSE](LICENSE).

[BSD]: https://opensource.org/licenses/BSD-3-Clause
