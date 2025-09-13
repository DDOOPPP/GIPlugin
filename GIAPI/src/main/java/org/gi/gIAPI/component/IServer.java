package org.gi.gIAPI.component;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IServer {
    public String getMotd();

    public OfflinePlayer getOfflinePlayer(UUID uuid);

    public OfflinePlayer getOfflinePlayer(String name);

    public List<Player> getOnlinePlayers();
}
