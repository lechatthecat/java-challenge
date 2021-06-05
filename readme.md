### How to use this java-challenge project

Prerequisites
- Java1.8
- Maven 3.*
- Docker 20.10.6
- Docker-compose 1.29.1

#### How to Run this project
```
$ git clone https://github.com/lechatthecat/java-challenge
$ cd ./java-challenge
$ docker-compose up -d --build
$ docker ps -a
$ mvn clean package
$ mvn run
```

Now the project should be running locally.
Application (with MariaDB container) is ready to be used.

You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui/#/

To connect to the MariaDB contaier, please use the following information:
- host: localhost
- port: 3306
- user id: root
- password: root
- database name: testdb

#### Enhancements I did
- Replaced H2 mem DB with MariaDB running on Docker container.
  - H2 DB is not supposed to be used as a main DB in production, but MariaDB and Docker can be used as a main DB in both development and production.
  - I used Docker not to reduce the project portability. H2 DB can be easily moved to other environment, but MariaDB with Docker also can be easily moved.
  - If this system were used in production, when traffic grows, MariaDB/Docker would provide better performance.
- Changed Swagger/Swagger-UI version to 3.0.0.
  - My IDE shows vulnerability warning on ver 2.9.xx, but ver 2.10.2 is broken version according to GitHub issue, so I changed it to ver 3.0.0.
  - Replaced some other dependencies' versions that can have vulnerability.
- Added validation for user input. When you pass wrong value to the endpoints, they will return validation messages.
  - Validation is performed for both of `pathVariable`s and `Employee` json passed for all endpoints.
- Removed bugs.
  - "basic error controller" was removed from swagger-ui.
  - `getEmployee(Long employeeId)` method of `EmoloyeeService` returns `Optional<Employee>`, but controller was checking whether value/null was returned from `getEmployee(Long employeeId)`, which is meaningless because this method always returns `Optional<Employee>` even if the method doesn't find any row by the specified `employeeId`.   
- Added 6 Unit tests.
- Added comments.
- Added simple caching logic for database calls

#### Enhancements I would do if I have more time
- In DB definition, "salary" column of "EMPLOYEE" table is defined as `VarChar(255)`, which is strange. I think `Int` or `BigInt` would be better. If I had more time, I would change the column definition.
- If I had more time, I would have added more detailed caching logic (evicts only updated caches).
- I would add more unit tests and increase the coverage of unit tests.

### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions
- use java 8


#### What we will look for
- Readability of your code
- Documentation
- Comments in your code 
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### Your experience in Java

Please let us know more about your Java experience in a few sentences. For example:

- I have 3 years experience in Java and I started to use Spring Boot from last year
- I'm a beginner and just recently learned Spring Boot
- I know Spring Boot very well and have been using it for many years

#### My Experience in Java
- I have 3 years experience in Java and I started to use Spring Boot 2 years ago
