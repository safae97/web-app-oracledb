package org.example.oracle.service;
import org.springframework.transaction.annotation.Transactional;

import org.example.oracle.classes.dao.TableInfoRepository;
import org.example.oracle.classes.model.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TableInfoRepository tableInfoRepository;

    @Transactional
    public void createTable(String tableName, List<String> columns) {
        try {
            String primaryKeyColumn = "id NUMBER PRIMARY KEY";
            String columnDefinitions = columns.stream()
                    .map(col -> col + " VARCHAR2(255)")
                    .collect(Collectors.joining(", "));

            String createTableSql = "CREATE TABLE c##safae." + tableName + " (" + primaryKeyColumn + ", " + columnDefinitions + ")";
            jdbcTemplate.execute(createTableSql);

            String sequenceName = tableName + "_seq";
            String createSequenceSql = "CREATE SEQUENCE c##safae." + sequenceName + " START WITH 1 INCREMENT BY 1";
            jdbcTemplate.execute(createSequenceSql);

            String triggerName = tableName + "_trg";
            String createTriggerSql = "CREATE OR REPLACE TRIGGER c##safae." + triggerName +
                    " BEFORE INSERT ON c##safae." + tableName +
                    " FOR EACH ROW " +
                    " BEGIN " +
                    "   :NEW.id := c##safae." + sequenceName + ".NEXTVAL; " +
                    " END;";
            jdbcTemplate.execute(createTriggerSql);

            jdbcTemplate.execute("COMMIT");

            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(tableName);
            tableInfo.setColumns(columns);
            tableInfoRepository.save(tableInfo);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating table: " + e.getMessage(), e);
        }
    }



    public List<TableInfo> listTables() {
        return tableInfoRepository.findAll();
    }
    @Transactional
    public void insertIntoTable(String tableName, List<String> values) {
        try {
            String insertSql = "INSERT INTO c##safae." + tableName + " (name, email, phone) VALUES ("
                    + String.join(", ", values.stream().map(val -> "'" + val + "'").collect(Collectors.toList())) + ")";
            jdbcTemplate.execute(insertSql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting into table: " + e.getMessage(), e);
        }
    }

    public TableInfo getTableInfo(String tableName) {
        return tableInfoRepository.findByTableName(tableName);
    }

    @Transactional
    public void editTable(String tableName, List<String> newColumns) {
        try {
            TableInfo tableInfo = tableInfoRepository.findByTableName(tableName);
            if (tableInfo != null) {
                List<String> existingColumns = tableInfo.getColumns();

                for (String newColumn : newColumns) {
                    if (!existingColumns.contains(newColumn)) {
                        String addColumnSql = "ALTER TABLE " + tableName + " ADD " + newColumn + " VARCHAR2(255)";
                        jdbcTemplate.execute(addColumnSql);
                    }
                }

                for (String oldColumn : existingColumns) {
                    if (!newColumns.contains(oldColumn)) {
                        String dropColumnSql = "ALTER TABLE " + tableName + " DROP COLUMN " + oldColumn;
                        jdbcTemplate.execute(dropColumnSql);
                    }
                }

                tableInfo.setColumns(newColumns);
                tableInfoRepository.save(tableInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error editing table: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteTable(String tableName) {
        try {
            String triggerName = tableName + "_trg";
            String dropTriggerSql = "DROP TRIGGER " + triggerName;
            jdbcTemplate.execute(dropTriggerSql);

            String sequenceName = tableName + "_seq";
            String dropSequenceSql = "DROP SEQUENCE " + sequenceName;
            jdbcTemplate.execute(dropSequenceSql);

            String dropTableSql = "DROP TABLE " + tableName;
            jdbcTemplate.execute(dropTableSql);

            TableInfo tableInfo = tableInfoRepository.findByTableName(tableName);
            if (tableInfo != null) {
                tableInfoRepository.delete(tableInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting table: " + e.getMessage(), e);
        }
    }

    public List<String> listTableNames() {
        return tableInfoRepository.findAll().stream()
                .map(TableInfo::getTableName)
                .collect(Collectors.toList());
    }

    public List<String> listColumns(String tableName) {
        TableInfo tableInfo = tableInfoRepository.findByTableName(tableName);
        return tableInfo != null ? tableInfo.getColumns() : null;
    }
}
