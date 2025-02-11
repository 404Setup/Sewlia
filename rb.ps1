#!/usr/bin/env pwsh

gradle fixupPaperApiFilePatches
gradle fixupPaperServerFilePatches
gradle fixupMinecraftSourcePatches
gradle rebuildFoliaPatches || exit_on_error "An error occurred when rebuilding patches!"
gradle rebuildServerPatches || exit_on_error "An error occurred when rebuilding patches!"
gradle rebuildMinecraftPatches || exit_on_error "An error occurred when rebuilding patches!"