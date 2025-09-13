package org.gi.gICore.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {
    private final String tableName;

    public QueryBuilder(String tableName) {
        this.tableName = tableName;
    }

    public String buildInsert(List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            return "No Query";
        }
        String cols = String.join(",", columns);
        String placeHolder = String.join(",", Collections.nCopies(columns.size(), "?"));

        return "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + placeHolder + ")";
    }

    public String buildUpsert(List<String> columns,List<String> updateColumns) {
        if (columns == null || columns.isEmpty() || updateColumns == null || updateColumns.isEmpty()) {
            return "No Query";
        }
        String cols = String.join(",", columns);
        String placeHolder = String.join(",", Collections.nCopies(columns.size(), "?"));

        String updates = updateColumns.stream()
                .map(col -> col + " = ?")
                .collect(Collectors.joining(", "));
        return "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + placeHolder + ") "
                + "ON DUPLICATE KEY UPDATE " + updates;
    }

    public String buildSelect(List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            return "No Query";
        }
        String conditions  = columns.stream()
                .map(col -> col + " = ?")
                .collect(Collectors.joining(" AND "));
        return "SELECT * FROM " + tableName + " WHERE " + conditions;
    }

    public String buildSelectSingle(String key) {
        return "SELECT * FROM " + tableName + " WHERE " + key + " = ?";
    }

    public String buildSelectAll() {
        return "SELECT * FROM " + tableName;
    }

    public String buildSelectCount(List<String> columns){
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("columns must not be empty");
        }
        String conditions  = columns.stream()
                .map(col -> col + " = ?")
                .collect(Collectors.joining(" AND "));
        return "SELECT COUNT(*) FROM " + tableName + " WHERE " + conditions;
    }

    public String buildUpdate(String key, List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("columns must not be empty");
        }

        String updates = columns.stream()
                .map(col -> col + " = ?")
                .collect(Collectors.joining(", "));

        return "UPDATE " + tableName + " SET " + updates + " WHERE " + key + " = ?";
    }

    public String buildUpdate(String key, String update) {
        return "UPDATE " + tableName + " SET " + update + " = ? WHERE " + key + " = ?";
    }

    public String buildDelete(String key) {
        return "DELETE FROM " + tableName + " WHERE " + key + " = ?";
    }

    public String buildSelect(List<String> whereColumns, String orderBy, Integer limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);

        if (whereColumns != null && !whereColumns.isEmpty()) {
            String conditions = whereColumns.stream()
                    .map(col -> col + " = ?")
                    .collect(Collectors.joining(" AND "));
            sql.append(" WHERE ").append(conditions);
        }

        if (orderBy != null && !orderBy.isBlank()) {
            sql.append(" ORDER BY ").append(orderBy);
        }

        if (limit != null && limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }

        return sql.toString();
    }

    public String DeleteOptionQuery(String key, int limit) {
        return "DELETE FROM " + tableName +
                " WHERE " + key + " = ? " +
                "AND (SELECT COUNT(*) FROM " + tableName + " WHERE " + key + " = ?) > " + limit;
    }
}
