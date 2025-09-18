package org.gi.gICore.component;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gi.gICore.manager.EconomyManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GIPlaceHolder extends PlaceholderExpansion {
    private final EconomyManager manager;
    public GIPlaceHolder() {
        manager = EconomyManager.getInstance();
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
        }else{
            return null;
        }
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equals("balance")) {
            return manager.format(manager.getBalance(player).doubleValue());
        }else{
            return null;
        }
    }
}
