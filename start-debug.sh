#!/bin/bash
# Variables
SERVER_JAR="server.jar"
DEBUG_PORT=5005
PLUGINS_DIR="plugins"
WORLDS_DIR="../worlds"
MAX_HEAP="4096M"
INIT_HEAP="1024M"
set -e
# If debug dir doesn't exist, create it
if [ ! -d "debug" ]; then
    # Create the dir
    mkdir debug
fi
# Set the current dir to the debug dir
cd debug
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$DEBUG_PORT \
    -Xmx$MAX_HEAP -Xms$INIT_HEAP -jar $SERVER_JAR \
    --plugins $PLUGINS_DIR \
    --universe $WORLDS_DIR nogui
