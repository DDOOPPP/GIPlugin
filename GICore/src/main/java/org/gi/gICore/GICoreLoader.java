package org.gi.gICore;

import io.r2dbc.spi.Result;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gICore.component.GIEconomy;
import org.gi.gICore.component.MessageLoader;
import org.gi.gICore.controller.ControllerLoader;
import org.gi.gICore.data.database.DataBaseConnection;

public class GICoreLoader {
    private static GIConfig config;
    private static Plugin plugin;
    private static GILogger logger;
    private static ControllerLoader controllerLoader;
    public static void initialize(){
        plugin = GICore.getInstance();
        config = GICore.getDefaultConfig();
        logger = new GILogger();

        connectDB(config);

        if (!registerVault(plugin)){
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        logger.info("Vault Initialize...");
        MessageLoader.Initialize();

        controllerLoader = new ControllerLoader();
    }

    private static void connectDB(GIConfig dbConfig){
         DataBaseConnection.connect(dbConfig);
    }

    private static boolean registerVault(Plugin plugin){
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null){
            logger.error("Vault not found!");
            return false;
        }
        GIEconomy.registerEconomy();

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null){
            logger.error("Economy Service not registered!");
            return false;
        }
        Economy econ = rsp.getProvider();
        if (econ == null){
            logger.error("Economy Provider not registered!");
            return false;
        }

        GICore.setEconomy(econ);
        return true;
    }
}
