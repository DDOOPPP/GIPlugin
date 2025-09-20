package org.gi.gICore.data.repository;

import org.gi.gICore.GILogger;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.guild.GuildLog;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class GuildLogRepository implements LogRepository<GuildLog, Connection> {
    private QueryBuilder builder;
    private GILogger logger;
    public GuildLogRepository() {
        builder = new QueryBuilder(TableQuery.GUILD_LOG);
        logger = new GILogger();
    }

    @Override
    public Result insert(GuildLog guildLog, Connection connection) {
        String query = builder.buildInsert(
                List.of("guild_id","member_id","event","amount")
        );

        try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1,guildLog.getGuildId().toString());
            statement.setString(2,guildLog.getPlayerId().toString());
            statement.setString(3,guildLog.getEvent().name());
            statement.setBigDecimal(4,guildLog.getAmount());

            return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR;

        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return Result.Exception(e);
        }
    }

    @Override
    public Result deleteAll(UUID key, Connection connection) {
        String query = builder.buildDelete("guild_id");

        try(PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, key.toString());

            return  statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return Result.Exception(e);
        }
    }

    @Override
    public Result delete(UUID key, Connection connection) {
        String query = builder.buildDelete("member_id");

        try(PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, key.toString());

            return  statement.executeUpdate() > 0 ? Result.SUCCESS : Result.ERROR;
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return Result.Exception(e);
        }
    }

    @Override
    public int serachCount(UUID key, Connection connection) {
        return 0;
    }

    @Override
    public Result deleteOverCount(UUID key, Connection connection) {
        return null;
    }
}
