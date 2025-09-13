package org.gi.gICore.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Result {
    private int code;
    private String msg;

    public static Result SUCCESS = new Result(0,"success");
    public static Result FAIL = new Result(-1,"fail");
    public static Result ERROR = new Result(-1,"error");

    public static Result Exception(Exception e) {
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
