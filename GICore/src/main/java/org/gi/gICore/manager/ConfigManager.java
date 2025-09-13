package org.gi.gICore.manager;

import org.bukkit.plugin.Plugin;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gIAPI.util.FileUtil;
import org.gi.gICore.GICore;

public class ConfigManager {
    private static Plugin plugin = GICore.getInstance();

    public static GIConfig getConfig(String fileName){
        return new GIConfig(FileUtil.getResource(plugin,fileName));
    }
}
