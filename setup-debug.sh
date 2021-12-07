#!/bin/bash
set -x
# Variables
VERSION="1.16.5"
BUILD_NUMBER="1171"
DOWNLOAD_URL="https://api.pl3x.net/v2/purpur/${VERSION}/${BUILD_NUMBER}/download"
SERVER_JAR="server.jar"
# If debug dir doesn't exist, create it
if [ ! -d "debug" ]; then
    # Create the dir
    mkdir debug
    mkdir debug/plugins
fi
# Download the server jar if not already present
if [ ! -f "debug/${SERVER_JAR}" ]; then
    # Download the jar
    echo "Downloading ${DOWNLOAD_URL}"
    curl -o debug/${SERVER_JAR} ${DOWNLOAD_URL}
fi
# Accept eula if there isn't a file for eula.txt
if [ ! -f "debug/eula.txt" ]; then
    # Download the jar
    echo "Accepting eula"
    echo "eula=true" >debug/eula.txt
fi
# Create a server.properties with max-tick-time=-1
if [ ! -f "debug/server.properties" ]; then
    # Download the jar
    echo "Creating server.properties"
    cat <<EOF >debug/server.properties
max-tick-time=-1
allow-nether=false
motd=Debug Server TSI
EOF
fi
# Create a server.properties with max-tick-time=-1
if [ ! -f "debug/bukkit.yml" ]; then
    # Download the jar
    echo "Creating bukkit.yml"
    cat <<EOF >debug/bukkit.yml
settings:
  allow-end: false
  connection-throttle: -1
worlds:
  world:
    generator: VoidGen:{"biome":"DESERT","mobs":false}
EOF
fi
