package org.gi.gICore.manager;

import org.gi.gIAPI.component.adapter.GIConfig;
import org.gi.gICore.GILogger;
import org.gi.gICore.model.setting.GuildSetting;

import java.util.logging.Logger;

public class GuildManager {
    private GuildSetting guildSetting;
    private static GuildManager instance;
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
        instance = this;
    }

    public static GuildManager getInstance(){
        if(instance == null){
            instance = new GuildManager();
        }
        return instance;
    }
}
