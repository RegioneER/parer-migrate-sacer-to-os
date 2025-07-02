<img src="src/docs/quarkus.png" width="300">
<br/><br/>

# Migrazione blob/clob SACER verso object storage


## Pre-requisito

Installazione wrapper maven, attraverso il seguente comando:

```shell script
mvn wrapper:wrapper
```
## Esposizione applicazione in DEV mode 

Data la seguente configurazione: 

```bash
quarkus.http.root-path = /migrate-sacer-to-os
```

l'applicazione (in modalità dev, vedi [paragrafo](#running-the-application-in-dev-mode)) viene esposta al seguente indirizzo (locale): ``http://localhost:10005/migrate-sacer-to-os``.


## Requisiti per lo start dell'applicazione

Per effettuare lo start dell'applicazione occorrono i seguenti parametri (chiave=valore) i quali potranno essere configurati con le modalità descritti nella seguente guida https://quarkus.io/guides/config-reference.

Connessione al database OracleDB schema SACER:

- SACER_JDBC_URL=${url database}
- SACER_USERNAME=${username}
- SACER_PASSWORD=${password}

Parmetri (base) di configurazione client S3:

- QUARKUS_S3_ENDPOINT_OVERRIDE=${host uri}
- QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_ACCESS_KEY_ID=${access key}
- QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_SECRET_ACCESS_KEY=${secret key}
- SIP_BUCKET_NAME=${bucket}
- COMP_BUCKET_NAME=${bucket}

parametri custom legati ad S3: 

- TENANT_NAME=${tenant} (SACER_SVIL / SACER_TEST / SACER_PRE / SACER_PROD)
- BACKEND_NAME=${backen name} (OBJECT_STORAGE_PRIMARIO / OBJECT_STORAGE_SECONDARIO)



In particolare, si consiglia di creare un file <strong>.env</strong> nella directory root di progetto, di seguito un esempio:

```
_DEV_SACER_JDBC_URL=jdbc:oracle:thin:@<host>:<port>/<service>
_DEV_SACER_USERNAME=<user>
_DEV_SACER_PASSWORD=<password>
_DEV_QUARKUS_S3_ENDPOINT_OVERRIDE=https://minio-sid-parer.regione.emilia-romagna.it:9004
_DEV_QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_ACCESS_KEY_ID=<access-key>
_DEV_QUARKUS_S3_AWS_CREDENTIALS_STATIC_PROVIDER_SECRET_ACCESS_KEY=<secret-key>
_DEV_SIP_BUCKET_NAME=sip-svil
_DEV_COMP_BUCKET_NAME=componenti-svil
_DEV_TENANT_NAME=SACER_SVIL
_DEV_BACKEND_NAME=OBJECT_STORAGE_SECONDARIO

```

da notare che la parte legata ai datasource viene profilata attraverso i due profili standard dev e test (vedi <strong>_DEV</strong>_config).



## Documentazione da README originale (lingua inglese)



This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/migrate-os-metadata-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and Jakarta Persistence
- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)



### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
