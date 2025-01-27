package org.example.oracle.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsSchedulerController {

    @Autowired
    private StatisticsSchedulerService schedulerService;

    /**
     * Planifie une tâche pour mettre à jour les statistiques d'une table ou d'un index.
     * @param objectName Nom de la table ou de l'index.
     * @param type Le type d'objet ("table" ou "index").
     * @param date La date et l'heure au format "yyyy-MM-dd HH:mm:ss".
     * @return Une réponse confirmant la planification.
     */
    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleTask(
            @RequestParam String objectName,
            @RequestParam String type,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {

        String sql;
        if ("table".equalsIgnoreCase(type)) {
            sql = "BEGIN DBMS_STATS.GATHER_TABLE_STATS(OWNNAME => SYS, TABNAME => ?, CASCADE => TRUE); END;";
        } else if ("index".equalsIgnoreCase(type)) {
            sql = "BEGIN DBMS_STATS.GATHER_INDEX_STATS(OWNNAME => USER, INDNAME => ?); END;";
        } else {
            return ResponseEntity.badRequest().body("Invalid type. Use 'table' or 'index'.");
        }

        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        schedulerService.scheduleTask(sql, objectName, dateTime);
        return ResponseEntity.ok(type + " statistics for '" + objectName + "' scheduled at " + dateTime);
    }

    /**
     * Recalcule immédiatement les statistiques globales.
     * @return Une réponse confirmant l'opération.
     */
    @PostMapping("/recalculate-global")
    public ResponseEntity<String> recalculateGlobalStatistics() {
        schedulerService.recalculateGlobalStatistics();
        return ResponseEntity.ok("Global statistics recalculated successfully.");
    }

    /**
     * Récupère les statuts des tâches.
     * @return Une liste des statuts.
     */
    @GetMapping("/task-status")
    public ResponseEntity<List<Map<String, Object>>> getTaskStatus() {
        List<Map<String, Object>> taskLogs = schedulerService.getTaskStatus();
        return ResponseEntity.ok(taskLogs);
    }
}
