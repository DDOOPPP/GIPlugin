package org.gi.gICore.component;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gIAPI.util.FileUtil;
import org.gi.gICore.GICore;
import org.gi.gICore.GICoreLoader;
import org.gi.gICore.GILogger;
import org.gi.gICore.manager.EconomyManager;
import org.gi.gICore.model.values.MessageName;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
public class MessageLoader {
    private static File MSG_FILE;
    public static Map<String,String> kr_message = new HashMap<String,String>();
    public static Map<String,String> en_message = new HashMap<String,String>();
    public static Map<String,String> ja_message = new HashMap<String,String>();
    private static GILogger logger;
    private static JavaPlugin plugin;
    private static List<String> languages = List.of("ko_kr","en_us","ja_jp");
    private static List<String> messageFiles = List.of("error","economy");
    public static void Initialize(){
        logger = new GILogger();

        plugin = GICore.getInstance();
        File file = new  File(plugin.getDataFolder(), "messages");
        if (!file.exists()) {
            file.mkdir();
        }
        loadResource();
        loadMessageFile();
    }

    private static void loadResource(){
        for (String language : languages) {
            String country = language.toLowerCase();
            for (String messageFile : messageFiles) {
                String path = "messages"+ "/"  + country.toLowerCase() + "/" + messageFile+".yml";
                File file = new File(plugin.getDataFolder(), path);
                if (file.exists()) {
                    continue;
                }
                logger.warn(path);
                FileUtil.getResource(GICore.getInstance(),path);
            }
        }
    }
    private static void loadMessageFile(){
        for (String language : languages) {
            String country = language.toLowerCase();
            for (String messageFile : messageFiles) {
                Map<String ,String> messagePack = new HashMap<>();
                String path = "messages"+ "/" + country.toLowerCase() + "/" + messageFile+".yml";
                File file = new File(plugin.getDataFolder(), path);
                switch (language) {
                    case "ko_kr":
                        messagePack = loadMessage(file);
                        if (messagePack == null) {
                            continue;
                        }
                        kr_message.putAll(messagePack);

                        break;
                    case "en_us":
                        messagePack = loadMessage(file);
                        if (messagePack == null) {
                            continue;
                        }
                        en_message.putAll(messagePack);
                        break;
                    case "ja_jp":
                        messagePack = loadMessage(file);
                        if (messagePack == null) {
                            continue;
                        }
                        ja_message.putAll(messagePack);
                        break;
                }
            }
        }
    }

    private static Map<String,String> loadMessage(File file){
        GIConfig config = new GIConfig(file);
        if (config == null){
            logger.error("Config is Null");
            return null;
        }
        Map<String,String> map = new HashMap<>();

        ConfigurationSection section = config.getConfigurationSection("");
        if (section == null){
            logger.error("Config Section is Null");
            return null;
        }

        for (String key : section.getKeys(false)) {
            map.put(key,section.getString(key));
        }
        return map;
    }
}
