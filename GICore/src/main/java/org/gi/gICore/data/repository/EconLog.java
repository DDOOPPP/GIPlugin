package org.gi.gICore.data.repository;

import org.gi.gICore.GILogger;
import org.gi.gICore.model.log.EconomyLog;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class EconLog implements LogRepository<EconomyLog,Connection> {
    QueryBuilder builder;
    GILogger logger;
    public EconLog() {
        builder = new QueryBuilder("economyLog");
        logger = new GILogger();
    }

    @Override
    public Result insert(EconomyLog econLog, Connection connection) {
        if (connection == null) {
            return Result.ERROR("Connection is null");
        }
        String query = builder.buildSelect(
                List.of("player_id","player_name","type","amount","balance")
        );
        Result result = Result.FAIL;
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,econLog.getUuid().toString());
            statement.setString(2,econLog.getUserName());
            statement.setString(3,econLog.getEconomyType().name());
            statement.setBigDecimal(4,econLog.getAmount());
            statement.setBigDecimal(5,econLog.getBalance());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return Result.ERROR("CREATE FAIL ECONOMY LOG");
        }finally {
            try{
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    @Override
    public Result deleteAll(UUID playerUUID, Connection connection) {
        if (connection == null) {
            return Result.ERROR("Connection is null");
        }
        String query = builder.buildDelete("player_id");
        Result result = Result.FAIL;
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,playerUUID.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return Result.ERROR("DELETE FAIL ECONOMY LOG");
        }finally {
            try{
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    @Override
    public Result delete(UUID key, Connection connection) {
        return null;
    }

    @Override
    public int serachCount(UUID key, Connection connection) {
        String query = builder.buildSelectCount(List.of("player_id"));
        try(PreparedStatement statement = connection.prepareStatement(query)){

        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
    }

    @Override
    public Result deleteOverCount(UUID key, Connection connection) {
        return null;
    }
}
