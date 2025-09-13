package org.gi.gIAPI.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface GII18n {
    Component translate(String key, Object... args);
    void applyItemTranslation(ItemStack item, String nameKey, List<String> loreKeys, List<List<Component>> args);
    String getPlayerLocale(Player player);
}