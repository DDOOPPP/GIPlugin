package org.gi.gICore.data.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gICore.GICore;

@Getter
public class DBConfig {
    private String database;
    private String host;
    private String port;
    private String user;
    private String password;

    private int maxPoolSize;
    private int minimumIdle;
    private long maxLifetime;
    private long idleTimeout;
    private long connectionTimeout;

    private int logCount;
    public DBConfig(GIConfig config) {
        database = config.getString("database","NONE");
        if (database.equals("NONE")) {
            return;
        }
        logCount = config.getInt("log_count",5);
        host = config.getString("mysql.host","localhost");
        port = config.getString("port","3306");
        user = config.getString("user","root");
        password = config.getString("mysql.password","");
        maxPoolSize = config.getInt("mysql.maxPoolSize",10);
        minimumIdle = config.getInt("mysql.minimumIdle",5);
        maxLifetime = config.getLong("mysql.maxLifetime",30000);
        idleTimeout = config.getLong("mysql.idleTimeout",30000);
        connectionTimeout = config.getLong("mysql.connectionTimeout",30000);
    }

    public String getURL(){
        return  "jdbc:mysql://"+host+":"+port+"/"+database;
    }
}
