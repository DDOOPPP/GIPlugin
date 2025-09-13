package org.gi.gICore.data.table;

public class TableQuery {
    public static final String ECONOMY_LOG = "economyLog";
    public static final String USER = "gi_user";
    public static final String GUILD = "gi_guild";
    public static final String GUILD_FUND = "gi_guild_fund";
    public static final String MAILBOX = "gi_mailbox";

    public static final String CREATE_ECON_LOG = "CREATE TABLE IF NOT EXISTS "+ECONOMY_LOG +" ("
            + "player_id VARCHAR(36) PRIMARY KEY,"
            + "player_name VARCHAR(50) NOT NULL,"
            + "type VARCHAR(20) NOT NULL,"
            + "amount DECIMAL(10,0) NOT NULL,"
            + "balance DECIMAL(10,0) NOT NULL,"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);";

    public static final String CREATE_GUILD_FUND = "CREATE TABLE IF NOT EXISTS "+GUILD_FUND +" ("
            + "guild_id VARCHAR(36) PRIMARY KEY,"
            + "player_name VARCHAR(50) NOT NULL,"
            + "type VARCHAR(20) NOT NULL,"
            + "amount DECIMAL(10,0) NOT NULL,"
            + "fund DECIMAL(10,0) NOT NULL,"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);";

    public static final String CREATE_USER = "CREATE TABLE IF NOT EXISTS "+USER +" ("
            + "player_id VARCHAR(36) PRIMARY KEY,"
            + "player_name VARCHAR(50) NOT NULL,"
            + "balance DECIMAL(10,0) NOT NULL DEFAULT 500,"
            + "guild VARCHAR(50) NOT NULL DEFAULT 'NONE',"
            + "class VARCHAR(50) NOT NULL DEFAULT '모험가',"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";

    public static final String CREATE_GUILD = "CREATE TABLE IF NOT EXISTS "+GUILD +" ("
            + "guild_id VARCHAR(36) PRIMARY KEY,"
            + "guild_name VARCHAR(50) NOT NULL,"
            + "guild_fund DECIMAL(10,0) NOT NULL,"
            + "member_count INTEGER NOT NULL DEFAULT 5,"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";
}
