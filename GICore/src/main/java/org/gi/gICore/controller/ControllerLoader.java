package org.gi.gICore.controller;

import org.gi.gICore.GICore;
import org.gi.gICore.controller.command.CommandCore;
import org.gi.gICore.controller.command.CommandList;
import org.gi.gICore.controller.event.PlayerEvent;

public class ControllerLoader {
    private final GICore plugin;
    public ControllerLoader() {
        plugin = GICore.getInstance();

        registerCommands();
        registerEvents();
    }

    private void registerEvents(){
        plugin.getServer().getPluginManager().registerEvents(new PlayerEvent(), plugin);
    }

    private void registerCommands(){
        plugin.getCommand(CommandList.INFO).setExecutor(new CommandCore());
        plugin.getCommand(CommandList.DEPOSIT).setExecutor(new CommandCore());
        plugin.getCommand(CommandList.WITHDRAW).setExecutor(new CommandCore());
    }
}
