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

NB: Hent for korrekt versjon, døma over er for versjon `2.9.0`.

Lag deg eit token på github på din brukar under setting for å ha lov til å create packages: https://github.com/settings/tokens med scopes: `repo, write:packages`.
Legg midlertidig dette tokenet inn i di Maven settings.xml fil: `~/m2/settings.xml` under `<servers><server>github</id><password> DITT-PACKAGE_WRITE_TOKEN </password>...`.

Køyr kommando under for kvar av JAR artifact i lista over, stå på rot av gjeldande repository:
```
mvn deploy:deploy-file -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=<artifact>
```
F.eks. slik:
```
mvn deploy:deploy-file -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=eu-packages/eidas-saml-metadata-2.9.0.jar
```
For parent pom.xml må ein truleg også ha med ekstra parameter for pomFile:
```
mvn deploy:deploy-file -DpomFile=eu-packages/eidas-parent-2.9.0.pom -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=eu-packages/eidas-parent-2.9.0.pom
```
Det er lagt inn <distributionManagement> i pom.xml i gjeldande repo for å få laste opp til korrekt URL.

Verifiser at packages med korrekt versjon blir lasta opp hit: https://github.com/orgs/felleslosninger/packages?repo_name=eidas-redis-lib
