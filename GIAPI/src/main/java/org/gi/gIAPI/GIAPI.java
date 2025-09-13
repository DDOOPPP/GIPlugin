package org.gi.gIAPI;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class GIAPI extends JavaPlugin {
    private static GIAPI instance;
    private static Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        logger.info("Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("[GIAPI] Plugin has been disabled.");
    }

    public static GIAPI getInstance() {
        return instance;
    }

    public static Logger logger() {
        return  logger;
    }
}
