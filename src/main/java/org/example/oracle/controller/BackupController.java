package org.example.oracle.controller;

import org.example.oracle.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/backup")
public class BackupController {

    private final BackupService backupService;

    @Autowired
    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping("/run")
    public String runBackup(@RequestParam(name = "incremental", defaultValue = "true") boolean isIncremental,
                            RedirectAttributes redirectAttributes) {
        String result = backupService.runBackup(isIncremental);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/backup";
    }

    @GetMapping("/history")
    public String getBackupHistory(Model model) {
        String history = backupService.getBackupHistory();
        model.addAttribute("history", history);
        return "backup-history"; // Affiche la page backup-history.html
    }

    @PostMapping("/restore")
    public String restoreDatabase(@RequestParam String restoreDate, RedirectAttributes redirectAttributes) {
        String result = backupService.restoreDatabaseToDate(restoreDate);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/backup";
    }

    @GetMapping
    public String showBackupPage(Model model) {
        return "backup";
    }
}
