package org.gi.gICore.controller.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.gi.gICore.manager.EconomyManager;
import org.jetbrains.annotations.NotNull;

public class CommandCore implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandName = command.getName();

        switch (commandName) {
            case CommandList.INFO :
                return EconomyCommand.executeInfo(sender, args);
            case CommandList.DEPOSIT :
                return EconomyCommand.executeDeposit(sender, args);
            case CommandList.WITHDRAW :
                return EconomyCommand.executeWithdraw(sender, args);
            case CommandList.RELOAD :
                return true;
            default:
                return false;
        }
    }
}
