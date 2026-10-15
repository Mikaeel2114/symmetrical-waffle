package com.mikaeel.nickbook.listener;

import com.mikaeel.nickbook.NickBookPlugin;
import com.mikaeel.nickbook.service.NickService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class NickListener implements Listener {
    private final NickBookPlugin plugin;
    private final NickService service;

    public NickListener(NickBookPlugin plugin, NickService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        if (service.isNicked(player)) {
            service.applySilently(player);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    if (player.isOnline() && service.isNicked(player)) {
                        service.applySilently(player);
                    }
                }
            }, 5L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        if (Bukkit.getPluginManager().isPluginEnabled("LPC")) return;
        if (!plugin.getConfig().getBoolean("fallback-chat-format", true)) return;
        String prefix = service.shownPrefix(e.getPlayer()).replace("%", "%%");
        String name = service.shownName(e.getPlayer()).replace("%", "%%");
        String suffix = service.shownSuffix(e.getPlayer()).replace("%", "%%");
        e.setFormat(prefix + name + suffix + "§f: %2$s");
    }
}
