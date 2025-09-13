package org.gi.gICore.model.guild;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Guild {
    private UUID uuid;
    private String name;
    private int memberCount;
    private BigDecimal fund;
    private List<UUID> members;

    public UUID getMember(UUID memberName){
        if(members.contains(memberName)){
            return memberName;
        }
        return null;
    }
}
