package org.gi.gICore.model.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class Log {
    private UUID uuid;
    private String userName;
}
