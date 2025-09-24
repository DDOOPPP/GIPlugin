package org.gi.gICore.manager;

import lombok.extern.flogger.Flogger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.dag.DAG;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.repository.GuildLogRepository;
import org.gi.gICore.data.repository.GuildMemberRepository;
import org.gi.gICore.data.repository.GuildRepository;
import org.gi.gICore.data.repository.UserRepository;
import org.gi.gICore.model.Enum;
import org.gi.gICore.model.guild.Guild;
import org.gi.gICore.model.guild.GuildLog;
import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.guild.GuildRole;
import org.gi.gICore.model.setting.GuildSetting;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.model.values.MessageName;
import org.gi.gICore.util.Result;

import javax.print.DocFlavor;
import javax.xml.validation.Validator;
import java.awt.desktop.OpenFilesEvent;
import java.beans.EventHandler;
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
    private EconomyManager economyManager;
    private UserRepository userRepository;
    private GILogger logger;

    public GuildService(){
        this.repository = new GuildRepository();
        this.memberRepository = new GuildMemberRepository();
        this.logRepository = new GuildLogRepository();
        this.userRepository = new UserRepository();
        this.economyManager = EconomyManager.getInstance();
        GIConfig config = ConfigManager.getConfig("setting.yml");
        this.setting = new GuildSetting(config);

        logger = new GILogger();
        instance = this;
    }

    public Result create(UUID playerId, String guildName){
        Connection connection = null;
        try{

            connection = DataBaseConnection.getDataSource().getConnection();
            UserData data = userRepository.find(playerId,connection);

            connection.setAutoCommit(false);

            if (data == null) {
                return Result.ERROR(MessageName.NOT_FOUND_DATA);
            }
            if (data.hasGuild()){
                return Result.ERROR(MessageName.ALREADY_JOINED_GUILD);
            }
            if (!economyManager.has(data.getPlayer(),setting.getCreateValue().doubleValue())){
                return Result.ERROR(MessageName.NOT_ENOUGH_BALANCE);
            }

            Result withDrawResult = userRepository.updateBalance(playerId,setting.getCreateValue(),connection, Enum.EconomyType.WITHDRAW);

            if (!withDrawResult.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.WITHDRAW_NG);
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
        Connection connection = null;
        try{
            connection =DataBaseConnection.getDataSource().getConnection();
            connection.setAutoCommit(false);
            UserData data = userRepository.find(playerId,connection);
            UserData target = userRepository.find(targetId,connection);
            if (data == null || target == null) {
                return Result.ERROR(MessageName.NOT_FOUND_DATA);
            }

            if (!data.hasGuild()){
                return Result.ERROR(MessageName.NOT_JOIN_GUILD);
            }

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
            Result update = userRepository.updateGuildName(targetId, guild.getGuildName(),connection);

            if (result.isSuccess() && update.isSuccess()){
                connection.commit();
                return Result.SUCCESS(MessageName.JOIN_GUILD_OK);
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
        Connection connection = null;

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            GuildMember member = memberRepository.getMember(playerId,connection);
            connection.setAutoCommit(false);
            if (member == null){
                return Result.ERROR(MessageName.MEMBER_NOT_FOUND);
            }

            if (member.isOwner()){
                return Result.ERROR(MessageName.OWNER_CANT_LEAVE);
            }

            Result result = memberRepository.deleteMember(playerId,connection);
            if (!result.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.LEAVE_GUILD_NG);
            }
            Result update = userRepository.updateGuildName(playerId, "NONE",connection);

            if (!update.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.KICK_GUILD_NG);
            }
            GuildLog log = new GuildLog(
                    member.getGuildId(),
                    playerId,
                    GuildRole.event.LEAVE,
                    BigDecimal.valueOf(0)
            );
            Result logResult = logRepository.insert(log,connection);

            if (!logResult.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.LEAVE_GUILD_NG);
            }

            connection.commit();
            return Result.SUCCESS(MessageName.LEAVE_GUILD_OK);
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

    public Result kick(UUID playerId,UUID targetId){
        Connection connection = null;

        if (playerId.equals(targetId)){
            return  Result.ERROR(MessageName.SELF_KICK_IS_NOT_AVAILABLE);
        }

        try{
            connection = DataBaseConnection.getDataSource().getConnection();
            connection.setAutoCommit(false);
            GuildMember member = memberRepository.getMember(playerId,connection);
            if (member == null){
                return Result.ERROR(MessageName.MEMBER_NOT_FOUND);
            }

            if (!member.isOwner()){
                return Result.ERROR(MessageName.NOT_HAVE_PERMISSION);
            }

            Result result = memberRepository.deleteMember(targetId,connection);
            Result update = userRepository.updateGuildName(targetId, "NONE",connection);

            if (!result.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.KICK_GUILD_NG);
            }

            if (!update.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.KICK_GUILD_NG);
            }
            UUID guildId = member.getGuildId();
            GuildLog actionLog = new GuildLog(
                    guildId,
                    playerId,
                    GuildRole.event.KICK,
                    BigDecimal.valueOf(0)
            );
            GuildLog targetLog = new GuildLog(
                    guildId,
                    targetId,
                    GuildRole.event.LEAVE,
                    BigDecimal.valueOf(0)
            );


            Result action = logRepository.insert(actionLog,connection);
            Result target = logRepository.insert(targetLog,connection);

            if (!action.isSuccess() || !target.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.KICK_GUILD_NG);
            }

            connection.commit();
            return Result.SUCCESS(MessageName.KICK_GUILD_OK);
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

    public Result changeRole(UUID playerId,UUID targetId,GuildRole.Role role){
        Connection connection = null;
        if (playerId.equals(targetId)){
            return Result.FAIL;
        }
        if (role == GuildRole.Role.OWNER){
            return Result.FAIL;
        }

        try{
            connection = DataBaseConnection.getDataSource().getConnection();

            GuildMember member = memberRepository.getMember(playerId,connection);
            if (member == null){
                return Result.ERROR(MessageName.MEMBER_NOT_FOUND);
            }
            if (!member.isOwner()){
                return Result.ERROR(MessageName.NOT_HAVE_PERMISSION);
            }

            GuildMember target = memberRepository.getMember(targetId,connection);
            if (target == null){
                return Result.ERROR(MessageName.MEMBER_NOT_FOUND);
            }
            if (target.isOwner()){
                return Result.ERROR(MessageName.ROLE_CHANGE_FAIL);
            }
            target.setRole(role);
            connection.setAutoCommit(false);
            Result update = memberRepository.updateRole(targetId,role,connection);

            if (!update.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.ROLE_CHANGE_FAIL);
            }

            GuildLog log = new GuildLog(
                    member.getGuildId(),
                    playerId,
                    GuildRole.event.ROLE_CHANGE,
                    BigDecimal.ZERO
            );
            GuildRole.event targetEvent = role.equals(GuildRole.Role.MEMBER) ?
                    GuildRole.event.ROLE_MEMBER : GuildRole.event.ROLE_SUB_OWNER;


            GuildLog targetLog = new GuildLog(
                    target.getGuildId(),
                    targetId,
                    targetEvent,
                    BigDecimal.ZERO
            );

            Result logResult = logRepository.insert(log,connection);
            Result targetResult = logRepository.insert(targetLog,connection);

            if (!logResult.isSuccess() || !targetResult.isSuccess()){
                DataBaseConnection.rollback(connection);
                return Result.ERROR(MessageName.ROLE_CHANGE_FAIL);
            }

            connection.commit();
            return Result.SUCCESS(MessageName.ROLE_CHANGE_OK);
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
    public static GuildService getInstance() {
        if (instance == null) {
            instance = new GuildService();
        }
        return instance;
    }
}
