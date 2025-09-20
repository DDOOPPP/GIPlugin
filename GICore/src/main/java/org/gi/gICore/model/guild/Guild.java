package org.gi.gICore.model.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class Guild {
    private UUID guildId;
    private String guildName;
    private UUID ownerId;
    private BigDecimal fund;
    private int level;
    private BigDecimal exp;
    private String notice;

    public boolean isOwner(UUID ownerId) {
        return this.ownerId.equals(ownerId);
    }
}
