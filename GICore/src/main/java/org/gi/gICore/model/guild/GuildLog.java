package org.gi.gICore.model.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GuildLog {
    private UUID guildId;
    private UUID playerId;
    private GuildRole.event event;
    private BigDecimal amount;
}
