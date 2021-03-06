# Dockerfile to run my WAR file
# This uses a base image from jetty, which can run war files...
FROM jetty:9.3.12-jre8-alpine

ENV CONFIG_FILE ""

# Adds war into the image
ADD ./target/ods-fhir-0.0.1-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.war

# Create a non-root user for our FHIR server to run as
RUN adduser -D -u 1000 fhir

# Says that when it runs, it's port 8080 needs to be available
EXPOSE 8080

# Volume to store profiles that the server will serve up
VOLUME /opt/fhir

USER fhir

