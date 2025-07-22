# delta
Course project for EECS-3311 (Software Design) at York University.

## Running the Application
To run the test suite:

```bash
mvn test
```
To run the application:

```bash
mvn clean compile exec:java -Dexec.mainClass="app.AppMainPresenter" -e
```

To run test that simulate CI environment (headless mode) before pushing:

```bash
mvn clean test -B -Djava.awt.headless=true
```

