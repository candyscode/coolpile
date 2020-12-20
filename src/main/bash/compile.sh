#!/bin/bash
SESSION_ID="$1"

docker run -t -d --name "$SESSION_ID" gcc-compileworker
docker cp "$SESSION_ID".c "$SESSION_ID":/"$SESSION_ID".c
docker exec "$SESSION_ID" gcc -S "$SESSION_ID".c
docker cp "$SESSION_ID":/"$SESSION_ID".s .
docker stop "$SESSION_ID"
docker rm "$SESSION_ID"