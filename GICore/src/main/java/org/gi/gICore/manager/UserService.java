package org.gi.gICore.manager;

import jdk.jfr.consumer.RecordedStackTrace;
import org.gi.gICore.data.repository.EconLog;
import org.gi.gICore.data.repository.UserRepository;
import org.gi.gICore.model.log.EconomyLog;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.util.Result;

import java.math.BigDecimal;
import java.util.UUID;

public class UserService {
    private UserRepository userRepository;
    private static UserService instance;
    public UserService() {
        this.userRepository = new UserRepository();
        instance = this;
    }

    public Result create(UserData userData) {
        return userRepository.insert(userData);
    }

    public Result updateBalance(UUID userId, EconomyLog economyLog) {
        return userRepository.updateBalance(userId,economyLog);
    }

    public Result changeUsername(UUID userId, String newUsername) {
        return userRepository.updatePlayerName(userId,newUsername);
    }

    public Result changeGuild(UUID userId, String guildName) {
        return userRepository.updateGuild(userId,guildName);
    }

    public UserData getUserData(UUID userId) {
        return userRepository.find(userId);
    }

    public boolean isExistUser(UUID userId) {
        return getUserData(userId) != null;
    }

    public boolean hasGuild(UUID userId) {
        UserData userData = getUserData(userId);
        if (userData == null) return false;

        return userData.hasGuild();
    }

    public Result delete (UUID id) {
        return userRepository.delete(id);
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
}
