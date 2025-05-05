# eidas-redis-lib

Sharing of data between EU software eidas-node (connector/proxy) and ID-porten eidas-connector/proxy via storage in Redis.

## Manual upgrade of dependencies
Dependencies must be upgraded manually since they have to match with version used in EU eIDAS-node, and thus must be upgraded carefully.
eidas-redis-lib also uses other 3.party dependencies that is not in use in EU eIDAS-node, these can be upgraded freely.

## How to upgrade

1. Get the latest EU software from [here](https://ec.europa.eu/digital-building-blocks/sites/display/DIGITAL/eIDAS-Node+Integration+Package)
2. Unzip the package
3. Compare the versions of libs and java used in the eu software and upgrade the pom-file in this project accordingly
4. Run the command mvn install in eidas parent directory
5. install eidas libs locally  ``` mvn install:install-file -Dfile=eidas-commons-2.9.0.jar -DgroupId=eu.eidas -DartifactID=eidas-commons -Dversion=2.9.0 -Dpackaging=jar```
6. check locally with DEV-SNAPSHOT if it works (see e.g. eidas-idporten-connector for how to run locally
7. And in github packages when ok and make a new release. See next section

## Upload of EU-artifacts to Packages

Download EU-software from https://ec.europa.eu/digital-building-blocks/sites/display/DIGITAL/eIDAS-Node+version+2.9 (velg siste versjon), use button "Download" (zip).

Unpack and find these artifacts for upload to eidas-redis-lib packages:
* eidas-parent-2.9.0.pom
* eidas-commons-2.9.0.jar
* eidas-encryption-2.9.0.jar
* eidas-light-commons-2.9.0.jar
* eidas-saml-engine-2.9.0.jar
* eidas-saml-metadata-2.9.0.jar

NB: Fetch the correct version, example above is for version `2.9.0`.

Create a token on Github for your user under setting with the correct accesses to create packages: https://github.com/settings/tokens with scopes: `repo, write:packages`.
Temporary add this token to your Maven settings.xml file: `~/m2/settings.xml` under `<servers><server>github</id><password> YOUR-PACKAGE_WRITE_TOKEN </password>...`.

Run command below for each of the JAR artifacts in the list above (stand in the root of the current repository):
```
mvn deploy:deploy-file -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=<artifact>
```
E.g. like this:
```
mvn deploy:deploy-file -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=eu-packages/eidas-saml-metadata-2.9.0.jar
```
For parent pom.xml you must propably also add an extra parameter for pomFile:
```
mvn deploy:deploy-file -DpomFile=eu-packages/eidas-parent-2.9.0.pom -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=eu-packages/eidas-parent-2.9.0.pom
```
You do not need to add distributionManagement to the command since this is already added to pom.xml in this repo.

Verify that the packages with correct version are uploaded: https://github.com/orgs/felleslosninger/packages?repo_name=eidas-redis-lib

This library exists to keep "down to date" with the EU software

