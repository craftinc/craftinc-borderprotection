#!/bin/bash

# Craft Inc. BorderProtection
# Copyright (C) 2016  Paul Schulze
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

SCRIPT_DIR="$(dirname "$0")"

DEVELOPER="$(whoami)"

BUKKIT_DIR="$SCRIPT_DIR/../bukkit-testserver"
PLUGIN_DIR="$SCRIPT_DIR/../bukkit-testserver/plugins"
START_STOP_SCRIPT="$SCRIPT_DIR/minecraft.sh"
BUILD_TOOLS_DIR="$BUKKIT_DIR/buildtools"

mkdir -p "$BUILD_TOOLS_DIR"
# get build tools
if [ ! -f "$BUILD_TOOLS_DIR/BuildTools.jar" ]; then
    wget -O- 'https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar' > "$BUILD_TOOLS_DIR/BuildTools.jar"
fi

cp -f "$BUILD_TOOLS_DIR"/craftbukkit*.jar "$BUKKIT_DIR/"

# run build tools
if [ ! -f "$BUKKIT_DIR"/craftbukkit*.jar ]; then
    cd "$BUILD_TOOLS_DIR"
    java -jar ./BuildTools.jar
fi

echo 'eula=TRUE' > "$BUKKIT_DIR/eula.txt"

# TODO: This is a bad solution! Maven should write necessary information into an extra file.
ARTIFACT_ID="$(grep -C5 '<groupId>de.craftinc' "$SCRIPT_DIR/../pom.xml" | grep '<name>' | sed 's/[ \t]*<name>//g' | sed 's/<\/name>[ \t]*//g')"

# TODO: This is a bad solution! Maven should write necessary information into an extra file.
VERSION="$(grep -C5 '<groupId>de.craftinc' "$SCRIPT_DIR/../pom.xml" | grep '<version>' | sed 's/[ \t]*<version>//g' | sed 's/<\/version>[ \t]*//g')"

rm -rf "$PLUGIN_DIR"
mkdir -p "$PLUGIN_DIR"

cp "$SCRIPT_DIR/../target/$ARTIFACT_ID-$VERSION".jar "$PLUGIN_DIR/"

echo -e "ddidderr\nmice_on_drugs\nMochaccino" > "$BUKKIT_DIR/ops.txt"

$START_STOP_SCRIPT restart_or_start
