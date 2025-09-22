package org.gi.gICore.manager;

import io.lumine.mythic.bukkit.utils.lib.jooq.User;
import javafx.beans.property.ReadOnlySetProperty;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.repository.EconLogRepository;
import org.gi.gICore.data.repository.UserRepository;
import org.gi.gICore.model.Enum;
import org.gi.gICore.model.log.EconomyLog;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UserService {
    public static UserService instance;
    private UserRepository userRepository;
    private EconLogRepository logRepository;
    private GILogger logger;
    public UserService() {
        userRepository = new UserRepository();
        logRepository = new EconLogRepository();
        logger = new GILogger();

        instance = this;
    }

    public Result create(UserData userData) {
        Connection connection = null;
        try{
            connection = DataBaseConnection.getDataSource().getConnection();
            connection.setAutoCommit(false);

            Result result = userRepository.insert(userData,connection);

            EconomyLog log = new EconomyLog(
                    userData.getPlayerUUID(),
                    userData.getPlayerName(),
                    Enum.EconomyType.CREATE,
                    userData.getBalance(),
                    userData.getBalance()
            );

            Result logResult = logRepository.insert(log,connection);

            if (result.isSuccess() && logResult.isSuccess()) {
                connection.commit();
                return Result.SUCCESS;
            }

            DataBaseConnection.rollback(connection);
        }  catch (SQLException e) {
            if (connection != null) {
                DataBaseConnection.rollback(connection);
            }
            return Result.Exception(e);
        }finally {
            if (connection != null) {
                DataBaseConnection.disconnect(connection);
            }
        }
        return Result.FAIL;
    }

    public boolean isExist(UUID uuid) {
        return getUserData(uuid) != null;
    }

    public UserData getUserData(UUID uuid) {
        Connection connection = null;
        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            return userRepository.find(uuid,connection);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }finally {
            if (connection != null) {
                DataBaseConnection.disconnect(connection);
            }
        }
    }

    public Result updateBalance (UUID uuid, EconomyLog log) {
        Connection connection = null;

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            connection.setAutoCommit(false);

            Result result = userRepository.updateBalance(log.getUuid(),log.getBalance(),connection);

            Result logResult = logRepository.insert(log,connection);

            if (result.isSuccess() && logResult.isSuccess()) {
                connection.commit();
                return Result.SUCCESS;
            }

            DataBaseConnection.rollback(connection);
            return Result.FAIL;

        }  catch (SQLException e) {
            if (connection != null) {
                DataBaseConnection.rollback(connection);
            }
            return Result.Exception(e);
        }finally {
            if (connection != null) {
                DataBaseConnection.disconnect(connection);
            }
        }
    }

    public Result updateGuildName (UUID uuid, String guildName) {
        Connection connection = null;

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            connection.setAutoCommit(false);

            Result result = userRepository.updateGuildName(uuid,guildName,connection);

            if (result.isSuccess()) {
                connection.commit();
                return Result.SUCCESS;
            }

            DataBaseConnection.rollback(connection);
            return Result.FAIL;

        }  catch (SQLException e) {
            if (connection != null) {
                DataBaseConnection.rollback(connection);
            }
            return Result.Exception(e);
        }finally {
            if (connection != null) {
                DataBaseConnection.disconnect(connection);
            }
        }
    }

    public Result deleteUser(UUID uuid) {
        Connection connection = null;
        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            Result result = userRepository.delete(uuid,connection);

            if (!result.isSuccess()) {
                DataBaseConnection.rollback(connection);
                return Result.FAIL;
            }

            return Result.SUCCESS;
        }  catch (SQLException e) {
            if (connection != null) {
                DataBaseConnection.rollback(connection);
            }
            return Result.Exception(e);
        }finally {
            if (connection != null) {
                DataBaseConnection.disconnect(connection);
            }
        }
    }


    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
}
