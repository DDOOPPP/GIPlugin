package org.gi.gICore.data.repository;

import it.unimi.dsi.fastutil.floats.FloatObjectImmutablePair;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.gi.gIAPI.util.ResourceUtil;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.guild.Guild;
import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.guild.GuildRole;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GuildRepository {
    private QueryBuilder builder;
    private GILogger logger;
    private GuildMemberRepository guildMemberRepository;

    public GuildRepository(){
        this.builder = new QueryBuilder(TableQuery.GUILD);
        this.logger = new GILogger();
        this.guildMemberRepository = new GuildMemberRepository();
    }

    public Result createGuild(Guild guild){
        Connection connection = null;
        String query = builder.buildInsert(
                List.of("guild_id","guild_name","owner_id","fund","level","exp","notice")
        );
        Result result = Result.FAIL;

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, guild.getGuildId().toString());
                statement.setString(2, guild.getGuildName());
                statement.setString(3, guild.getOwnerId().toString());
                statement.setBigDecimal(4, guild.getFund());
                statement.setInt(5, guild.getLevel());
                statement.setBigDecimal(6, guild.getExp());
                statement.setString(7, guild.getNotice());

                result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
                if (result.isSuccess()){
                    GuildMember member = new GuildMember(guild.getOwnerId(),guild.getGuildId(), GuildRole.Role.OWNER);

                    result = guildMemberRepository.insertMember(member);
                }

                return result;
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            DataBaseConnection.rollback(connection);
            return Result.Exception(e);
        }finally{
            try{
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public Result deleteGuild(UUID guildId){
        Connection connection = null;
        String query = builder.buildDelete("guild_id");
        Result result = Result.FAIL;
        try{
           connection = DataBaseConnection.getDataSource().getConnection();

           try(PreparedStatement statement = connection.prepareStatement(query)){
               statement.setString(1, guildId.toString());

               return statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
           }
        } catch (SQLException e) {
            DataBaseConnection.rollback(connection);
            return Result.Exception(e);
        }finally{
            try{
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public Guild getGuild(String guildName){
        Guild guild = null;
        String query = builder.buildSelectSingle("guild_name");

        try(Connection connection = DataBaseConnection.getDataSource().getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, guildName);

                ResultSet resultSet = statement.executeQuery();

                if(resultSet.next()){
                    guild = new  Guild(
                            UUID.fromString(resultSet.getString("guild_id")),
                            guildName,
                            UUID.fromString(resultSet.getString("owner_id")),
                            resultSet.getBigDecimal("fund"),
                            resultSet.getInt("level"),
                            resultSet.getBigDecimal("exp"),
                            resultSet.getString("notice")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return null;
        }
        return guild;
    }

    public Guild find (UUID guildId){
        Guild guild = null;
        String query = builder.buildSelectSingle("guild_id");

        try(Connection connection = DataBaseConnection.getDataSource().getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, guildId.toString());

                ResultSet resultSet = statement.executeQuery();

                if(resultSet.next()){
                    guild = new  Guild(
                            guildId,
                            resultSet.getString("guild_name"),
                            UUID.fromString(resultSet.getString("owner_id")),
                            resultSet.getBigDecimal("fund"),
                            resultSet.getInt("level"),
                            resultSet.getBigDecimal("exp"),
                            resultSet.getString("notice")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return null;
        }
        return guild;
    }

    public List<Guild> findAll(){
        List<Guild> guilds = new ArrayList<>();
        String query = builder.buildSelectAll();

        try(Connection connection = DataBaseConnection.getDataSource().getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    guilds.add(new Guild(
                            UUID.fromString(resultSet.getString("guild_id")),
                            resultSet.getString("guild_name"),
                            UUID.fromString(resultSet.getString("owner_id")),
                            resultSet.getBigDecimal("fund"),
                            resultSet.getInt("level"),
                            resultSet.getBigDecimal("exp"),
                            resultSet.getString("notice")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("SQLException: ", e);
            return List.of();
        }
        return guilds;
    }

    public Result updateGuildFund(UUID guildId, BigDecimal fund, Player player, GuildRole.event event,BigDecimal amount){
        Connection connection = null;
        String query = builder.buildUpdate("guild_id","fund");
        Result result = Result.FAIL;
        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setBigDecimal(1, fund);
                statement.setString(2, guildId.toString());

                result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

                if (!result.isSuccess()){
                    DataBaseConnection.rollback(connection);
                    result = Result.ERROR("Update Failed");
                    return  result;
                }

                result = guildMemberRepository.updateMember(player.getUniqueId(),guildId,event,amount);
                return result;
            }
        } catch (SQLException e) {
            DataBaseConnection.rollback(connection);
            return Result.Exception(e);
        }finally{
            try{
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public Result updateGuildExp(UUID guildId, BigDecimal exp, GuildMember member){
        return Result.FAIL;
    }

    public Result updateGuildLevel(UUID guildId, GuildMember member){
        return Result.FAIL;
    }
}
