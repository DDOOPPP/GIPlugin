package org.gi.gICore.model.log;

import lombok.Getter;
import org.gi.gICore.model.Enum;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class EconomyLog extends Log{
    private Enum.EconomyType economyType;
    private BigDecimal amount;
    private BigDecimal balance;

    public EconomyLog(UUID uuid, String userName, Enum.EconomyType economyType, BigDecimal amount, BigDecimal balance) {
        super(uuid, userName);
        this.economyType = economyType;
        this.amount = amount;
        this.balance = balance;
    }
    @Override
    public String toString() {
        return String.format("UUID: [%s], NAME: [%s], TYPE: [%s], AMOUNT: [%s], BALANCE: [%s]"
                ,super.getUuid().toString()
                ,super.getUserName()
                ,economyType.name()
                ,amount.doubleValue()
                ,balance.doubleValue()
        );
    }
}
