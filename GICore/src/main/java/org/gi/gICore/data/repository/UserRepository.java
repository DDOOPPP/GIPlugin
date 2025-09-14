package org.gi.gICore.data.repository;

import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.Enum;
import org.gi.gICore.model.log.EconomyLog;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserRepository implements Repository<UserData, UUID> {
    QueryBuilder builder;
    GILogger logger;
    EconLog econLog;
    public UserRepository() {
        builder = new QueryBuilder(TableQuery.USER);
        logger = new GILogger();
        econLog = new EconLog();
    }

    @Override
    public Result insert(UserData data) {
        Connection conn = null;
        Result result = Result.FAIL;
        String query = builder.buildSelectCount(
                List.of("player_id, player_name, balance, guild")
        );

        try{
            conn = DataBaseConnection.getDataSource().getConnection();

            conn.setAutoCommit(false);

            try(PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1,data.getPlayerUUID().toString());
                statement.setString(2,data.getPlayerName());
                statement.setBigDecimal(3,data.getBalance());
                statement.setString(4,data.getGuildName());

                result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;


                if (result.isSuccess()){
                    EconomyLog economyLog = new EconomyLog(
                            data.getPlayerUUID(),
                            data.getPlayerName(),
                            Enum.EconomyType.CREATE,
                            data.getBalance(),
                            data.getBalance()
                    );

                    result = econLog.insert(economyLog,conn);
                }else {
                    logger.error("UserData Insert Fail");
                    DataBaseConnection.rollback(conn);
                    return Result.FAIL;
                }

                if (!result.isSuccess()){
                    logger.error(result.getMsg());
                    DataBaseConnection.rollback(conn);
                    return Result.FAIL;
                }
                conn.commit();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            DataBaseConnection.rollback(conn);
            return Result.ERROR("UserData Insert Fail");
        }finally {
            try {
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    @Override
    public Result update(UserData data) {
        return null;
    }

    @Override
    public Result delete(UUID key) {
        Connection conn = null;
        String query = builder.buildDelete("player_id");
        Result result = Result.FAIL;
        try{
            conn = DataBaseConnection.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try(PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1,key.toString());

                boolean isSuccess = statement.executeUpdate() > 0 ? true : false;

                if (isSuccess){
                    result = econLog.deleteAll(key,conn);
                }else {
                    logger.error("UserData Delete Fail");
                    DataBaseConnection.rollback(conn);
                    return Result.FAIL;
                }

                if (!result.isSuccess()){
                    DataBaseConnection.rollback(conn);
                    logger.error(result.getMsg());
                    return result;
                }
            }
            conn.commit();
        } catch (SQLException e) {

            logger.error(e.getMessage());
            DataBaseConnection.rollback(conn);
            return Result.ERROR("UserData Delete Fail");
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {

            }
        }

        return result;
    }

    public Result updatePlayerName(UUID key, String playerName) {
        String query = builder.buildUpdate("player_name", "player_id");
        Result result = Result.FAIL;
        Connection conn = null;

        try{
            conn = DataBaseConnection.getDataSource().getConnection();

            try(PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1,playerName);
                statement.setString(2,key.toString());

                result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            DataBaseConnection.rollback(conn);
            return Result.ERROR("UserData Update By Name Fail");
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    public Result updateBalance(UUID key, EconomyLog economyLog) {
        String query = builder.buildUpdate("balance", "player_id");
        Result result = Result.FAIL;
        Connection conn = null;
        try{
            conn = DataBaseConnection.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try(PreparedStatement statement = conn.prepareStatement(query)){
                statement.setBigDecimal(1,economyLog.getBalance());
                statement.setString(2,key.toString());

                boolean isSuccess = statement.executeUpdate() > 0;

                if (isSuccess){
                    result = econLog.insert(economyLog,conn);

                    if (!result.isSuccess()){
                        logger.error(result.getMsg());
                        DataBaseConnection.rollback(conn);
                        return Result.ERROR("UserData Update Fail");
                    }
                }else {
                    logger.error("UserData Update Fail");
                    DataBaseConnection.rollback(conn);
                    return Result.FAIL;
                }

                conn.commit();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            DataBaseConnection.rollback(conn);
            return Result.ERROR("UserData Update By Balance Fail");
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    public Result updateGuild(UUID key, String guildName) {
        String query = builder.buildUpdate("guild", "player_id");
        Result result = Result.FAIL;
        Connection conn = null;
        try{
            conn = DataBaseConnection.getDataSource().getConnection();

            try(PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1,guildName);
                statement.setString(2,key.toString());

                result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

                if (!result.isSuccess()){
                        logger.error("UserData Delete Fail");
                        DataBaseConnection.rollback(conn);
                        return Result.FAIL;
                }


            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            DataBaseConnection.rollback(conn);
            return Result.ERROR("UserData Update By Guild Fail");
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {

            }
        }
        return result;
    }

    @Override
    public UserData find(UUID key) {
        Connection conn = null;
        UserData userData = null;
        String query = builder.buildSelectSingle("player_id");

        try{
            conn = DataBaseConnection.getDataSource().getConnection();

            try(PreparedStatement statement = conn.prepareStatement(query)){
                statement.setString(1,key.toString());

                try(ResultSet resultSet = statement.executeQuery()){
                    userData = new UserData(
                            key,
                            resultSet.getString("player_name"),
                            resultSet.getBigDecimal("balance"),
                            resultSet.getString("guild")
                    );

                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            Result.ERROR("UserData Select Fail");
            return userData;
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {

            }
        }
        return userData;
    }

    @Override
    public List<UserData> findAll() {
        List<UserData> userDataList = new ArrayList<UserData>();
        Connection conn = null;
        Result result = Result.FAIL;
        String query = builder.buildSelectAll();

        try{
            conn = DataBaseConnection.getDataSource().getConnection();

            try(PreparedStatement statement = conn.prepareStatement(query)){


                try(ResultSet resultSet = statement.executeQuery()){
                    while (resultSet.next()){
                        userDataList.add(new UserData(
                                UUID.fromString(resultSet.getString("player_id")),
                                resultSet.getString("player_name"),
                                resultSet.getBigDecimal("balance"),
                                resultSet.getString("guild")
                        ));
                    }
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());
            Result.ERROR("UserData Select Fail");
            return null;
        }finally {
            try{
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {

            }
        }
        return userDataList;
    }
}
