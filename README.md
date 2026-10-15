# NickBook

Hypixel-style `/nick` written-book setup plugin for a Spigot 1.8.8 server you control (Java 17 runtime, Java 8 bytecode target).

## Important Limit

This plugin only works on your own Spigot 1.8.8 server. It cannot change nicknames on the real Hypixel server.

## Features

- `/nick` opens a 5-step clickable written-book flow (Rank -> Skin -> Name -> Random Name Reroll -> Confirmation) using the 1.8.8 `MC|BOpen` packet without leaving books in the player's inventory.
- Action bar message above the hearts and hunger bar every 40 ticks while nicked: `&fYou are currently &cNICKED`.
- Direct TAB API reflection hook (`TabHook`) that sets tab list prefix/name/suffix and nametag prefix/suffix automatically when TAB is installed, plus re-applies on join and `/tab reload`.
- PlaceholderAPI expansion (`%nickbook_name%`, `%nickbook_prefix%`, `%nickbook_suffix%`, `%nickbook_displayname%`, `%nickbook_realname%`, `%nickbook_is_nicked%`) for LPC and TAB configs.
- LuckPerms reflection hook to read real rank prefixes and suffixes when not nicked.

## Build

```bash
mvn -B clean package
```

Output jar: `target/nickbook-1.1.0.jar`
