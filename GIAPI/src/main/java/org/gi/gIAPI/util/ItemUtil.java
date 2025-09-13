package org.gi.gIAPI.util;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.StatManager;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtil {
    public static boolean isCustom(String item_id) {
        return CustomStack.isInRegistry(item_id);
    }

    public static ItemStack getItem(String material) {
        return new ItemStack(Material.valueOf(material.toUpperCase()));
    }

    public static ItemStack getItem(Material material) {
        return new ItemStack(material);
    }

    public static ItemStack getItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    public static boolean isMMOItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        NBTItem nbtItem = NBTItem.get(item);

        return nbtItem.hasType();
    }

    public static Type getItemType(String id) {
        TypeManager manager = new  TypeManager();

        return manager.has(id.toUpperCase()) ? manager.get(id.toUpperCase()) : null;
    }

    public static ItemStack getMMOItem(String itemID, Type type) {
        if (type == null) {
            return new ItemStack(Material.AIR);
        }
        MMOItems mmoItems = new  MMOItems();
        MMOItem item = mmoItems.getMMOItem(type,itemID);

        if (item == null) {
            return new ItemStack(Material.AIR);
        }
        return item.newBuilder().getItemStack();
    }

    public static ItemStack getMMOItem(ItemStack item) {
        NBTItem nbtItem = NBTItem.get(item);

        LiveMMOItem liveMMOItem = new LiveMMOItem(nbtItem);
        return liveMMOItem.clone().newBuilder().getItemStack();
    }

    public static ItemStack getCustomItem(String namespace_id) {
        if (!CustomStack.isInRegistry(namespace_id)) {
            return new ItemStack(Material.BEDROCK);
        }
        return CustomStack.getInstance(namespace_id).getItemStack();
    }

    public static boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        return CustomStack.byItemStack(item) != null;
    }

    public static ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack ItemDeserialize(Map<String,Object> map) {
        return ItemStack.deserialize(map);
    }

    public static Map<String,Object> ItemSerialize(ItemStack itemStack) {
        itemStack.clone();
        return itemStack.serialize();
    }

    public static NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey("gi", key);
    }

    public static <P,C> void edit(ItemStack itemStack, String key, PersistentDataType<P,C> type,C value) {
        NamespacedKey namespacedKey = getNamespacedKey(key);

        itemStack.editMeta(meta ->{
            meta.getPersistentDataContainer().set(namespacedKey, type, value);
        });
    }

    public static <P,C> C getValue(ItemStack itemStack, String key, PersistentDataType<P,C> type) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;

        ItemMeta meta = itemStack.getItemMeta();

        NamespacedKey namespacedKey = getNamespacedKey(key);
        return meta.getPersistentDataContainer().get(namespacedKey, type);
    }

    public static boolean hasKey(ItemStack itemStack, String key,PersistentDataType<?,?> type) {
        if (itemStack == null || !itemStack.hasItemMeta()) return false;
        ItemMeta meta = itemStack.getItemMeta();

        NamespacedKey namespacedKey = getNamespacedKey(key);

        return meta.getPersistentDataContainer().has(namespacedKey, type);
    }

    public static String getString(ItemStack itemStack, String key) {
        String value = getValue(itemStack, key, PersistentDataType.STRING);
        return value == null ? "" : value;
    }

    public static int getInt(ItemStack itemStack, String key) {
        int value = getValue(itemStack, key, PersistentDataType.INTEGER);
        return value == -1 ? 0 : value;
    }

    public static Double getDouble(ItemStack itemStack, String key) {
        double value = getValue(itemStack, key, PersistentDataType.DOUBLE);
        return value == -1 ? null : value;
    }

    public static boolean getBoolean(ItemStack itemStack, String key) {
        Boolean value = getValue(itemStack, key, PersistentDataType.BOOLEAN);
        return value != null && value;
    }

    public static void setString(ItemStack item, String key, String value) {
        edit(item, key, PersistentDataType.STRING, value);
    }
    public static void setInteger(ItemStack item, String key, int value) {
        edit(item, key, PersistentDataType.INTEGER, value);
    }

    public static void setDouble(ItemStack item, String key, double value) {
        edit(item, key, PersistentDataType.DOUBLE, value);
    }

    public static void setBoolean(ItemStack item, String key, boolean value) {
        edit(item, key, PersistentDataType.BOOLEAN, value);
    }

    public static ItemStack create(ItemStack item,String display, List<String> lore){
        ItemStack itemStack = item.clone();
        itemStack.editMeta(meta->{
            meta.setDisplayName(display);
            meta.setLore(lore);
        });
        return itemStack;
    }

    public static boolean isArmor(ItemStack itemStack) {
        if (itemStack == null ){
            return false;
        }
        switch (itemStack.getType()) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
            case TURTLE_HELMET:
                return true;
            default:
                return false;
        }
    }

    public static Map<String, Component> extractStat(ItemStack itemStack) {
        Map<String, Component> result = new HashMap<>();

        StatManager statManager = new StatManager();
        Collection<ItemStat<?, ?>> allStats = statManager.getAll();

        NBTItem nbt = NBTItem.get(itemStack);

        for (ItemStat<?, ?> stat : allStats) {
            String id = stat.getId(); // ex: ATTACK_DAMAGE
            String nbtKey = "MMOITEMS_" + id.toUpperCase();

            if (!nbt.hasTag(nbtKey)) continue;

            String value = null;
            try {
                // DoubleData, NumericStatData 등 대부분 숫자 기반
                value = String.valueOf(nbt.getDouble(nbtKey));
            } catch (Exception e) {
                try {
                    value = String.valueOf(nbt.getInteger(nbtKey));
                } catch (Exception e2) {
                    value = nbt.getString(nbtKey);
                }
            }

            result.put(id, Component.text(value));
        }
        return result;
    }

    public static String getArmorString(ItemStack item) {
        String armor_type = "";
        if (item.getType().name().contains("HELMET")){
            armor_type = "HELMET";
        }else if (item.getType().name().contains("CHESTPLATE")){
            armor_type = "CHESTPLATE";
        }else if (item.getType().name().contains("LEGGINGS")){
            armor_type = "LEGGINGS";
        }else if (item.getType().name().contains("BOOTS")){
            armor_type = "BOOTS";
        }
        return armor_type;
    }

    public static boolean isAcc(ItemStack item){
        Type type = Type.get(item);

        if (type == null) return false;

        if (type.equals(Type.ACCESSORY)) return true;
        return false;
    }
}

