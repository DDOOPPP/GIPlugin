package org.gi.gICore.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.Indyuce.mmocore.api.player.PlayerData;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@ToString
public class UserData implements Serializable {
    private UUID playerUUID;
    private String playerName;
    private BigDecimal balance;
    private String guildName;
    private String className;

    public UserData(){

    }

    public UserData(UUID playerUUID, String playerName, BigDecimal balance, String guildName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.balance = balance;
        this.guildName = guildName;
        PlayerData data = PlayerData.get(playerUUID);

        className = data.getProfess().getName();
    }

    public boolean hasGuild(){
        if (guildName == null || guildName.equalsIgnoreCase("NONE")){
            return false;
        }
        return true;
    }
}
