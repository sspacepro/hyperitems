# HyperItems

HyperItems is a Minecraft (Paper) server plugin focused on procedurally-generated or custom dungeon content, custom items, and MythicMobs integration. This repository contains the plugin source code, resource files (items, weapons, armors, entity element mappings), and build configuration for packaging a deployable plugin JAR.

# [License](./LICENSE)
Copyright (C) 2025 Galaxy Studios
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

Quick overview
- Java 21-based Paper plugin
- Integrates with MythicMobs and uses Foundation (by kangarko)
- Loads custom items, weapons, armors and entity element configurations from YAML resources
- Registers several listeners for mob spawn/killed events and player/item interactions

Repository layout
- pom.xml - Maven build file (targets Java 21 and shades Foundation)
- src/main/java/... - plugin source code (main class: org.galaxystudios.dungeonCreate.hyperitems)
- src/main/resources - runtime resource YAMLs used by the plugin
  - items.yml, weapons.yml, armors.yml, EntityElements.yml, drops.yml, plugin.yml

Requirements
- Java 21 (JDK 21) for compilation and runtime
- Maven (3.x) for building
- Target server: Paper 1.21.x (plugin compiled against Paper API 1.21.8)
- Required server plugins (runtime): MythicMobs

Build (developer)
Open a PowerShell terminal in the repository root and run:

```powershell
mvn -DskipTests clean package
```

Notes:
- The Maven Shade plugin is configured to relocate Foundation classes. The shaded Foundation dependency is declared in the POM.
- You can enable parallel builds via `-T 1C` if desired: `mvn -T 1C -DskipTests clean package`.

Install (server)
1. Copy the generated JAR from the `target/` directory to your Paper server's `plugins/` folder.
2. Start (or restart) the server.
3. The plugin depends on MythicMobs — ensure that MythicMobs is installed and compatible with your server version.

Configuration & data files
- On first run (or during development), the plugin saves default resources for:
  - `EntityElements.yml`, `armors.yml`, `weapons.yml`, `items.yml` (see `DungeonCreate#onPluginStart`).
- Edit these YAML files in your server's `plugins/dungeonCreate` folder to add or tweak custom items, weapons, armors and entity element mappings.

Development notes
- Main class: `org.galaxystudios.dungeonCreate.hyperitems` (extends `SimplePlugin` from Foundation).
- Custom items are loaded via `LoadItems.register()` and registered with `MythicIntegration.ItemManager`.
- The plugin registers listeners for MythicMob spawn/killed events and for player/item interactions. Look in `src/main/java/org/galaxystudios/dungeonCreate/Listeners` and `MythicIntegration` for behavior.
- To reload plugin-specific data without restarting the server, the plugin exposes `onPluginReload()` which re-loads several element definitions.

Contact / Author
- org.galaxystudios (project groupId) — check repository metadata or project maintainer for contact details.