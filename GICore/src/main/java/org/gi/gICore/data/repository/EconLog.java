package org.gi.gICore.data.repository;

import org.gi.gIAPI.util.ResourceUtil;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
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
    int count;
    public EconLog() {
        builder = new QueryBuilder("economyLog");
        logger = new GILogger();
        count = DataBaseConnection.getLogCount();
    }

    @Override
    public Result insert(EconomyLog econLog, Connection connection) {
        if (connection == null) {
            return Result.ERROR("Connection is null");
        }
        String query = builder.buildInsert(
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
            if (result.isSuccess()) {
                return delete(econLog.getUuid(),connection);
            }
        } catch (SQLException e) {
            return Result.ERROR(e.getMessage());
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
            return Result.ERROR(e.getMessage());
        }
        return result;
    }

    @Override
    public Result delete(UUID key, Connection connection) {
        int log_count = serachCount(key, connection);
        if (log_count > 0) {
            logger.info("Count: %s",String.valueOf(count));
        }
        Result result = Result.FAIL;
        if (log_count >= count) {
            result = deleteOverCount(key, connection);
        }else{
            return Result.SUCCESS;
        }
        return result;
    }

    @Override
    public int serachCount(UUID key, Connection connection) {
        String query = builder.buildSelectCount(List.of("player_id"));
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,key.toString());

            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return -1;
        }
        return -1;
    }

    @Override
    public Result deleteOverCount(UUID key, Connection connection) {
        String query = builder.deleteOlderKeepNForKey("player_id",count);
        Result result = Result.FAIL;

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,key.toString());
            statement.setInt(2,count);

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            logger.error(e.getMessage());

            return Result.ERROR(e.getMessage());
        }
        return result;
    }
}
