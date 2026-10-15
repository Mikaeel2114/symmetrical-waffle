# Hooking NickBook into PlaceholderAPI, LuckPerms, LPC and TAB

Install on your 1.8.8 server:
- PlaceholderAPI
- LuckPerms
- LPC
- TAB
- NickBook (`nickbook-1.1.0.jar`)

Then run in console or in-game:
```text
/papi ecloud download LuckPerms
/papi reload
```

## Placeholders

| Placeholder | When nicked | When not nicked |
|---|---|---|
| `%nickbook_prefix%` | fake rank, e.g. `[VIP+] ` | real LuckPerms prefix |
| `%nickbook_name%` | nick name | real username |
| `%nickbook_suffix%` | empty | real LuckPerms suffix |
| `%nickbook_displayname%` | prefix + name + suffix | prefix + name + suffix |
| `%nickbook_realname%` | always the real username | always the real username |
| `%nickbook_is_nicked%` | `true` | `false` |

## LPC (`plugins/LPC/config.yml`)

Replace `{prefix}`, `{name}`, `{suffix}` with the NickBook placeholders:

```yaml
chat-format: "%nickbook_prefix%%nickbook_name%%nickbook_suffix%&f: {message}"
```

If you use `group-formats`, update each group the same way, then run `/lpc reload`.
NickBook does not override the chat event when LPC is enabled.

## TAB (`plugins/TAB/groups.yml`)

NickBook now hooks into the TAB developer API in code (`TabListFormatManager` and `NameTagManager`) to apply fake ranks and names directly when nicked and clear them on `/nick reset`.

You can also configure `plugins/TAB/groups.yml` with the PlaceholderAPI placeholders as a backup:

```yaml
_DEFAULT_:
  tabprefix: "%nickbook_prefix%"
  customtabname: "%nickbook_name%"
  tabsuffix: "%nickbook_suffix%"
  tagprefix: "%nickbook_prefix%"
  tagsuffix: "%nickbook_suffix%"
```

Remove `tabprefix` and `tagprefix` overrides from other groups (or set them to the same placeholders), then run `/tab reload`.

Optional faster refresh in `plugins/TAB/config.yml`:

```yaml
placeholder-refresh-intervals:
  "%nickbook_prefix%": 500
  "%nickbook_name%": 500
  "%nickbook_suffix%": 500
```

## Action bar

While nicked, players see this above the hearts and hunger bar:
`&fYou are currently &cNICKED`
