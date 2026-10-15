package com.mikaeel.nickbook.command;

import com.mikaeel.nickbook.NickBookPlugin;
import com.mikaeel.nickbook.service.NickService;
import com.mikaeel.nickbook.ui.NickBook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class NickCommand implements TabExecutor {
    private final NickBookPlugin p;
    private final NickService s;

    public NickCommand(NickBookPlugin p, NickService s) {
        this.p = p;
        this.s = s;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String label, String[] a) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use /nick.");
            return true;
        }
        Player x = (Player) sender;
        if (!x.hasPermission("nickbook.use")) {
            x.sendMessage(s.color("&cNo permission."));
            return true;
        }
        if (a.length == 0) {
            NickBook.openRank(p, x, s);
            return true;
        }
        String q = a[0].toLowerCase(Locale.ROOT);
        if (q.equals("reset")) {
            s.reset(x);
        } else if (q.equals("rank")) {
            if (a.length > 1) {
                s.setRank(x, a[1].toUpperCase(Locale.ROOT));
                x.sendMessage(s.color("&aYou set your nick rank to " + a[1]));
                NickBook.openSkin(p, x, s);
            } else {
                NickBook.openRank(p, x, s);
            }
        } else if (q.equals("skin")) {
            if (a.length > 1) {
                s.setSkin(x, a[1]);
                x.sendMessage(s.color("&aYou set your nick skin to " + a[1].toUpperCase(Locale.ROOT) + "!"));
                NickBook.openName(p, x, s);
            } else {
                NickBook.openSkin(p, x, s);
            }
        } else if (q.equals("name")) {
            if (a.length > 1 && a[1].equalsIgnoreCase("random")) {
                x.sendMessage(s.color("&aGenerating a unique random name. Please wait..."));
                NickBook.openRandomName(p, x, s);
            } else if (a.length > 1 && a[1].equalsIgnoreCase("previous")) {
                s.useCandidate(x);
                x.sendMessage(s.color("&aYou are now nicked as " + s.get(x).name() + "!"));
                NickBook.openConfirmation(p, x, s);
            } else {
                NickBook.openName(p, x, s);
            }
        } else if (q.equals("_use")) {
            s.useCandidate(x);
            x.sendMessage(s.color("&aYou are now nicked as " + s.get(x).name() + "!"));
            NickBook.openConfirmation(p, x, s);
        } else {
            NickBook.openRank(p, x, s);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] x) {
        if (x.length == 1) return Arrays.asList("rank", "skin", "name", "reset");
        if (x.length == 2 && x[0].equalsIgnoreCase("rank")) return this.s.ranks();
        if (x.length == 2 && x[0].equalsIgnoreCase("skin")) return Arrays.asList("normal", "steve", "random", "previous");
        if (x.length == 2 && x[0].equalsIgnoreCase("name")) return Arrays.asList("random", "previous");
        return Collections.emptyList();
    }
}
