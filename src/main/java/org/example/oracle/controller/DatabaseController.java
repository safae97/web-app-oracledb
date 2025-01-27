package org.example.oracle.controller;

import org.example.oracle.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Controller
@RequestMapping("/database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/index-stats")
    public String showIndexStatsForm() {
        return "index-stats";
    }

    @PostMapping("/index-stats")
    public String gatherIndexStats(@RequestParam String owner, @RequestParam String indexName, Model model) {
        Map<String, Object> stats = databaseService.getIndexStats(owner, indexName);
        model.addAttribute("stats", stats);
        model.addAttribute("owner", owner);
        model.addAttribute("indexName", indexName);

        return "index-stats";  // Affiche la page avec les statistiques d'index
    }

    @PostMapping("/update-index-stats")
    public String updateIndexStats(@RequestParam String owner, @RequestParam String indexName, Model model) {
        owner = owner.toUpperCase();

        databaseService.gatherIndexStats(owner, indexName);

        Map<String, Object> stats = databaseService.getIndexStats(owner, indexName);
        model.addAttribute("stats", stats);
        model.addAttribute("owner", owner);
        model.addAttribute("indexName", indexName);
        model.addAttribute("message", "Index stats updated successfully.");

        return "index-stats";
    }

    @GetMapping("/table-stats")
    public String showTableStatsForm() {
        return "table-stats"; // Affiche le formulaire pour les stats de table
    }

    @PostMapping("/table-stats")
    public String gatherTableStats(@RequestParam String owner, @RequestParam String tableName, Model model) {
        Map<String, Object> stats = databaseService.getTableStats(owner, tableName);
        model.addAttribute("stats", stats);
        model.addAttribute("owner", owner);
        model.addAttribute("tableName", tableName);

        return "table-stats";
    }

    @PostMapping("/update-table-stats")
    public String updateTableStats(@RequestParam String owner, @RequestParam String tableName, Model model) {
        owner = owner.toUpperCase();

        databaseService.gatherTableStats(owner, tableName);

        Map<String, Object> stats = databaseService.getTableStats(owner, tableName);
        model.addAttribute("stats", stats);
        model.addAttribute("owner", owner);
        model.addAttribute("tableName", tableName);
        model.addAttribute("message", "Table stats updated successfully.");

        return "table-stats";
    }
}
