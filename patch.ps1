#!/usr/bin/env pwsh

gradle applyAllPatches || exit_on_error "An error occurred when merging patches!"