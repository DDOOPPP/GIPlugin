package org.gi.gICore.data.repository;

import org.gi.gICore.util.Result;

import java.util.UUID;

public interface LogRepository <K,V>{
    public Result insert(K k,V v);
    public Result deleteAll(UUID key, V v);
    public Result delete(UUID key, V v);
    public int serachCount(UUID key, V v);
    public Result deleteOverCount(UUID key, V v);

}
