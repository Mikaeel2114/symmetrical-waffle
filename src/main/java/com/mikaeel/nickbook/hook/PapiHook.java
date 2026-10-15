package com.mikaeel.nickbook.hook;

import com.mikaeel.nickbook.NickBookPlugin;

public final class PapiHook {
    private PapiHook() {}

    public static boolean register(NickBookPlugin plugin) {
        return new NickExpansion(plugin).register();
    }
}
