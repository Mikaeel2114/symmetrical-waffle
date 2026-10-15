package com.mikaeel.nickbook.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class LuckPermsHook {
    private LuckPermsHook() {}

    public static String prefix(Player player) { return meta(player, "getPrefix"); }
    public static String suffix(Player player) { return meta(player, "getSuffix"); }

    private static String meta(Player player, String getter) {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) return "";
        try {
            Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
            Class<?> lpClass = Class.forName("net.luckperms.api.LuckPerms");
            Class<?> adapterClass = Class.forName("net.luckperms.api.platform.PlayerAdapter");
            Class<?> metaClass = Class.forName("net.luckperms.api.cacheddata.CachedMetaData");
            Object lp = providerClass.getMethod("get").invoke(null);
            Object adapter = lpClass.getMethod("getPlayerAdapter", Class.class).invoke(lp, Player.class);
            Method getMeta = adapterClass.getMethod("getMetaData", Object.class);
            Object meta = getMeta.invoke(adapter, player);
            Object value = metaClass.getMethod(getter).invoke(meta);
            return value == null ? "" : value.toString();
        } catch (Throwable t) {
            return "";
        }
    }
}
