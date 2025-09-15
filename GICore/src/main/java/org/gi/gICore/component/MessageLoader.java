package org.gi.gICore.component;

import org.bukkit.plugin.java.JavaPlugin;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
    private static JavaPlugin plugin;
    public static void Initialize(){
        logger = new GILogger();

        plugin = GICore.getInstance();
    }

}
