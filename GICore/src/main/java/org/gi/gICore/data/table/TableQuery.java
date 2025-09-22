package org.gi.gICore.data.table;

public class TableQuery {
    public static final String ECONOMY_LOG = "economyLog";
    public static final String USER = "gi_user";
    public static final String GUILD = "gi_guild";
    public static final String GUILD_MEMBER = "gi_guild_member";
    public static final String GUILD_LOG = "gi_guild_log";
    public static final String MAILBOX = "gi_mailbox";

    public static final String CREATE_ECON_LOG = "CREATE TABLE IF NOT EXISTS "+ECONOMY_LOG +" ("
            + "id AUTO_INCREMENT PRIMARY KEY,"
            + "player_id VARCHAR(36) NOT NULL,"
            + "player_name VARCHAR(50) NOT NULL,"
            + "type VARCHAR(20) NOT NULL,"
            + "amount DECIMAL(10,0) NOT NULL,"
            + "balance DECIMAL(10,0) NOT NULL,"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);";

    public static final String CREATE_USER = "CREATE TABLE IF NOT EXISTS "+USER +" ("
            + "player_id VARCHAR(36) PRIMARY KEY,"
            + "player_name VARCHAR(50) NOT NULL,"
            + "balance DECIMAL(10,0) NOT NULL DEFAULT 500,"
            + "guild VARCHAR(50) NOT NULL DEFAULT 'NONE',"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";

    public static final String CREATE_GUILD = "CREATE TABLE IF NOT EXISTS "+GUILD +" ("
            + "guild_id VARCHAR(36) PRIMARY KEY,"
            + "guild_name VARCHAR(50) NOT NULL UNIQUE,"
            + "owner_id VARCHAR(36) NOT NULL,"
            + "fund DECIMAL(10,0) NOT NULL DEFAULT 0,"
            + "level INTEGER NOT NULL DEFAULT 1,"
            + "exp DECIMAL(10,0) NOT NULL DEFAULT 0,"
            + "notice VARCHAR(100) NOT NULL DEFAULT '',"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY (owner_id) REFERENCES "+ USER +"(player_id) ON DELETE CASCADE)";

    public static final String CREATE_GUILD_MEMBER =  "CREATE TABLE IF NOT EXISTS "+GUILD_MEMBER+" ("
            + "guild_id VARCHAR(36) NOT NULL,"
            + "member_id VARCHAR(36) NOT NULL UNIQUE,"
            + "role VARCHAR(20) NOT NULL,"
            + "joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY (guild_id, member_id),"
            + "FOREIGN KEY (guild_id)  REFERENCES "+GUILD+"(guild_id) ON DELETE CASCADE,"
            + "FOREIGN KEY (member_id)  REFERENCES "+USER+"(player_id) ON DELETE CASCADE"
            + ");";

    public static final String CREATE_GUILD_LOG = "CREATE TABLE IF NOT EXISTS "+GUILD_LOG+" ("
            + "guild_id VARCHAR(36) NOT NULL,"
            + "member_id VARCHAR(36) NOT NULL,"
            + "event VARCHAR(20) NOT NULL,"
            + "amount DECIMAL(10,0) NOT NULL,"
            + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY (guild_id, member_id),"
            + "FOREIGN KEY (guild_id)  REFERENCES " + GUILD + "(guild_id) ON DELETE CASCADE,"
            + "FOREIGN KEY (member_id) REFERENCES "+USER+"(player_id) ON DELETE CASCADE"
            + ")";

}
