package org.gi.gICore.data.repository;

import org.apache.logging.log4j.message.ReusableMessage;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.table.TableQuery;

import org.gi.gICore.model.guild.GuildLog;
import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.guild.GuildRole;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildMemberRepository {
    private QueryBuilder queryBuilder;
    private GILogger logger;
    private GuildLogRepository guildLogRepository;

    public GuildMemberRepository(){
        this.logger = new GILogger();
        this.queryBuilder = new QueryBuilder(TableQuery.GUILD_MEMBER);
        this.guildLogRepository = new GuildLogRepository();
    }

    public Result insertMember(GuildMember member){
        Connection connection = null;
        String query = queryBuilder.buildInsert(
                List.of("guild_id","member_id","role")
        );
        Result result = null;
        try{
            connection = DataBaseConnection.getDataSource().getConnection();
            connection.setAutoCommit(false);
            try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1,member.getGuildId().toString());
                statement.setString(2,member.getUserId().toString());
                statement.setString(3,member.getRole().name());

                result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

                if (!result.isSuccess()){
                    DataBaseConnection.rollback(connection);
                    return result;
                }

                GuildLog log = new GuildLog(
                        member.getGuildId(),
                        member.getUserId(),
                        GuildRole.event.JOIN,
                        BigDecimal.valueOf(0)
                );

                result = guildLogRepository.insert(log,connection);

                if (!result.isSuccess()){
                    DataBaseConnection.rollback(connection);
                    logger.error(result.getMsg());
                    return result;
                }
                connection.commit();
            }
        } catch (SQLException e) {
            DataBaseConnection.rollback(connection);
            logger.error(e.getMessage());
            return Result.Exception(e);
        }finally {
            try {
                if (connection != null){
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    public Result deleteMember(UUID player_id){
        String query = queryBuilder.buildDelete("member_id");
        Connection connection = null;
        Result result = null;

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1,player_id.toString());
                result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

                return result;
            }
        } catch (SQLException e) {
            DataBaseConnection.rollback(connection);
            logger.error(e.getMessage());
        }finally {
            try {
                if (connection != null){
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    public Result updateMember(UUID player_uuid,UUID guild_id, GuildRole.event event, BigDecimal amount){
        Connection connection = null;
        Result result = null;

        GuildLog guildLog = new GuildLog(
                guild_id,
                player_uuid,
                event,
                amount
        );
        try{
            connection = DataBaseConnection.getDataSource().getConnection();
            connection.setAutoCommit(false);

            result = guildLogRepository.insert(guildLog,connection);

            if (!result.isSuccess()){
                DataBaseConnection.rollback(connection);
                return result;
            }
        } catch (SQLException e) {
            DataBaseConnection.rollback(connection);
            return  Result.Exception(e);
        }
        return result;
    }

    public GuildMember getMember(UUID player_id){
        String query = queryBuilder.buildSelectSingle("member_id");
        GuildMember guildMember = null;
        try(Connection connection = DataBaseConnection.getDataSource().getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1,player_id.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()){
                    guildMember = new GuildMember(
                            player_id,
                            UUID.fromString(resultSet.getString("guild_id")),
                            GuildRole.Role.valueOf(resultSet.getString("role"))
                    );
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return guildMember;
    }

    public List<GuildMember> getMembers(UUID guild_id){
        String query = queryBuilder.buildSelectSingle("guild_id");
        List<GuildMember> members = new ArrayList<>();
        try(Connection connection = DataBaseConnection.getDataSource().getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

                statement.setString(1,guild_id.toString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()){
                    members.add(new GuildMember(
                            UUID.fromString(resultSet.getString("member_id")),
                            guild_id,
                            GuildRole.Role.valueOf(resultSet.getString("role"))
                    ));
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return members;
    }
}
