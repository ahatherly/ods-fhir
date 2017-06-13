#!/bin/bash

# Usage:
# build.sh registryhostname targethostname tagname

REGISTRY_HOST=$1
TARGET_HOST=$2
PORT=${PORT:-8085}
TAG_NAME=$3

IMAGE_NAME=ods-fhir
CONTAINER_NAME=${CONTAINER_NAME:-ods-fhir}
DATA_CONTAINER=${DATA_CONTAINER:-openods-postgres}

if [ ! -z $TAG_NAME ]
then
  IMAGE_NAME="$IMAGE_NAME:$TAG_NAME"
fi

if [ -z $TARGET_HOST ]
then
  TARGET_PREFIX=""
else
  TARGET_PREFIX="--tlsverify -H $TARGET_HOST:2376"
fi

if [ -z $REGISTRY_HOST ]
then
  REGISTRY_PREFIX=""
  SOURCE=$IMAGE_NAME
else
  REGISTRY_PREFIX="--tlsverify -H $REGISTRY_HOST:2376"
  SOURCE=$REGISTRY_HOST:5000/$IMAGE_NAME
fi

MEMORYFLAG=2g
CPUFLAG=768

echo "Pull and run ODS endpoint server"
if [ ! -z $REGISTRY_HOST ]
then
  docker $TARGET_PREFIX pull $SOURCE
fi

docker $TARGET_PREFIX stop $CONTAINER_NAME
docker $TARGET_PREFIX rm $CONTAINER_NAME
docker $TARGET_PREFIX run -p $PORT:8080 --name $CONTAINER_NAME \
	--restart=always \
	--link $DATA_CONTAINER:$DATA_CONTAINER \
        -m $MEMORYFLAG \
	-c $CPUFLAG \
	-v /docker-data/ods-fhir-temp:/tmp/jetty \
	-d $SOURCE


