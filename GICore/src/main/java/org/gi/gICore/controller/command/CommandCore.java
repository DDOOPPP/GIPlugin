package org.gi.gICore.controller.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandCore implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandName = command.getName();

        switch (commandName) {
            case CommandList.INFO :
                return true;
            case CommandList.DEPOSIT :
                return true;
            case CommandList.WITHDRAW :
                return true;
            case CommandList.RELOAD :
                return true;
            default:
                return false;
        }
    }
}
