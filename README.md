# eidas-redis-lib

Sharing of data between EU software eidas-node (connector/proxy) and ID-porten eidas-connector/proxy via storage in Redis.

## Manual upgrade of dependencies
Dependencies must be upgraded manually since they have to match with version used in EU eIDAS-node, and thus must be upgraded carefully.
eidas-redis-lib also uses other 3.party dependencies that is not in use in EU eIDAS-node, these can be upgraded freely.

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
