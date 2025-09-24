package org.gi.gICore.data.repository;

import io.lumine.mythic.bukkit.utils.lib.jooq.User;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.Enum;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.lang.ref.PhantomReference;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UserRepository {
    private QueryBuilder builder;
    private GILogger logger;

    public UserRepository() {
        builder = new QueryBuilder(TableQuery.USER);
        logger = new GILogger();
    }

    public Result insert(UserData user, Connection connection) {
        Result result = Result.FAIL;
        String query = builder.buildInsert(
                List.of("player_id","player_name","balance","guild")
        );

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,user.getPlayerUUID().toString());
            statement.setString(2,user.getPlayerName());
            statement.setBigDecimal(3,user.getBalance());
            statement.setString(4,user.getGuildName());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public UserData find(UUID uuid, Connection connection) {
        UserData user = null;
        String query = builder.buildSelectSingle("player_id");

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,uuid.toString());

            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    user = new UserData(
                            uuid,
                            resultSet.getString("player_name"),
                            resultSet.getBigDecimal("balance"),
                            resultSet.getString("guild")
                    );
                }
            }
        } catch (SQLException e) {
            Result.Exception(e);
            return null;
        }
        return user;
    }

    public Result updateBalance(UUID playerId, BigDecimal balance, Connection connection, Enum.EconomyType type) {
        Result result = Result.FAIL;
        String query = "";

        switch (type) {
            case CREATE: return Result.ERROR("Long Type");
            case DEPOSIT: query = builder.depositQuery("balance","player_id"); break;
            case WITHDRAW: query = builder.withDrawQuery("balance","player_id"); break;
        }
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setBigDecimal(1,balance);
            statement.setString(2,playerId.toString());

            if (type == Enum.EconomyType.WITHDRAW) {
                statement.setBigDecimal(3,balance);
            }

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result updateGuildName(UUID playerId, String guildName, Connection connection) {
        Result result = Result.FAIL;
        String query = builder.buildUpdate("player_id","guild");

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,guildName);
            statement.setString(2,playerId.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }


    public Result delete(UUID playerId, Connection connection) {
        Result result = Result.FAIL;
        String query = builder.buildDelete("player_id");

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,playerId.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }
}
