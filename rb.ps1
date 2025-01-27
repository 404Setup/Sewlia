./gradlew fixupPaperApiFilePatches
./gradlew fixupPaperServerFilePatches
./gradlew fixupMinecraftSourcePatches
./gradlew rebuildFoliaPatches || exit_on_error "An error occurred when rebuilding patches!"
./gradlew rebuildServerPatches || exit_on_error "An error occurred when rebuilding patches!"
./gradlew rebuildMinecraftPatches || exit_on_error "An error occurred when rebuilding patches!"