package org.gi.gICore.model.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gi.gIAPI.component.adapter.GIConfig;

import java.math.BigDecimal;
import java.util.BitSet;
import java.util.UUID;

@Getter
public class GuildSetting {
    private BigDecimal createValue;
    private int defaultMemberCount;
    private int maxMemberCount;

    public GuildSetting(GIConfig config) {
        double value = config.getDouble("guild.createValue",100000);
        createValue = BigDecimal.valueOf(value);
        defaultMemberCount = config.getInt("guild.defaultMemberCount",5);
        maxMemberCount = config.getInt("guild.maxMemberCount",5);
    }
}
