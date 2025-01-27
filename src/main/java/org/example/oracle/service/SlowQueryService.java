package org.example.oracle.service;

import org.example.oracle.classes.SlowQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlowQueryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<SlowQuery> getSlowQueries() {
        double threshold = 8000.0;

        String sql = "SELECT sql_id, sql_text, " +
                "CASE WHEN executions > 0 THEN elapsed_time / executions " +
                "ELSE 0 END AS avg_execution_time, " +
                "executions, last_active_time " +
                "FROM v$sql " +
                "WHERE executions > 0 " +
                "AND elapsed_time / executions > ? " +
                "ORDER BY avg_execution_time DESC " +
                "FETCH FIRST 20 ROWS ONLY";

        return jdbcTemplate.query(sql, new Object[]{threshold}, (rs, rowNum) -> {
            SlowQuery query = new SlowQuery();
            query.setSqlId(rs.getString("sql_id"));
            query.setSqlText(rs.getString("sql_text"));
            query.setAvgExecutionTime(rs.getDouble("avg_execution_time"));
            query.setExecutions(rs.getInt("executions"));
            query.setLastActiveTime(rs.getTimestamp("last_active_time"));
            return query;
        });
    }

    public String optimizeQuery(String sqlId) {
        String sql = "BEGIN " +
                "   DECLARE " +
                "      l_sql_tune_task_id VARCHAR2(100); " +
                "   BEGIN " +
                "      l_sql_tune_task_id := DBMS_SQLTUNE.create_tuning_task( " +
                "         sql_id => ?, " +
                "         scope => DBMS_SQLTUNE.scope_comprehensive, " +
                "         time_limit => 500, " +
                "         task_name => ? || '_tuning_task', " +
                "         description => 'Tuning task for ' || ? " +
                "      ); " +
                "      DBMS_SQLTUNE.execute_tuning_task(task_name => ? || '_tuning_task'); " +
                "   END; " +
                "END;";

        String taskName = sqlId + "_tuning_task";
        jdbcTemplate.update(sql, sqlId, sqlId, sqlId, sqlId);
        return getTuningReport(taskName);
    }

    public String getTuningReport(String taskName) {
        String sql = "SELECT DBMS_SQLTUNE.report_tuning_task(?) FROM DUAL";
        return jdbcTemplate.queryForObject(sql, new Object[]{taskName}, String.class);
    }
}
