#!/bin/bash

docker run -t -d --name temp-ubuntu ubuntu
docker exec temp-ubuntu apt-get update
docker exec temp-ubuntu "$2"
docker commit temp-ubuntu coolpile-"$1"
docker stop temp-ubuntu
docker rm temp-ubuntu
