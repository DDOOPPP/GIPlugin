package org.gi.gICore.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.util.Builder;
import org.bukkit.Bukkit;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gICore.GICore;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.table.TableQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class DataBaseConnection {
    private static HikariDataSource dataSource;
    private static GILogger logger = new GILogger();
    private static int logCount;

    public static void connect(GIConfig config){
        DBConfig dbConfig = new DBConfig(config);
        if (dbConfig == null){
            logger.info("[DB] Database Not Use");
            return;
        }
        logger.info("Connecting to database...");

        String url = dbConfig.getURL();
        logCount = dbConfig.getLogCount();

        logger.info("[DB] URL: %s",url);
        logger.info("[DB] Save LogCount: %s",String.valueOf(logCount));
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(dbConfig.getUser());
        hikariConfig.setPassword(dbConfig.getPassword());

        hikariConfig.setConnectionTimeout(dbConfig.getConnectionTimeout());
        hikariConfig.setMaximumPoolSize(dbConfig.getMaxPoolSize());
        hikariConfig.setIdleTimeout(dbConfig.getIdleTimeout());
        hikariConfig.setMaximumPoolSize(dbConfig.getMaxPoolSize());
        hikariConfig.setMinimumIdle(dbConfig.getMinimumIdle());

        dataSource = new HikariDataSource(hikariConfig);
        Connection connection = null;
        try{
            connection = dataSource.getConnection();

            if (connection.isValid(5)){
                logger.info("[DB] Connection Successful");
                logger.info("[DB] Create Table...");
                if(!createTable(connection)){
                    Bukkit.getServer().getPluginManager().disablePlugin(GICore.getInstance());
                    return;
                }
            }
        }catch (SQLException e){
            logger.error("[DB] Connection Failed");
            logger.error(e.getMessage());
            return;
        }finally {
            try {
                if (connection != null){
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    private static boolean createTable(Connection connection){
        try{
            if (!connection.isClosed()){
                connection = dataSource.getConnection();
            }

            try(Statement statement = connection.createStatement()){

                statement.execute(TableQuery.CREATE_USER);
                statement.execute(TableQuery.CREATE_ECON_LOG);
                statement.execute(TableQuery.CREATE_GUILD);
                statement.execute(TableQuery.CREATE_GUILD_MEMBER);
                statement.execute(TableQuery.CREATE_GUILD_LOG);

                SQLWarning warn = statement.getWarnings();
                while (warn != null) {
                    System.err.printf("[SQLWarning] state=%s, code=%d, msg=%s%n",
                            warn.getSQLState(), warn.getErrorCode(), warn.getMessage());
                    warn = warn.getNextWarning();
                }

                if (warn != null){
                    return false;
                }
                logger.info("[DB] Create Table Successful...");
                return true;
            }
        } catch (SQLException e) {
            logger.error("[DB] Table Create Failed");
            logger.error(e.getMessage());
            return false;
        }finally {
            try {
                if (connection != null){
                    connection.close();
                }
            }catch (Exception e){

            }
        }
    }

    public static void rollback(Connection connection){
        try{
            if (connection != null){
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("[DB] Connection RollBack Failed");
            logger.error(e.getMessage());
        }
    }

    public static HikariDataSource getDataSource(){
        if (dataSource == null){
            connect(GICore.getDefaultConfig());
        }
        return dataSource;
    }

    public static int getLogCount(){
        return logCount;
    }
}
