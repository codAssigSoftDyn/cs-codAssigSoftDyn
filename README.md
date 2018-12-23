# cs-codAssigSoftDyn

# to test:
./gradlew clean test

result: build/reports/tests/test/index.html


# to generate test coverage:
./gradlew clean build jacocoTestReport

result: build/jacocoHtml/index.html


# to run:
./gradlew clean bootRun --args='<path_to_file>'
example of path_to_file: C:\work\something\events.json

result: hsqldb "event_db" is available in the working directory
