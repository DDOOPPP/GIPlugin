package org.gi.gICore.data.repository;

import org.apache.logging.log4j.message.ReusableMessage;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.table.TableQuery;

import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.guild.GuildRole;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class GuildMemberRepository {
    private QueryBuilder queryBuilder;
    private GILogger logger;
    private GuildLogRepository guildLogRepository;

    public GuildMemberRepository(){
        this.logger = new GILogger();
        this.queryBuilder = new QueryBuilder(TableQuery.GUILD_MEMBER);
        this.guildLogRepository = new GuildLogRepository();
    }

    public Result insertMember(GuildMember member, GuildRole.event event){
        Connection connection = null;
        String query = queryBuilder.buildInsert(
                List.of("guild_id","member_id","role")
        );

        try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

        } catch (SQLException e) {
            DataBaseConnection.rollback(connection);
            logger.error(e.getMessage());
            return Result.Exception(e);
        }
        return Result.SUCCESS;
    }

}
