package org.gi.gICore.data.repository;

import org.gi.gICore.util.Result;

import java.util.List;

public interface Repository <K,V>{
    public Result insert(K data);

    public Result update(K data);

    public Result delete(V key);

    public K find(V key);

    public List<K> findAll();
}
