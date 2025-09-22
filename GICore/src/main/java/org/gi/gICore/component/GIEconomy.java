package org.gi.gICore.component;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gIAPI.util.FileUtil;
import org.gi.gICore.GICore;
import org.gi.gICore.manager.UserService;
import org.gi.gICore.model.Enum;
import org.gi.gICore.model.log.EconomyLog;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.model.values.MessageName;
import org.gi.gICore.util.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GIEconomy implements Economy {
    private static GIEconomy instance;
    private GIConfig config;
    private BigDecimal base;
    private String unit;
    private UserService userService;
    public GIEconomy() {
        config = new GIConfig(FileUtil.getResource(GICore.getInstance(), "setting.yml"));
        double start = config.getDouble("economy.start",100d);
        this.base = BigDecimal.valueOf(start);
        this.unit = config.getString("economy.unit","GOLD");
        userService = UserService.getInstance();
        instance = this;
    }

    public static void registerEconomy(){
        if (instance == null){
            instance = new GIEconomy();
        }
        GICore.getInstance().getServer().getServicesManager().register(Economy.class,instance,GICore.getInstance(), ServicePriority.Highest);
    }

    public static GIEconomy getInstance() {
        if (instance == null) {
            instance = new GIEconomy();
        }
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "GIEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return String.format("%.0f %s",amount,unit);
    }

    @Override
    public String currencyNamePlural() {
        return this.unit;
    }

    @Override
    public String currencyNameSingular() {
        return this.unit;
    }

    @Override
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return this.hasAccount(player, null);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return userService.isExist(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return this.getBalance(player, null);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        if (!hasAccount(player, world)) {
            return -9999;
        }
        return userService.getUserData(player.getUniqueId()).getBalance().doubleValue();
    }

    @Override
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return this.has(player,null, amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        double balance = this.getBalance(player, worldName);
        if (balance < 0) {
            return false;
        }
        return balance >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return this.withdrawPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        UUID uuid = player.getUniqueId();
        if (amount <= 0) {
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.INVALID_VALUE);
        }
        double balance = this.getBalance(player, worldName);
        if (balance < 0) {
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.NOT_FOUND_DATA);
        }
        if (!has(player,amount)){
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.NOT_ENOUGH_BALANCE);
        }

        balance -= amount;

        EconomyLog log = new EconomyLog(
                uuid,
                player.getName(),
                Enum.EconomyType.WITHDRAW,
                BigDecimal.valueOf(amount),
                BigDecimal.valueOf(balance)
        );

        Result result = userService.updateBalance(uuid, log);
        if (!result.isSuccess()){
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.CALL_ADMIN);
        }

        return new EconomyResponse(amount,balance, EconomyResponse.ResponseType.SUCCESS,MessageName.WITHDRAW_OK);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return this.depositPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        UUID uuid = player.getUniqueId();
        if (amount <= 0) {
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.INVALID_VALUE);
        }
        double balance = this.getBalance(player, worldName);
        if (balance < 0) {
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.NOT_FOUND_DATA);
        }

        EconomyLog log = new EconomyLog(
                uuid,
                player.getName(),
                Enum.EconomyType.DEPOSIT,
                BigDecimal.valueOf(amount),
                BigDecimal.valueOf(balance)
        );

        Result result = userService.updateBalance(uuid, log);
        if (!result.isSuccess()){
            return new EconomyResponse(0,0, EconomyResponse.ResponseType.FAILURE, MessageName.CALL_ADMIN);
        }

        return new EconomyResponse(amount,balance, EconomyResponse.ResponseType.SUCCESS,MessageName.DEPOSIT_OK);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player,null);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        if (hasAccount(player)){
            return true;
        }
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        String guild = "NONE";

        UserData userData =  new UserData(
                uuid,
                playerName,
                base,
                guild
        );

        return userService.create(userData).isSuccess();
    }
}
