package com.mikaeel.nickbook.util;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class ActionBar {
    private static boolean broken;
    private static String version;
    private static Method serialize;
    private static Constructor<?> packetCtor;
    private static Class<?> packetClass;

    private ActionBar() {}

    public static void send(Player player, String json) {
        if (broken) return;
        try {
            if (version == null) init(player);
            Object component = serialize.invoke(null, json);
            Object packet = packetCtor.newInstance(component, (byte) 2);
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", packetClass).invoke(connection, packet);
        } catch (Exception ex) {
            broken = true;
        }
    }

    private static void init(Player player) throws Exception {
        version = player.getServer().getClass().getPackage().getName().split("\\.")[3];
        String nms = "net.minecraft.server." + version + ".";
        Class<?> base = Class.forName(nms + "IChatBaseComponent");
        Class<?> serializer = Class.forName(nms + "IChatBaseComponent$ChatSerializer");
        serialize = serializer.getMethod("a", String.class);
        Class<?> chatPacket = Class.forName(nms + "PacketPlayOutChat");
        packetCtor = chatPacket.getConstructor(base, byte.class);
        packetClass = Class.forName(nms + "Packet");
    }
}
