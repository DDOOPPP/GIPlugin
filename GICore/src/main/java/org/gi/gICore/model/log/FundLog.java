package org.gi.gICore.model.log;

import lombok.Getter;
import org.gi.gICore.model.Enum;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class FundLog extends Log{
    private Enum.EconomyType economyType;
    private BigDecimal amount;
    private BigDecimal fund;

    public FundLog(UUID uuid, String userName, Enum.EconomyType economyType, BigDecimal amount, BigDecimal fund) {
        super(uuid, userName);
        this.economyType = economyType;
        this.amount = amount;
        this.fund = fund;
    }

    @Override
    public String toString() {
        return String.format("GUILD: [%s], NAME: [%s], TYPE: [%s], AMOUNT: [%s], BALANCE: [%s]"
                ,super.getUuid().toString()
                ,super.getUserName()
                ,economyType.name()
                ,amount.doubleValue()
                ,fund.doubleValue()
        );
    }
}
