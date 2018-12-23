# cs-codAssigSoftDyn
to test:
./gradlew clean test

result: build/reports/tests/test/index.html


to generate test coverage:
./gradlew clean build jacocoTestReport

result: build/jacocoHtml/index.html


to run:
./gradlew clean bootRun --args='C:\Work\pe\fun\eventsmid.json'

result: hsqldb "event_db" is available in the working directory
