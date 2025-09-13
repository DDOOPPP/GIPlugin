package org.gi.gICore.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class UserData implements Serializable {
    private UUID playerUUID;
    private String playerName;
    private double balance;
    private String guildName;
    private String className;

    public UserData(){

    }

    public boolean hasGuild(){
        if (guildName == null || guildName.equalsIgnoreCase("NONE")){
            return false;
        }
        return true;
    }
}
