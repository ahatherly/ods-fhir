# Introduction

Simple test FHIR endpoint for NHS Organisation data. This service used the HAPI ReSTful server libraries to expose a STU3 FHIR endpoint for Organization resources, backed off on data from the Organisation Data Service (ODS).

## Docker Quickstart Guide

You will need Docker, a recent JDK and Maven installed on your machine to run these steps. This test server re-uses the work done in the OpenODS project, which takes the ODS data file and inserts it into a postgres database. The first step therefore is to clone the OpenODS repository and create a docker container for Postgres, and insert the ODS data into it:

```
cd ~
wget https://s3.amazonaws.com/openods-assets/database_backups/openods_014.dump
git clone https://github.com/open-ods/open-ods.git
cd open-ods/Docker
./deploy-postgres.sh
./import-data.sh ~/openods_014.dump
```

You should now have a postgres container running, with all the ODS data in it - check as follows:

```
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
31c66121a2e3        postgres:9.6        "docker-entrypoint.sh"   47 minutes ago      Up 47 minutes       5432/tcp                 openods-postgres
```

Now you can install the FHIR server endpoint. Go to the root of this project directory, and compile the code:

```
mvn install
```

Now, create and deploy the container:

```
cd Docker
./build.sh
./deploy.sh
```

Assuming it worked, you should now have two containers running:

```
adam@eat-development:~/WorkspaceDeveloperNetwork/ods-fhir/Docker$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
dc0791d68ef2        ods-fhir            "/docker-entrypoin..."   12 minutes ago      Up 12 minutes       0.0.0.0:8085->8080/tcp   ods-fhir
31c66121a2e3        postgres:9.6        "docker-entrypoint.sh"   47 minutes ago      Up 47 minutes       5432/tcp                 openods-postgres
```

You should now be able to run FHIR calls against http://localhost:8085. For example, try:

```
curl http://localhost:8085/Organization/X09
curl http://localhost:8085/Organization/X09?_format=json
curl http://localhost:8085/Organization?name:contains=CLECKHEATON
curl http://localhost:8085/Organization?name:exact=ASHCROFT+NH+\(CLECKHEATON\)
```

