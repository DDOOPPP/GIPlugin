package org.gi.gICore.model.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class GuildMember {
    private UUID userId;
    private UUID guildId;
    private GuildRole.Role role;

    public boolean isOwner(){
        return role.equals(GuildRole.Role.OWNER);
    }

    public boolean isManager(){
        return role.equals(GuildRole.Role.OWNER) ||  role.equals(GuildRole.Role.SUB_OWNER);
    }
}
