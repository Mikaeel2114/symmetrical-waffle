package com.mikaeel.nickbook.ui;

import com.mikaeel.nickbook.NickBookPlugin;
import com.mikaeel.nickbook.service.NickService;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class NickBook {
    private NickBook() {}

    public static void openRank(NickBookPlugin p, Player player, NickService s) {
        open(player, Arrays.asList(LegacyBook.compose(
                "{\"text\":\"Let's get you set up with your nickname! First, you'll need to choose which RANK you would like to be shown as when nicked.\\n\\n\",\"color\":\"black\",\"bold\":false}",
                LegacyBook.option("➤ DEFAULT\n", "/nick rank DEFAULT", "Click here to be shown as DEFAULT", "gray"),
                LegacyBook.option("➤ VIP\n", "/nick rank VIP", "Click here to be shown as VIP", "green"),
                LegacyBook.option("➤ VIP+\n", "/nick rank VIP+", "Click here to be shown as VIP+", "green"),
                LegacyBook.option("➤ MVP\n", "/nick rank MVP", "Click here to be shown as MVP", "aqua"),
                LegacyBook.option("➤ MVP+", "/nick rank MVP+", "Click here to be shown as MVP+", "aqua"))));
    }

    public static void openSkin(NickBookPlugin p, Player player, NickService s) {
        open(player, Collections.singletonList(LegacyBook.compose(
                "{\"text\":\"Awesome! Now, which SKIN would you like to have while nicked?\\n\\n\",\"color\":\"black\"}",
                LegacyBook.option("➤ My normal skin\n", "/nick skin normal", "Click here to use your normal skin", "black"),
                LegacyBook.option("➤ Steve/Alex skin\n", "/nick skin alex", "Click here to use a Steve/Alex skin", "black"),
                LegacyBook.option("➤ Random skin", "/nick skin random", "Click here to use a random skin", "black"))));
    }

    public static void openName(NickBookPlugin p, Player player, NickService s) {
        open(player, Collections.singletonList(LegacyBook.compose(
                "{\"text\":\"Alright, now you'll need to choose the NAME to use!\\n\\n\",\"color\":\"black\"}",
                LegacyBook.option("➤ Use a random name\n", "/nick name random", "Click here to use a randomly generated name.", "black"),
                "{\"text\":\"\\nTo go back to being your usual self, type:\\n/nick reset\",\"color\":\"black\"}")));
    }

    public static void openRandomName(NickBookPlugin p, Player player, NickService s) {
        String candidate = s.randomName(player);
        open(player, Collections.singletonList(LegacyBook.compose(
                "{\"text\":\"We've generated a random username for you:\\n\",\"color\":\"black\"}",
                "{\"text\":\"" + LegacyBook.escape(candidate) + "\\n\\n\",\"color\":\"black\",\"bold\":true}",
                LegacyBook.option("USE NAME\n", "/nick name previous", "Click here to use this name.", "dark_green"),
                LegacyBook.option("TRY AGAIN", "/nick name random", "Click here to generate another name.", "red"))));
    }

    public static void openConfirmation(NickBookPlugin p, Player player, NickService s) {
        open(player, Collections.singletonList(LegacyBook.page("You have finished setting up your nickname!\n\nWhen you go into a game, you will be nicked as " + s.get(player).name() + ".\n\nYou will not be nicked in lobbies.\n\nTo go back to being your usual self, type:\n/nick reset")));
    }

    private static void open(Player player, List<String> pages) {
        LegacyBook.open(player, pages);
    }
}
