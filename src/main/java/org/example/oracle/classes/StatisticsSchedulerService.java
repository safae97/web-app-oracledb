package org.example.oracle.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsSchedulerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Planifie une tâche de mise à jour des statistiques pour une table ou un index.
     * @param sql La requête SQL à exécuter.
     * @param parameter Le paramètre (nom de la table ou de l'index).
     * @param dateTime La date et l'heure pour exécuter la tâche.
     */
    public void scheduleTask(String sql, String parameter, LocalDateTime dateTime) {
        ScheduledTaskExecutor.getInstance().schedule(() -> {
            try {
                jdbcTemplate.update(sql, parameter);
                System.out.println("Statistics updated for: " + parameter);
                logTask(parameter, "SCHEDULED_TASK", "SUCCESS", "Task executed successfully");
            } catch (Exception e) {
                System.err.println("Failed to update statistics for: " + parameter + ". Error: " + e.getMessage());
                logTask(parameter, "SCHEDULED_TASK", "FAILED", e.getMessage());
            }
        }, dateTime);
    }

    private void logTask(String parameter, String type, String status, String message) {
        String sql = "INSERT INTO SYS.STATISTICS_TASK_LOG (ID, PARAMETER, TASK_TYPE, STATUS, MESSAGE, CREATED_AT) " +
                "VALUES (STATISTICS_TASK_LOG_SEQ.NEXTVAL, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, parameter, type, status, message);
    }



    public List<Map<String, Object>> getTaskStatus() {
        String sql = "SELECT ID, PARAMETER, TASK_TYPE, STATUS, MESSAGE, CREATED_AT FROM STATISTICS_TASK_LOG ORDER BY CREATED_AT DESC";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Méthode pour recalculer les statistiques globales immédiatement.
     */
    public void recalculateGlobalStatistics() {
        String sql = "BEGIN DBMS_STATS.GATHER_DATABASE_STATS(CASCADE => TRUE); END;";
        jdbcTemplate.update(sql);
        System.out.println("Global statistics recalculated.");
        logTask("GLOBAL", "RECALCULATE_GLOBAL", "SUCCESS", "Global statistics recalculated successfully");
    }
}
