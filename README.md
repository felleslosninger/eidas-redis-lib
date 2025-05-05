# eidas-redis-lib

Deling av data mellom EU software eidas-node (connector/proxy) og ID-porten eidas-connector/proxy gjennom lagring i Redis.

## Opplasting av eu-artifacts til Packages

Last ned EU-software frå https://ec.europa.eu/digital-building-blocks/sites/display/DIGITAL/eIDAS-Node+version+2.9 (velg siste versjon) frå Download (zip).

Pakk ut og finn fram desse artifactane for opplasting til eidas-redis-lib packages:
* eidas-parent-2.9.0.pom
* eidas-commons-2.9.0.jar
* eidas-encryption-2.9.0.jar
* eidas-light-commons-2.9.0.jar
* eidas-saml-engine-2.9.0.jar
* eidas-saml-metadata-2.9.0.jar

NB: Hent for korrekt versjon, døma over er for versjon 2.9.0.

Lag deg eit token på github på din brukar under setting for å ha lov til å create packages.
Legg midlertidig dette tokenet inn i di Maven settings.xml fil: ~/m2/settings.xml under <repository><id>github</id>...

Køyr kommando under for kvar artifact i lista over, stå på rot av gjeldande repository:
´´´
mvn deploy:deploy-file -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=<artifact>
´´´
F.eks. slik:
´´´
mvn deploy:deploy-file -DrepositoryId=github -Durl=https://maven.pkg.github.com/felleslosninger/eidas-redis-lib -Dfile=eu-packages/eidas-saml-metadata-2.9.0.jar
´´´
Det er lagt inn <distributionManagement> i pom.xml i gjeldande repo for å få laste opp til korrekt URL.
