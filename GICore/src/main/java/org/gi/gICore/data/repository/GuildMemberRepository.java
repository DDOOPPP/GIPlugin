package org.gi.gICore.data.repository;

import org.gi.gICore.GILogger;
import org.gi.gICore.data.table.TableQuery;
import org.gi.gICore.model.guild.GuildMember;
import org.gi.gICore.model.guild.GuildRole;
import org.gi.gICore.util.QueryBuilder;
import org.gi.gICore.util.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildMemberRepository {
    private QueryBuilder builder;
    private GILogger logger;

    public GuildMemberRepository() {
        this.builder = new QueryBuilder(TableQuery.GUILD_MEMBER);
        this.logger = new GILogger();
    }

    public Result insert(GuildMember member, Connection connection) {
        String query = builder.buildInsert(
                List.of("member_id", "guild_id", "role")
        );

        Result result = Result.FAIL;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, member.getUserId().toString());
            statement.setString(2, member.getGuildId().toString());
            statement.setString(3, member.getRole().name());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result updateRole(UUID playerId, GuildRole.Role role, Connection connection) {
        String query = builder.buildUpdate("member_id", "role");

        Result result = Result.FAIL;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, role.name());
            statement.setString(2, playerId.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public Result deleteMember(UUID playerId, Connection connection) {
        String query = builder.buildDelete("member_id");
        Result result = Result.FAIL;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerId.toString());

            result = statement.executeUpdate() > 0 ? Result.SUCCESS : Result.FAIL;
        } catch (SQLException e) {
            return Result.Exception(e);
        }
        return result;
    }

    public GuildMember getMember(UUID guildId,UUID playerId, Connection connection) {
        String query = builder.buildSelect(
                List.of("guild_id","member_id")
        );
        GuildMember member = null;

        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, guildId.toString());
            statement.setString(2, playerId.toString());

            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    member = new GuildMember(
                            playerId,
                            guildId,
                            GuildRole.Role.valueOf(resultSet.getString("role"))
                    );
                }
            }

        } catch (SQLException e) {
            Result.Exception(e);
        }
        return member;
    }

    public List<GuildMember> getMembers(UUID guildId, Connection connection) {
        String query = builder.buildSelect(
                List.of("guild_id")
        );
        logger.warn(query);
        List<GuildMember> members = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, guildId.toString());

            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    members.add(new GuildMember(
                            UUID.fromString("member_id"),
                            guildId,
                            GuildRole.Role.valueOf(resultSet.getString("role"))
                    ));
                }
            }

        } catch (SQLException e) {
            Result.Exception(e);
            return List.of();
        }
        return members;
    }
}
