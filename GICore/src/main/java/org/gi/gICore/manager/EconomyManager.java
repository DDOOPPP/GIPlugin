package org.gi.gICore.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.gi.gICore.GICore;

import java.math.BigDecimal;

public class EconomyManager {
    private static EconomyManager instance;
    private Economy economy;

    public EconomyManager(){
        economy = GICore.getEconomy();
        instance = this;
    }

    public boolean createUser(Player player){
        return economy.createPlayerAccount(player);
    }

    public boolean has(OfflinePlayer player,double amount){
        return economy.has(player,amount);
    }

    public BigDecimal getBalance(OfflinePlayer player){
        return BigDecimal.valueOf(economy.getBalance(player));
    }

    public String deposit(OfflinePlayer player,double amount){
        return economy.depositPlayer(player,amount).errorMessage;
    }

    public String withdraw(OfflinePlayer player,double amount){
        return economy.withdrawPlayer(player,amount).errorMessage;
    }

    public static EconomyManager getInstance() {
        if (instance == null || instance.economy == null) {
            instance = new EconomyManager();
        }
        return instance;
    }
}
