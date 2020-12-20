#!/bin/bash
echo "Compile Script started."
docker run -t -d --name compi gcc-compileworker
docker cp test.c compi:/test.c
docker exec compi gcc -S test.c
docker cp compi:/test.s .
docker stop compi
docker rm compi
# TODO: Set name dynamically with sessionId