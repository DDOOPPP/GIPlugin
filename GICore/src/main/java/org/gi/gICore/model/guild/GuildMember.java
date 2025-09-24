package org.gi.gICore.model.guild;

import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.nio.Buffer;
import java.time.OffsetDateTime;
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

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(this.userId);
    }
}
