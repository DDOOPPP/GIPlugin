package org.gi.gIAPI.util;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileUtil {
    public static File getResource(Plugin plugin,String filename) {
        File file = new File(plugin.getDataFolder(), filename);
        if (!file.exists()) {
            plugin.saveResource(filename, false);
        }
        return file;
    }


}
