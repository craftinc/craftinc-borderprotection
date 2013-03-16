#!/bin/bash

SCRIPT_DIR=$(readlink -f $(dirname "$0"))

BUKKIT_DIR="$SCRIPT_DIR/../bukkit-testserver"
PLUGIN_DIR="$SCRIPT_DIR/../bukkit-testserver/plugins"

# TODO: This is a bad solution! Maven should write necessary information into an extra file.
ARTIFACT_ID="$(grep -C3 '<groupId>de.craftinc' "$SCRIPT_DIR/../pom.xml" | grep '<artifactId>' | sed 's/\s*<artifactId>//g' | sed 's/<\/artifactId>\s*//g')"

# TODO: This is a bad solution! Maven should write necessary information into an extra file.
VERSION="$(grep -C3 '<groupId>de.craftinc' "$SCRIPT_DIR/../pom.xml" | grep '<version>' | sed 's/\s*<version>//g' | sed 's/<\/version>\s*//g')"


mkdir -p "$PLUGIN_DIR"

cp "$SCRIPT_DIR/../target/$ARTIFACT_ID-$VERSION".jar "$PLUGIN_DIR/$ARTIFACT_ID".jar

echo -e "ddidderr\nmice_on_drugs\nMochaccino" > "$BUKKIT_DIR/ops.txt"

"$SCRIPT_DIR/minecraft.sh" reload_or_start
