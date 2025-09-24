package org.gi.gICore.data.repository;

import javafx.scene.layout.BackgroundImage;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.Enum;
import org.gi.gICore.model.guild.Guild;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public class GuildRepository {
    private QueryBuilder builder;
    private GILogger logger;

    public GuildRepository() {
        this.builder = new QueryBuilder(TableQuery.GUILD);
        this.logger = new GILogger();
    }

    public Result insert(Guild guild, Connection connection) {
        String query = builder.buildInsert(
                List.of("guild_id","guild_name","owner_id","fund","level","exp","notice")
        );
        Result result = Result.FAIL;
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, guild.getGuildId().toString());
            statement.setString(2, guild.getGuildName());
            statement.setString(3, guild.getOwnerId().toString());
            statement.setBigDecimal(4, guild.getFund());
            statement.setInt(5, guild.getLevel());
            statement.setBigDecimal(6, guild.getExp());
            statement.setString(7, guild.getNotice());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result updateFund(UUID guildID, BigDecimal amount, Enum.EconomyType type, Connection connection) {
        String query = "";
        switch (type){
            case DEPOSIT:
                query = builder.depositQuery("fund","guild_id");
                break;
            case WITHDRAW:
                query = builder.withDrawQuery("fund","guild_id");
                break;
        }
        Result result = Result.FAIL;

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setBigDecimal(1, amount);
            statement.setString(2, guildID.toString());

            if (type == Enum.EconomyType.WITHDRAW) {
                statement.setBigDecimal(3, amount);
            }

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result updateExp(UUID guildID, BigDecimal exp, Connection connection) {
        String query = builder.buildUpdate("guild_id","exp");
        Result result = Result.FAIL;

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setBigDecimal(1, exp);
            statement.setString(2, guildID.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result updateLevel(UUID guildID, Integer level, Connection connection) {
        String query = builder.buildUpdate("guild_id","level");
        Result result = Result.FAIL;

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, level);
            statement.setString(2, guildID.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result updateNotice(UUID guildID, String notice, Connection connection) {
        String query = builder.buildUpdate("guild_id","notice");
        Result result = Result.FAIL;

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, notice);
            statement.setString(2, guildID.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result delete(UUID guildID, Connection connection) {
        String query = builder.buildDelete("guild_id");
        Result result = Result.FAIL;

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,guildID.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Guild find(UUID guildID, Connection connection) {
        String query = builder.buildSelectSingle("guild_id");
        Guild guild = null;

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,guildID.toString());

            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    guild = new Guild(
                            guildID,
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
            Result.Exception(e);
            return null;
        }
        return guild;
    }

    public Guild find(String guildName, Connection connection) {
        String query = builder.buildSelectSingle("guild_name");
        Guild guild = null;

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,guildName);

            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    guild = new Guild(
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
            Result.Exception(e);
            return null;
        }
        return guild;
    }
}
