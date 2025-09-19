package org.gi.gICore.data.repository;

import org.gi.gICore.GILogger;
import org.gi.gICore.data.database.DataBaseConnection;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.guild.Guild;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class GuildRepository implements Repository<Guild, UUID> {
    private GILogger logger;
    private QueryBuilder builder;;
    public GuildRepository() {
        logger = new GILogger();
        builder = new QueryBuilder(TableQuery.GUILD);
    }
    @Override
    public Result insert(Guild data) {
        Connection connection = null;
        String query = builder.buildInsert(
                List.of("guild_id","guild_name","guild_fund","member_count")
        );

        try{
            connection = DataBaseConnection.getDataSource().getConnection();


        } catch (SQLException e) {
            try {
                connection.rollback();

            } catch (SQLException ex) {

            }

        }finally {
            try{
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    @Override
    public Result update(Guild data) {
        return null;
    }

    @Override
    public Result delete(UUID key) {
        return null;
    }

    @Override
    public Guild find(UUID key) {
        return null;
    }

    @Override
    public List<Guild> findAll() {
        return List.of();
    }
}
