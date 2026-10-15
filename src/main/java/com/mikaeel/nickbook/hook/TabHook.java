package com.mikaeel.nickbook.hook;

import com.mikaeel.nickbook.NickBookPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Reflection hook for TAB developer API (me.neznamy.tab.api.TabAPI).
 * Sets tab list prefix/name/suffix and nametag prefix/suffix when nicked,
 * resets with null, and re-applies on TabLoadEvent and PlayerLoadEvent.
 */
public final class TabHook {
    private TabHook() {}

    public static void apply(Player player, String prefix, String customName, String suffix) {
        update(player, prefix, customName, suffix);
    }

    public static void reset(Player player) {
        update(player, null, null, null);
    }

    private static void update(Player player, String prefix, String customName, String suffix) {
        if (player == null || Bukkit.getPluginManager().getPlugin("TAB") == null) return;
        try {
            Class<?> apiClass = Class.forName("me.neznamy.tab.api.TabAPI");
            Class<?> tabPlayerClass = Class.forName("me.neznamy.tab.api.TabPlayer");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            if (api == null) return;

            Object tabPlayer = apiClass.getMethod("getPlayer", UUID.class).invoke(api, player.getUniqueId());
            if (tabPlayer == null) return;

            Object tabListManager = apiClass.getMethod("getTabListFormatManager").invoke(api);
            if (tabListManager != null) {
                Class<?> tlmClass = Class.forName("me.neznamy.tab.api.tablist.TabListFormatManager");
                tlmClass.getMethod("setPrefix", tabPlayerClass, String.class).invoke(tabListManager, tabPlayer, prefix);
                tlmClass.getMethod("setName", tabPlayerClass, String.class).invoke(tabListManager, tabPlayer, customName);
                tlmClass.getMethod("setSuffix", tabPlayerClass, String.class).invoke(tabListManager, tabPlayer, suffix);
            }

            Object nameTagManager = apiClass.getMethod("getNameTagManager").invoke(api);
            if (nameTagManager != null) {
                Class<?> ntmClass = Class.forName("me.neznamy.tab.api.nametag.NameTagManager");
                ntmClass.getMethod("setPrefix", tabPlayerClass, String.class).invoke(nameTagManager, tabPlayer, prefix);
                ntmClass.getMethod("setSuffix", tabPlayerClass, String.class).invoke(nameTagManager, tabPlayer, suffix);
            }
        } catch (Throwable ignored) {
        }
    }

    public static boolean registerEvents(final NickBookPlugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("TAB") == null) return false;
        try {
            Class<?> apiClass = Class.forName("me.neznamy.tab.api.TabAPI");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            if (api == null) return false;

            Object eventBus = apiClass.getMethod("getEventBus").invoke(api);
            if (eventBus == null) return false;

            Class<?> eventBusClass = Class.forName("me.neznamy.tab.api.event.EventBus");
            Class<?> handlerClass = Class.forName("me.neznamy.tab.api.event.EventHandler");
            Method register = eventBusClass.getMethod("register", Class.class, handlerClass);

            Class<?> tabLoadEvent = Class.forName("me.neznamy.tab.api.event.plugin.TabLoadEvent");
            Object loadHandler = Proxy.newProxyInstance(
                    handlerClass.getClassLoader(),
                    new Class<?>[]{handlerClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) {
                            String name = method.getName();
                            if ("equals".equals(name)) return proxy == (args != null && args.length > 0 ? args[0] : null);
                            if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                            if ("toString".equals(name)) return "NickBookTabLoadHandler";
                            if ("handle".equals(name)) {
                                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            if (plugin.getNickService().isNicked(p)) {
                                                plugin.getNickService().applySilently(p);
                                            }
                                        }
                                    }
                                });
                            }
                            return null;
                        }
                    }
            );
            register.invoke(eventBus, tabLoadEvent, loadHandler);

            Class<?> playerLoadEvent = Class.forName("me.neznamy.tab.api.event.player.PlayerLoadEvent");
            Object playerHandler = Proxy.newProxyInstance(
                    handlerClass.getClassLoader(),
                    new Class<?>[]{handlerClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                            String name = method.getName();
                            if ("equals".equals(name)) return proxy == (args != null && args.length > 0 ? args[0] : null);
                            if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                            if ("toString".equals(name)) return "NickBookPlayerLoadHandler";
                            if ("handle".equals(name) && args != null && args.length == 1 && args[0] != null) {
                                Object tabPlayer = args[0].getClass().getMethod("getPlayer").invoke(args[0]);
                                if (tabPlayer != null) {
                                    final UUID uuid = (UUID) tabPlayer.getClass().getMethod("getUniqueId").invoke(tabPlayer);
                                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            Player p = Bukkit.getPlayer(uuid);
                                            if (p != null && plugin.getNickService().isNicked(p)) {
                                                plugin.getNickService().applySilently(p);
                                            }
                                        }
                                    });
                                }
                            }
                            return null;
                        }
                    }
            );
            register.invoke(eventBus, playerLoadEvent, playerHandler);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
