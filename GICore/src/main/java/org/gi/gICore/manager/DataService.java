package org.gi.gICore.manager;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.OfflinePlayer;
import org.gi.gICore.model.values.ValueName;

import java.util.HashMap;
import java.util.Map;

public class DataService {
    public static Map<String,Object> getPlayerData(OfflinePlayer player){
        Map<String,Object> data = new HashMap<>();
        PlayerData pd = PlayerData.get(player);

        data.put(ValueName.PLAYER_PROFESSION,pd.getProfess().getName());
        data.put(ValueName.PLAYER_LEVEL,pd.getLevel());

        return data;
    }

    private static double getBase(OfflinePlayer player,String stat){
        PlayerData data = PlayerData.get(player);

        return data.getStats().getBase(stat);
    }

    private static double getExtra(OfflinePlayer player,String stat){
        PlayerData data = PlayerData.get(player);

        return data.getStats().getStat(stat) - data.getStats().getBase(stat);
    }
}
