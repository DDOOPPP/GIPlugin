package org.gi.gICore.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gi.gICore.GILogger;

@Getter
@AllArgsConstructor
public class Result {
    private int code;
    private String msg;
    private static GILogger logger = new GILogger();
    public static Result SUCCESS = new Result(0,"success");
    public static Result FAIL = new Result(-1,"fail");
    public static Result ERROR = new Result(-1,"error");

    public static Result Exception(Exception e) {
        logger.error(e.getMessage());
        return new Result(-1,e.getMessage());
    }

    public static Result SUCCESS(String message) {
        return new Result(0,message);
    }

    public static Result ERROR(String message) {
        return new Result(-1,message);
    }

    public boolean isSuccess() {
        return this.equals(SUCCESS) || this.code == 0;
    }
}
