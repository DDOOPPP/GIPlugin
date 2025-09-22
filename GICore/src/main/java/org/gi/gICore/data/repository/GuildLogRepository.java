package org.gi.gICore.data.repository;

import org.gi.gICore.GILogger;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.guild.GuildLog;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class GuildLogRepository {
    private QueryBuilder builder;
    private GILogger logger;

    public GuildLogRepository() {
        this.builder = new QueryBuilder(TableQuery.GUILD_LOG);
        this.logger = new GILogger();
    }

    public Result insert(GuildLog guildLog, Connection connection) {
        String query = builder.buildInsert(
                List.of("guild_id","member_id","event","amount")
        );

        Result result = Result.FAIL;

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,guildLog.getGuildId().toString());
            statement.setString(2,guildLog.getPlayerId().toString());
            statement.setString(3,guildLog.getEvent().name());
            statement.setBigDecimal(4,guildLog.getAmount());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }
}
