package org.gi.gIAPI.component;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface IConfig {
    public String getString(String key, String defaultValue);

    public int getInt(String key, int defaultValue);

    public boolean getBoolean(String key, boolean defaultValue);

    public double getDouble(String key, double defaultValue);

    public long getLong(String key, long defaultValue);

    public ConfigurationSection getConfigurationSection(String key);

    public List<String> getStringList(String key);

    public List<Double> getDoubleList(String key);

    public List<Integer> getIntList(String key);
}
