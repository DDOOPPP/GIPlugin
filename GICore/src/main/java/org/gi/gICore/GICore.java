package org.gi.gICore;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gIAPI.GIAPI;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gIAPI.util.FileUtil;

import java.io.File;

public final class GICore extends JavaPlugin {
    private static GICore instance;
    private static GIConfig config;
    private static Economy econ;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        config = new GIConfig(FileUtil.getResource(this, "config.yml"));
        GICoreLoader.initialize();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }

    public static GIConfig getDefaultConfig(){
        return config;
    }

    public static GICore getInstance() {
        return instance;
    }

    public static Economy getEconomy(){
        return econ;
    }

    public static void setEconomy(Economy econ){
        GICore.econ = econ;
    }
}
