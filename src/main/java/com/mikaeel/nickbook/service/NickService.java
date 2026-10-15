package com.mikaeel.nickbook.service;

import com.mikaeel.nickbook.NickBookPlugin;
import com.mikaeel.nickbook.hook.LuckPermsHook;
import com.mikaeel.nickbook.hook.TabHook;
import com.mikaeel.nickbook.model.NickProfile;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class NickService {
    private final NickBookPlugin plugin;

    public NickService(NickBookPlugin plugin) {
        this.plugin = plugin;
    }

    public NickProfile get(Player player) {
        String path = "players." + player.getUniqueId();
        FileConfiguration c = plugin.getConfig();
        return new NickProfile(
                c.getString(path + ".rank", "DEFAULT"),
                c.getString(path + ".skin", "normal"),
                c.getString(path + ".name", player.getName())
        );
    }

    public void setRank(Player p, String rank) {
        if (plugin.getConfig().contains("ranks." + rank)) {
            plugin.getConfig().set("players." + p.getUniqueId() + ".rank", rank);
            plugin.saveConfig();
            if (isNicked(p)) {
                applySilently(p);
            }
        }
    }

    public void setSkin(Player p, String skin) {
        String path = "players." + p.getUniqueId();
        String oldSkin = plugin.getConfig().getString(path + ".skin");
        if (oldSkin != null) {
            plugin.getConfig().set(path + ".previous-skin", oldSkin);
        }
        plugin.getConfig().set(path + ".skin", skin);
        plugin.saveConfig();
        p.sendMessage(color("&aSkin choice saved: &f" + skin));
    }

    public void setRandomName(Player p) {
        String value = randomName(p);
        String path = "players." + p.getUniqueId();
        String oldName = plugin.getConfig().getString(path + ".name");
        if (oldName != null) {
            plugin.getConfig().set(path + ".previous-name", oldName);
        }
        plugin.getConfig().set(path + ".name", value);
        saveAndApply(p);
    }

    public String randomName(Player p) {
        List<String> names = new ArrayList<String>(plugin.getConfig().getStringList("random-names"));
        if (names.isEmpty()) {
            names.add("PixelPilot");
        }
        String old = get(p).name();
        String currentCandidate = plugin.getConfig().getString("players." + p.getUniqueId() + ".candidate");
        if (names.size() > 1) {
            Iterator<String> it = names.iterator();
            while (it.hasNext()) {
                String n = it.next();
                if (n.equalsIgnoreCase(old) || (currentCandidate != null && n.equalsIgnoreCase(currentCandidate) && names.size() > 1)) {
                    it.remove();
                }
            }
            if (names.isEmpty()) {
                names.add("PixelPilot");
            }
        }
        String value = names.get(ThreadLocalRandom.current().nextInt(names.size()));
        plugin.getConfig().set("players." + p.getUniqueId() + ".candidate", value);
        plugin.saveConfig();
        return value;
    }

    public String candidate(Player p) {
        String value = plugin.getConfig().getString("players." + p.getUniqueId() + ".candidate");
        if (value == null || value.isEmpty()) {
            return randomName(p);
        }
        return value;
    }

    public void useCandidate(Player p) {
        String path = "players." + p.getUniqueId();
        String oldName = plugin.getConfig().getString(path + ".name");
        if (oldName != null) {
            plugin.getConfig().set(path + ".previous-name", oldName);
        }
        plugin.getConfig().set(path + ".name", candidate(p));
        saveAndApply(p);
    }

    public void reset(Player p) {
        plugin.getConfig().set("players." + p.getUniqueId() + ".active", false);
        plugin.saveConfig();
        p.setDisplayName(p.getName());
        p.setPlayerListName(null);
        TabHook.reset(p);
        p.sendMessage(color("&aNickname reset."));
    }

    public List<String> ranks() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ranks");
        if (section == null) {
            return Collections.emptyList();
        }
        return new ArrayList<String>(section.getKeys(false));
    }

    public void saveAndApply(Player p) {
        applySilently(p);
        p.sendMessage(color("&aNickname applied: &f" + get(p).name()));
    }

    public void applySilently(Player p) {
        plugin.getConfig().set("players." + p.getUniqueId() + ".active", true);
        plugin.saveConfig();
        String shown = formattedName(p);
        p.setDisplayName(shown);
        String list = shown.length() > 16 ? shown.substring(0, 16) : shown;
        if (list.endsWith("§")) {
            list = list.substring(0, list.length() - 1);
        }
        p.setPlayerListName(list);
        TabHook.apply(p, shownPrefix(p), shownName(p), "");
    }

    public String formattedName(Player p) {
        return shownPrefix(p) + shownName(p);
    }

    public String shownName(Player p) {
        return isNicked(p) ? get(p).name() : p.getName();
    }

    public String shownPrefix(Player p) {
        if (isNicked(p)) {
            return color(plugin.getConfig().getString("ranks." + get(p).rank(), "&7"));
        }
        return color(LuckPermsHook.prefix(p));
    }

    public String shownSuffix(Player p) {
        if (isNicked(p)) {
            return "";
        }
        return color(LuckPermsHook.suffix(p));
    }

    public boolean isNicked(Player p) {
        return plugin.getConfig().getBoolean("players." + p.getUniqueId() + ".active", false);
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}
