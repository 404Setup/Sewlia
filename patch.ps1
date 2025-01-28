#!/usr/bin/env pwsh
./gradlew applyAllPatches || exit_on_error "An error occurred when merging patches!"