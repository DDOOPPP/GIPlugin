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
import java.io.ObjectInputFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MessageLoader {
    private static File MSG_FILE;
    public static Map<String,String> kr_message = new HashMap<String,String>();
    public static Map<String,String> en_message = new HashMap<String,String>();
    public static Map<String,String> ja_message = new HashMap<String,String>();
    private static GILogger logger;
    private static String path = "messages/%s/%s.yml";
    private static GICore core;
    private static List<String> lang = List.of("en_us","ja_jp","ko_kr");
    private static List<String> baseMessageFile = List.of("error","economy");

    public static void Initialize(){
        logger = new GILogger();
        core = GICore.getInstance();

        File file = new File(core.getDataFolder(),"messages");
        if (!file.exists()){
            file.mkdirs();
        }
        loadResource();

        loadData();
    }

    private static void loadResource(){
        for (String lang : lang){
            for (String file : baseMessageFile){
                String total = path;
                total = String.format(lang,file);
                logger.info(total);
                File f = new File(core.getDataFolder(),total);
                if (f.exists()){
                    continue;
                }

                FileUtil.getResource(core,total);
            }
        }
    }

    private static void loadData(){
        for (String lang : lang){
            String path = "messages/%s".formatted(lang);
            File dir = new File(core.getDataFolder(),path);

            if (!dir.exists()){
                loadResource();
            }

            var filelist = dir.listFiles((dir1, name) -> name.endsWith(".yml"));
            if (filelist == null){
                continue;
            }

            for (File file : filelist){
                logger.info(file.getName());
                Map<String,String> map = new HashMap<>();

                map = loadMessage(file);
                if (map == null || map.isEmpty()){
                    continue;
                }

                switch (lang){
                    case "en_us":
                        en_message.putAll(map);
                        break;
                    case "ja_jp":
                        ja_message.putAll(map);
                        break;
                    case "ko_kr":
                        kr_message.putAll(map);
                        break;
                }
            }
        }
    }

    private static Map<String ,String> loadMessage(File file){
        GIConfig config = new GIConfig(file);
        Map<String ,String> map = new HashMap<>();
        if (config == null){
            logger.error("Config is Null");
            return null;
        }
        ConfigurationSection section = config.getConfigurationSection("");
        if (section == null){
            logger.error("Section is Null");
            return null;
        }

        for (String key : section.getKeys(false)){
            map.put(key,section.getString(key));
        }
        return map;
    }

    public static String getMessage(String key, String local){
        switch (local){
            case "en_us":
                return en_message.get(key);
            case "ja_jp":
                return ja_message.get(key);
            case "ko_kr":
                return kr_message.get(key);
            default:
                return "Unknown Local We Support korean , english, japan";
        }
    }
}
