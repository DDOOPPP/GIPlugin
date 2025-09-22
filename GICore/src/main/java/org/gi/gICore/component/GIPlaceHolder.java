package org.gi.gICore.component;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.manager.UserService;
import org.jetbrains.annotations.NotNull;

public class GIPlaceHolder extends PlaceholderExpansion {
    private final EconomyManager manager;
    private final UserService userService;
    public GIPlaceHolder() {
        manager = EconomyManager.getInstance();
        userService = UserService.getInstance();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "gi";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "Y.S.M";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equals("balance")) {
            return manager.format(manager.getBalance(player).doubleValue());
        }else if (params.equals("guild")){
            return userService.getUserData(player.getUniqueId()).getGuildName();
        }else{
            return null;
        }
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equals("balance")) {
            return manager.format(manager.getBalance(player).doubleValue());
        }else if (params.equals("guild")){
            return userService.getUserData(player.getUniqueId()).getGuildName();
        }
        else{
            return null;
        }
    }
}
