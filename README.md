# eidas-redis-lib

This library exists to keep down to date with the EU software

## How to upgrade

1. Get the latest EU software from [here](https://ec.europa.eu/digital-building-blocks/sites/display/DIGITAL/eIDAS-Node+Integration+Package)
2. Unzip the package
3. Compare the versions of libs and java used in the eu software and upgrade the pom-file in this project accordingly
4. skriv mvn install i eidas parent katalogen
5. install eidas libs locally  ``` mvn install:install-file -Dfile=eidas-commons-2.9.0.jar -DgroupId=eu.eidas -DartifactID=eidas-commons -Dversion=2.9.0 -Dpackaging=jar```
6. check locally with DEV-SNAPSHOT if it works (see e.g. eidas-idporten-connector for how to run locally)
6. And in github packages when ok and make a new release
