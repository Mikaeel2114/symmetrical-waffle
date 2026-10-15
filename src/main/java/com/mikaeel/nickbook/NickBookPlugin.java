package com.mikaeel.nickbook;

import com.mikaeel.nickbook.command.NickCommand;
import com.mikaeel.nickbook.hook.PapiHook;
import com.mikaeel.nickbook.hook.TabHook;
import com.mikaeel.nickbook.listener.NickListener;
import com.mikaeel.nickbook.service.NickService;
import com.mikaeel.nickbook.util.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class NickBookPlugin extends JavaPlugin {
    // &fYou are currently &cNICKED
    private static final String ACTION_BAR_JSON =
            "{\"text\":\"You are currently \",\"color\":\"white\",\"extra\":[{\"text\":\"NICKED\",\"color\":\"red\"}]}";

    private NickService nickService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        nickService = new NickService(this);
        NickCommand command = new NickCommand(this, nickService);
        getCommand("nick").setExecutor(command);
        getCommand("nick").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new NickListener(this, nickService), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                if (PapiHook.register(this)) {
                    getLogger().info("Hooked into PlaceholderAPI (%nickbook_...% placeholders).");
                }
            } catch (Throwable t) {
                getLogger().warning("PlaceholderAPI hook failed: " + t);
            }
        } else {
            getLogger().warning("PlaceholderAPI not found: LPC and PAPI configs cannot show nicks.");
        }

        if (Bukkit.getPluginManager().getPlugin("TAB") != null) {
            if (TabHook.registerEvents(this)) {
                getLogger().info("Hooked into TAB API (tablist and nametags).");
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (nickService.isNicked(player)) {
                nickService.applySilently(player);
            }
        }

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (nickService.isNicked(player)) {
                        ActionBar.send(player, ACTION_BAR_JSON);
                    }
                }
            }
        }, 20L, 40L);
    }

    public NickService getNickService() {
        return nickService;
    }
}
