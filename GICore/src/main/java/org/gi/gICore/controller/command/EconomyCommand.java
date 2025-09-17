package org.gi.gICore.controller.command;

import io.lumine.mythic.lib.math3.random.StableRandomGenerator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gi.gICore.GILogger;
import org.gi.gICore.component.MessageLoader;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.model.values.MessageName;
import org.gi.gICore.model.values.ValueName;
import org.gi.gICore.util.MessageUtil;

import java.util.HashMap;
import java.util.Map;

public class EconomyCommand {
    private static GILogger logger = new GILogger();
    private static EconomyManager economyManager = EconomyManager.getInstance();
    public static boolean executeInfo(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player) sender;
        String local = player.getLocale();
        String message = MessageLoader.getMessage(MessageName.USER_INFORMATION, local);

        if (message == null || message.isEmpty()){
            logger.info("Message is null or empty: %s",MessageName.USER_INFORMATION);
            return false;
        }

        if (args.length < 1){
                message = MessageUtil.replace(message,null,player);

                player.sendMessage(message);
            return true;
        }
        if (args.length == 2 && player.isOp()){
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            message = MessageUtil.replace(message,null,target);

            player.sendMessage(message);
            return true;
        }
        return false;
    }

    public static boolean executeDeposit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)){
            return false;
        }

        Player player = (Player) sender;
        String local = player.getLocale();
        if (!player.isOp()){
            return false;
        }

        if (args.length == 2){
            try{
                Map<String ,Object> values = new HashMap<>();
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                double amount = Double.parseDouble(args[1]);

                values.put(ValueName.AMOUNT, amount);

                String result_message = economyManager.deposit(target,amount);
                String admin_message = MessageName.DEPOSIT_ADMIN_NG;
                if (result_message.equals(MessageName.DEPOSIT_OK)){
                    admin_message = MessageName.DEPOSIT_ADMIN_OK;
                }

                if (target.isOnline()){
                    Player target_player = Bukkit.getPlayer(target.getUniqueId());

                    sendUser(target_player,result_message,values);
                }

                sendAdmin(player,target,admin_message,values);

            }catch (Exception e){
                if (e instanceof NumberFormatException){
                    player.sendMessage(MessageLoader.getMessage(MessageName.NUMBER_ERROR,local));
                    return false;
                }
                logger.error(e.getMessage());
                return false;
            }

        }
        return true;
    }

    private static void sendUser(Player player, String key,Map<String ,Object> values){
        String local = player.getLocale();
        String message = MessageLoader.getMessage(key, local);
        if (message == null || message.isEmpty()){
            logger.info("Message is null or empty: %s",key);
            return;
        }
        message = MessageUtil.replace(message,values,player);

        player.sendMessage(message);
    }

    private static void sendAdmin(Player player,OfflinePlayer target,String key,Map<String ,Object> values){
        String local = player.getLocale();
        String message = MessageLoader.getMessage(key, local);
        if (message == null || message.isEmpty()){
            logger.info("Message is null or empty: %s",key);
            return;
        }
        message = MessageUtil.replace(message,values,target);

        player.sendMessage(message);
    }
}
