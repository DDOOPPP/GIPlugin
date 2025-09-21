package org.gi.gICore.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gICore.GILogger;
import org.gi.gICore.component.MessageLoader;
import org.gi.gICore.data.repository.GuildMemberRepository;
import org.gi.gICore.data.repository.GuildRepository;
import org.gi.gICore.model.guild.Guild;
import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.guild.GuildRole;
import org.gi.gICore.model.setting.GuildSetting;
import org.gi.gICore.model.user.UserData;
import org.gi.gICore.model.values.MessageName;
import org.gi.gICore.util.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GuildManager {
    private GuildSetting guildSetting;
    private static GuildManager instance;
    private GuildRepository guildRepository;
    private GuildMemberRepository memberRepository;
    private UserService userService;
    private EconomyManager economyManager;
    private GIConfig config;
    private GILogger logger;

    public GuildManager(){
        logger = new GILogger();
        config = ConfigManager.getConfig("setting.yml");
        if (config == null){
            logger.info("[Guild] setting.yml not found");
            return;
        }

        guildSetting = new GuildSetting(config);
        userService = UserService.getInstance();
        economyManager = EconomyManager.getInstance();
        guildRepository = new GuildRepository();
        memberRepository = new GuildMemberRepository();
        instance = this;
    }

    public Result createGuild(Player player,String guildName){
        UserData userData = userService.getUserData(player.getUniqueId());
        if (userData == null){
            String message = MessageLoader.getMessage(MessageName.CALL_ADMIN,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("UserData not found");
        }

        List<Guild> guildList = guildRepository.findAll();
        if (!guildList.isEmpty()){
            boolean isExist = guildList.stream().anyMatch(guild -> guild.getGuildName().equals(guildName));

            if (!isExist){
                String message = MessageLoader.getMessage(MessageName.SAME_ID_EXISTS,player.getLocale());
                player.sendMessage(message);
                return Result.ERROR("Guild Name is Exists");
            }
        }

        if (userData.hasGuild()){
            String message = MessageLoader.getMessage(MessageName.ALREADY_JOINED_GUILD,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("ALREADY_JOINED_GUILD");
        }

        boolean has = economyManager.has(player, guildSetting.getCreateValue().doubleValue());
        if (!has){
            String message = MessageLoader.getMessage(MessageName.NOT_ENOUGH_BALANCE,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("Not Enough Money");
        }

        Guild guild = new Guild(
                UUID.randomUUID(),
                guildName,
                player.getUniqueId(),
                guildSetting.getCreateValue(),
                1,
                BigDecimal.valueOf(0),
                "공지사항"
        );

        return guildRepository.createGuild(guild);
    }

    public Result joinGuild(Player player,Player targetPlayer){
        UserData userData = userService.getUserData(player.getUniqueId());
        UserData targetUserData = userService.getUserData(targetPlayer.getUniqueId());
        Guild guild = guildRepository.getGuild(userData.getGuildName());
        if (guild == null){
            String message = MessageLoader.getMessage(MessageName.CALL_ADMIN,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("Guild Not Found");
        }

        if (!isManager(player.getUniqueId())){
            String message = MessageLoader.getMessage(MessageName.NOT_HAVE_PERMISSION,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("Not Have Permission");
        }

        if (targetUserData.hasGuild()){
            String message = MessageLoader.getMessage(MessageName.ALREADY_JOINED_GUILD,player.getLocale());
            player.sendMessage(message);

            return Result.ERROR("Already Joined");
        }

        List<GuildMember> members = memberRepository.getMembers(guild.getGuildId());
        if (members.size() >= guildSetting.getMaxMemberCount()){
            String message = MessageLoader.getMessage(MessageName.GUILD_FULL,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("Guild Full");
        }

        GuildMember target = new GuildMember(
                targetPlayer.getUniqueId(),
                guild.getGuildId(),
                GuildRole.Role.MEMBER
        );

        return memberRepository.insertMember(target);
    }

    public Result leaveGuild(Player player,Player target){
        UserData userData = null;
        GuildMember member = memberRepository.getMember(target.getUniqueId());
        if (member == null){
            String message = MessageLoader.getMessage(MessageName.MEMBER_NOT_FOUND,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("Member Not Found");
        }

        if (member.isOwner()){
            String message = MessageLoader.getMessage(MessageName.CANT_BAN_OWNER,target.getLocale());
            target.sendMessage(message);
            return Result.ERROR("Owner Can't Leave");
        }

        if (player != null){
            userData = userService.getUserData(player.getUniqueId());
            if (member.isOwner()){
                String message = MessageLoader.getMessage(MessageName.CANT_BAN_OWNER,player.getLocale());
                player.sendMessage(message);
                return Result.ERROR("Owner Can't Leave");
            }

            if (!member.isManager()){
                String message = MessageLoader.getMessage(MessageName.NOT_HAVE_PERMISSION,player.getLocale());
                player.sendMessage(message);

                return Result.ERROR("Not Manager");
            }

            return memberRepository.deleteMember(target.getUniqueId());
        }

        return memberRepository.deleteMember(target.getUniqueId());
    }

    public GuildMember getGuildMember(UUID uuid){
        return memberRepository.getMember(uuid);
    }

    public Result destroyGuild(Player player){
        GuildMember member = memberRepository.getMember(player.getUniqueId());
        if (member == null){
            String message = MessageLoader.getMessage(MessageName.MEMBER_NOT_FOUND,player.getLocale());
            player.sendMessage(message);
            return Result.ERROR("Member Not Found");
        }

        if (!member.isOwner()){
            String message = MessageLoader.getMessage(MessageName.NOT_HAVE_PERMISSION,player.getLocale());
            player.sendMessage(message);
            return  Result.ERROR("Not Have Permission");
        }

        List<GuildMember> members = memberRepository.getMembers(member.getGuildId());

        Result result = guildRepository.deleteGuild(member.getGuildId());
        if (!result.isSuccess()){
            String message = MessageLoader.getMessage(MessageName.GUILD_DESTROY_NG,player.getLocale());
            player.sendMessage(message);
            return result;
        }

        for (GuildMember guildMember : members){
            result = userService.changeGuild(guildMember.getUserId(),"NONE");
            if (!result.isSuccess()){
                logger.error("Guild Change Failed: %s",guildMember.getUserId());
                continue;
            }
        }
        if (!result.isSuccess()){
            String message = MessageLoader.getMessage(MessageName.CALL_ADMIN,player.getLocale());
            player.sendMessage(message);
            return result;
        }
        for (GuildMember guildMember : members) {
            OfflinePlayer memberPlayer = Bukkit.getOfflinePlayer(guildMember.getUserId());
            if (!memberPlayer.isOnline()) {
                continue;
            }
            Player onlinePlayer = memberPlayer.getPlayer();
            String message = MessageLoader.getMessage(MessageName.GUILD_DESTROY_OK, onlinePlayer.getLocale());
            onlinePlayer.sendMessage(message);
        }
        return result;
    }

    public Result updateGuildFund(){
        return Result.FAIL;
    }

    private boolean isManager(UUID uuid){
        GuildMember member = memberRepository.getMember(uuid);
        if (member == null){
            return false;
        }
        return member.isManager();
    }

    public static GuildManager getInstance(){
        if(instance == null){
            instance = new GuildManager();
        }
        return instance;
    }
}
