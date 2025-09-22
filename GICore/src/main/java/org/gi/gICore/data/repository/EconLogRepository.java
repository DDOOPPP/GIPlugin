package org.gi.gICore.data.repository;

import org.gi.gICore.GILogger;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.log.EconomyLog;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EconLogRepository {
    private QueryBuilder builder;
    private GILogger logger;

    public EconLogRepository() {
        this.builder = new QueryBuilder(TableQuery.ECONOMY_LOG);
        this.logger = new GILogger();
    }

    public Result insert(EconomyLog log, Connection connnection) {
        Result result = Result.FAIL;
        String query = builder.buildInsert(
                List.of("player_id","player_name","type","amount","balance")
        );

        try(PreparedStatement statement = connnection.prepareStatement(query)){
            statement.setString(1,log.getUuid().toString());
            statement.setString(2,log.getUserName());
            statement.setString(3,log.getEconomyType().name());
            statement.setBigDecimal(4,log.getAmount());
            statement.setBigDecimal(5,log.getBalance());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;

        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }
}
