package org.gi.gICore.manager;

import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.repository.GuildLogRepository;
import org.gi.gICore.data.repository.GuildMemberRepository;
import org.gi.gICore.data.repository.GuildRepository;
import org.gi.gICore.model.guild.Guild;
import org.gi.gICore.model.guild.GuildLog;
import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.guild.GuildRole;
import org.gi.gICore.model.setting.GuildSetting;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.model.values.MessageName;
import org.gi.gICore.util.Result;

import javax.xml.validation.Validator;
import java.io.File;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class GuildService {
    private static GuildService instance;
    private GuildRepository repository;
    private GuildMemberRepository memberRepository;
    private GuildLogRepository logRepository;
    private GuildSetting setting;
    private UserService userService;
    private EconomyManager economyManager;

    public GuildService(){
        this.repository = new GuildRepository();
        this.memberRepository = new GuildMemberRepository();
        this.logRepository = new GuildLogRepository();
        this.userService = UserService.getInstance();
        this.economyManager = EconomyManager.getInstance();
        GIConfig config = ConfigManager.getConfig("setting.yml");
        this.setting = new GuildSetting(config);
        instance = this;
    }

    public Result create(UUID playerId, String guildName){
        UserData data = userService.getUserData(playerId);
        if (data == null) {
            return Result.ERROR(MessageName.NOT_FOUND_DATA);
        }
        if (data.hasGuild()){
            return Result.ERROR(MessageName.ALREADY_JOINED_GUILD);
        }
        if (!economyManager.has(data.getPlayer(),setting.getCreateValue().doubleValue())){
            return Result.ERROR(MessageName.NOT_ENOUGH_BALANCE);
        }
        UUID guildId =  UUID.randomUUID();
        Guild guild = new Guild(
                guildId,
                guildName,
                playerId,
                setting.getCreateValue(),
                1,
                BigDecimal.valueOf(0),
                "공지사항"
        );

        GuildMember member = new GuildMember(
                playerId,
                guildId,
                GuildRole.Role.OWNER
        );

        GuildLog log = new GuildLog(
                guildId,
                playerId,
                GuildRole.event.NEW,
                setting.getCreateValue()
        );
        Connection connection = null;

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            connection.setAutoCommit(false);

            Result create = repository.insert(guild,connection);
            Result memberResult = memberRepository.insert(member,connection);
            Result logResult = logRepository.insert(log,connection);

            if (create.isSuccess() && memberResult.isSuccess() && logResult.isSuccess()){
                connection.commit();
                return Result.SUCCESS(MessageName.GUILD_CREATE_OK);
            }

            DataBaseConnection.rollback(connection);
            return Result.ERROR(MessageName.GUILD_CREATE_NG);
        } catch (SQLException e) {
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

    public Result join(UUID playerId, UUID targetId){
        UserData data = userService.getUserData(playerId);
        UserData target = userService.getUserData(targetId);
        if (data == null || target == null) {
            return Result.ERROR(MessageName.NOT_FOUND_DATA);
        }

        if (!data.hasGuild()){
            return Result.ERROR(MessageName.NOT_JOIN_GUILD);
        }
        Connection connection = null;
        try{
            connection =DataBaseConnection.getDataSource().getConnection();

            Guild guild= repository.find(data.getGuildName(),connection);

            int memberCount = memberRepository.getMembers(guild.getGuildId(),connection).size();
            GuildMember member = memberRepository.getMember(guild.getGuildId(),playerId,connection);

            if (!member.isManager()){
                return Result.ERROR(MessageName.NOT_HAVE_PERMISSION);
            }

            if (memberCount >= setting.getMaxMemberCount()){
                return Result.ERROR(MessageName.GUILD_FULL);
            }

            if (target.hasGuild()){
                return Result.ERROR(MessageName.ALREADY_JOINED_GUILD);
            }

            GuildMember newMember = new GuildMember(
                    targetId,
                    guild.getGuildId(),
                    GuildRole.Role.MEMBER
            );

            Result result = memberRepository.insert(newMember,connection);

            if (result.isSuccess()){
                Result update = userService.updateGuildName(targetId, guild.getGuildName());

                if (update.isSuccess()){
                    return Result.SUCCESS(MessageName.JOIN_GUILD_OK);
                }
            }

            if (connection != null) {
                DataBaseConnection.rollback(connection);
            }
            return Result.ERROR(MessageName.JOIN_GUILD_NG);
        } catch (SQLException e) {
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

    public Result leave(UUID playerId){
        UserData data = userService.getUserData(playerId);
        if (data == null) {
            return Result.ERROR(MessageName.NOT_FOUND_DATA);
        }

        if (!data.hasGuild()){
            return Result.ERROR(MessageName.NOT_JOIN_GUILD);
        }
        Connection connection = null;

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            GuildMember member = memberRepository.getMember(playerId,connection);

            if (member.isOwner()){
                return Result.ERROR(MessageName.CANT_BAN_OWNER);
            }

            Result result = memberRepository.deleteMember(playerId,connection);

            if (result.isSuccess()){
                return Result.SUCCESS(MessageName.GUILD_LEAVE_OK);
            }

            DataBaseConnection.rollback(connection);
            return Result.ERROR(MessageName.GUILD_LEAVE_NG);
        } catch (SQLException e) {
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

    public Result kick(UUID playerId, UUID targetId){
        UserData data = userService.getUserData(targetId);

        return Result.ERROR(MessageName.NOT_FOUND_DATA);
    }
    public static GuildService getInstance() {
        if (instance == null) {
            instance = new GuildService();
        }
        return instance;
    }
}
