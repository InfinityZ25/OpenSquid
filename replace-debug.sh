#!/bin/bash
set -e
# Compile and move file
./gradlew build
cp build/libs/*-all.jar debug/plugins/
