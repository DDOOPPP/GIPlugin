package org.gi.gICore;

import java.util.logging.Logger;

public class GILogger {
    private Logger logger;

    public GILogger(){
        this.logger = GICore.getInstance().getLogger();
    }

    public void info(String msg){
        logger.info(msg);
    }

    public void info(String msg,String message){
        String transfer = msg.formatted(message);
        info(transfer);
    }

    public void warn(String msg){
        logger.warning(msg);
    }

    public void warn(String msg,Object message){
        String transfer = msg.formatted(message);
        warn(transfer);
    }

    public void error(String msg){
        logger.severe(msg);
    }

    public void error(String msg,Object message){
        String transfer = msg.formatted(message);
        error(transfer);
    }
}