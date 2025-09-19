package org.gi.gICore.data.repository;

import org.gi.gIAPI.util.ResourceUtil;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.log.FundLog;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class GuildFundRepository implements LogRepository<FundLog, Connection> {
    private QueryBuilder builder;
    private GILogger logger;
    private int count = 0;
    public GuildFundRepository() {
        builder = new QueryBuilder(TableQuery.GUILD_FUND);
        logger = new GILogger();

        count = DataBaseConnection.getLogCount();
    }

    @Override
    public Result insert(FundLog fundLog, Connection connection) {
        String query = builder.buildInsert(
                List.of("guild_id","player_name","type","amount","fund")
        );
        Result result = Result.FAIL;
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, fundLog.getUuid().toString());
            statement.setString(2, fundLog.getUserName());
            statement.setString(3, fundLog.getEconomyType().name());
            statement.setBigDecimal(4, fundLog.getAmount());
            statement.setBigDecimal(4, fundLog.getFund());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

            if (!result.isSuccess()) {
                return result;
            }

            return delete(fundLog.getUuid(), connection);

        } catch (SQLException e) {
            logger.error("Insert Log failed");
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Result deleteAll(UUID key, Connection connection) {
        String query = builder.buildDelete("guild_id");
        Result result = Result.FAIL;
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, key.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

        } catch (SQLException e) {
            logger.error("Delete Log failed");
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Result delete(UUID key, Connection connection) {
        int select = serachCount(key, connection);
        if (select < 0) {
            return Result.ERROR("Count is -1");
        }

        if (select > count) {
            return deleteOverCount(key, connection);
        }else{
            return Result.SUCCESS;
        }
    }

    @Override
    public int serachCount(UUID key, Connection connection) {
        String query = builder.buildSelectCount(List.of("guild_id"));

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, key.toString());
            statement.setInt(2, count);
            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.error("Search Count failed");
            logger.error(e.getMessage());
        }
        return -1;
    }

    @Override
    public Result deleteOverCount(UUID key, Connection connection) {
        String query = builder.deleteOlderKeepNForKey("guild_id");
        Result result = Result.FAIL;
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, key.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            logger.error("Delete OverCount failed");
            logger.error(e.getMessage());
        }
        return result;
    }
}
