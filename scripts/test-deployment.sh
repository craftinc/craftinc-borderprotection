#!/bin/bash

SCRIPT_DIR="$(dirname "$0")"

BUKKIT_DIR="$SCRIPT_DIR/../bukkit-testserver"
PLUGIN_DIR="$SCRIPT_DIR/../bukkit-testserver/plugins"

# TODO: This is a bad solution! Maven should write necessary information into an extra file.
ARTIFACT_ID="$(grep -C5 '<groupId>de.craftinc' "$SCRIPT_DIR/../pom.xml" | grep '<name>' | sed 's/\s*<name>//g' | sed 's/<\/name>\s*//g')"

# TODO: This is a bad solution! Maven should write necessary information into an extra file.
VERSION="$(grep -C5 '<groupId>de.craftinc' "$SCRIPT_DIR/../pom.xml" | grep '<version>' | sed 's/\s*<version>//g' | sed 's/<\/version>\s*//g')"


mkdir -p "$PLUGIN_DIR"

cp "$SCRIPT_DIR/../target/$ARTIFACT_ID $VERSION".jar "$PLUGIN_DIR/"

echo -e "ddidderr\nmice_on_drugs\nMochaccino" > "$BUKKIT_DIR/ops.txt"

"$SCRIPT_DIR/minecraft.sh" reload_or_start
