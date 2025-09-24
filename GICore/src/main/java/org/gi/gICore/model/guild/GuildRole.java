package org.gi.gICore.model.guild;

public class GuildRole {
    public enum Role {
        OWNER, SUB_OWNER, MEMBER;
    }

    public enum event{
        JOIN, LEAVE, LEVEL_UP, DEPOSIT, WITHDRAW, NEW, KICK, ROLE_CHANGE ,ROLE_SUB_OWNER,ROLE_MEMBER;
    }
}
