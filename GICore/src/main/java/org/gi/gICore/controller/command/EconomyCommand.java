package org.gi.gICore.controller.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand {
    public static boolean executeInfo(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1){

        }
        return false;
    }
}
