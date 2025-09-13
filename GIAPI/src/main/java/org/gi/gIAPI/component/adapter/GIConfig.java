package org.gi.gIAPI.component.adapter;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.gi.gIAPI.component.IConfig;

import java.io.File;
import java.util.List;

public class GIConfig implements IConfig {
    private final FileConfiguration configuration;
    private final File file;

    public GIConfig(File file) {
        this.file = file;
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }


    @Override
    public String getString(String key, String defaultValue) {
        return configuration.getString(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return configuration.getInt(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return configuration.getBoolean(key, defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return configuration.getDouble(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return configuration.getLong(key, defaultValue);
    }

    @Override
    public ConfigurationSection getConfigurationSection(String key) {
        return configuration.getConfigurationSection(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return configuration.getStringList(key);
    }

    @Override
    public List<Double> getDoubleList(String key) {
        return configuration.getDoubleList(key);
    }

    @Override
    public List<Integer> getIntList(String key) {
        return configuration.getIntegerList(key);
    }
}
