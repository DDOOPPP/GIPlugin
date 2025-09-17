package org.gi.gICore.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {
    public static String replace(String message, Map<String,Object> values, OfflinePlayer player) {
        if(values == null && player == null) {
            return message;
        }
        Pattern pattern = Pattern.compile("\\{(.*?)}");
        Matcher matcher = pattern.matcher(message);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object val = values.getOrDefault(key, "");

            matcher.appendReplacement(sb, Matcher.quoteReplacement(val.toString()));
        }


        matcher.appendTail(sb);
        String replacedMessage = sb.toString();
        if (player != null) {
            PlaceholderAPI.setPlaceholders(player, sb.toString());
        }
        return replacedMessage;
    }

    public static List<String> replaceLore(List<String> messages, Map<String,Object> values, OfflinePlayer player) {
        List<String> ret = new ArrayList<String>();

        for (String message : messages) {
            ret.add(replace(message, values,player));
        }
        return ret;
    }
}
