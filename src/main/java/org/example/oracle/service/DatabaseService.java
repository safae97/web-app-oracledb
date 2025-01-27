package org.example.oracle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy");

    public void gatherIndexStats(String owner, String indexName) {
        String plsql = "BEGIN DBMS_STATS.GATHER_INDEX_STATS(OWNNAME => ?, INDNAME => ?); END;";
        jdbcTemplate.update(plsql, owner, indexName);
    }

    public Map<String, Object> getIndexStats(String owner, String indexName) {
        String sql = "SELECT INDEX_NAME, LAST_ANALYZED " +
                "FROM DBA_INDEXES WHERE OWNER = ? AND INDEX_NAME = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, owner, indexName);

        if (result.containsKey("LAST_ANALYZED") && result.get("LAST_ANALYZED") != null) {
            LocalDateTime lastAnalyzed = ((java.sql.Timestamp) result.get("LAST_ANALYZED")).toLocalDateTime();
            result.put("LAST_ANALYZED", lastAnalyzed.format(dateFormatter));
        }
        return result;
    }

    public void gatherTableStats(String owner, String tableName) {
        String plsql = "BEGIN DBMS_STATS.GATHER_TABLE_STATS(OWNNAME => ?, TABNAME => ?, CASCADE => TRUE); END;";
        jdbcTemplate.update(plsql, owner, tableName);
    }

    public Map<String, Object> getTableStats(String owner, String tableName) {
        String sql = "SELECT TABLE_NAME, LAST_ANALYZED " +
                "FROM DBA_TAB_STATISTICS WHERE TABLE_NAME = ? AND OWNER = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, tableName, owner);

        if (result.containsKey("LAST_ANALYZED") && result.get("LAST_ANALYZED") != null) {
            LocalDateTime lastAnalyzed = ((java.sql.Timestamp) result.get("LAST_ANALYZED")).toLocalDateTime();
            result.put("LAST_ANALYZED", lastAnalyzed.format(dateFormatter));
        }
        return result;
    }



}
//package org.example.oracle.service;

//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Map;
//
//@Service
//public class DatabaseService {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
//
//    public void gatherIndexStats(String owner, String indexName) {
//        String plsql = "BEGIN DBMS_STATS.GATHER_INDEX_STATS(OWNNAME => ?, INDNAME => ?); END;";
//        jdbcTemplate.update(plsql, owner, indexName);
//    }
//
//    public Map<String, Object> getIndexStats(String owner, String indexName) {
//        String sql = "SELECT INDEX_NAME, LAST_ANALYZED " +
//                "FROM DBA_INDEXES WHERE OWNER = ? AND INDEX_NAME = ?";
//        Map<String, Object> result = jdbcTemplate.queryForMap(sql, owner, indexName);
//
//        // Format LAST_ANALYZED if present
//        if (result.containsKey("LAST_ANALYZED") && result.get("LAST_ANALYZED") != null) {
//            LocalDateTime lastAnalyzed = ((java.sql.Timestamp) result.get("LAST_ANALYZED")).toLocalDateTime();
//            result.put("LAST_ANALYZED", lastAnalyzed.format(dateFormatter));
//        }
//        return result;
//    }
//
//    public void gatherTableStats(String owner, String tableName) {
//        String plsql = "BEGIN DBMS_STATS.GATHER_TABLE_STATS(OWNNAME => ?, TABNAME => ?, CASCADE => TRUE); END;";
//        jdbcTemplate.update(plsql, owner, tableName);
//    }
//
//    public Map<String, Object> getTableStats(String owner, String tableName) {
//        String sql = "SELECT TABLE_NAME, LAST_ANALYZED " +
//                "FROM DBA_TAB_STATISTICS WHERE TABLE_NAME = ? AND OWNER = ?";
//        Map<String, Object> result = jdbcTemplate.queryForMap(sql, tableName, owner);
//
//        // Format LAST_ANALYZED if present
//        if (result.containsKey("LAST_ANALYZED") && result.get("LAST_ANALYZED") != null) {
//            LocalDateTime lastAnalyzed = ((java.sql.Timestamp) result.get("LAST_ANALYZED")).toLocalDateTime();
//            result.put("LAST_ANALYZED", lastAnalyzed.format(dateFormatter));
//        }
//        return result;
//    }
//}
