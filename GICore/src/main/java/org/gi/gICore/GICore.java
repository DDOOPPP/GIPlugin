package org.gi.gICore;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gIAPI.util.FileUtil;

import java.io.File;

public final class GICore extends JavaPlugin {
    private static GICore instance;
    private static GIConfig config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        config = new GIConfig(FileUtil.getResource(this, "config.yml"));

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
}
