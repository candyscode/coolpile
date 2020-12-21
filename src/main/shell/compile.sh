#!/bin/bash
SESSION_ID="$1"
COMMAND="$3"
PARAMS="$4"
INPUTSUFFIX="$5"
OUTPUTSUFFIX="$6"

docker run -t -d --name "$SESSION_ID" coolpile-"$2"
docker cp "$SESSION_ID""$INPUTSUFFIX" "$SESSION_ID":/"$SESSION_ID""$INPUTSUFFIX"
docker exec "$SESSION_ID" "$COMMAND" "$PARAMS" "$SESSION_ID""$INPUTSUFFIX"
docker cp "$SESSION_ID":/"$SESSION_ID""$OUTPUTSUFFIX" .
docker stop "$SESSION_ID"
docker rm "$SESSION_ID"
