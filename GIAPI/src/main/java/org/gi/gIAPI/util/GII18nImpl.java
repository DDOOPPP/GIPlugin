package org.gi.gIAPI.util;

import net.Indyuce.mmoitems.api.event.ItemBuildEvent;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GII18nImpl implements GII18n {
    @Override
    public Component translate(String key, Object... args) {
        // translatable Component 반환
        Component[] comps = Arrays.stream(args)
                .map(o -> Component.text(String.valueOf(o)))
                .toArray(Component[]::new);
        return Component.translatable(key, comps);
    }

    @Override
    public void applyItemTranslation(ItemStack item, String nameKey, List<String> loreKeys, List<List<Component>> args) {
        ItemMeta meta = item.getItemMeta();
        if (nameKey != null) {
            meta.displayName(translate(nameKey));
        }
        if (loreKeys != null && !loreKeys.isEmpty()) {
            List<Component> lore = new ArrayList<>();
            for (int i = 0; i < loreKeys.size(); i++) {
                String k = loreKeys.get(i);
                List<Component> a = (args != null && i < args.size()) ? args.get(i) : List.of();
                lore.add(Component.translatable(k, a));
            }
            meta.lore(lore);
        }
        item.setItemMeta(meta);
    }

    @Override
    public String getPlayerLocale(Player player) {
        return player.locale().toString().toLowerCase(Locale.ROOT); // ex: "ko_kr"
    }
}

