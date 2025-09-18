package org.gi.gICore.controller.event;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.gi.gICore.GILogger;
import org.gi.gICore.manager.EconomyManager;

public class PlayerEvent implements Listener {
    private EconomyManager economyManager;
    private GILogger logger;
    public PlayerEvent() {
        economyManager = EconomyManager.getInstance();
        logger = new GILogger();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore() || !economyManager.hasAccount(player)) {
            boolean result = economyManager.createUser(player);
            if (!result) {

                player.kickPlayer("Data Create Failed");
                return;
            }
            logger.info("신규 접속: %s",player.getName());
            return;
        }
    }
}
