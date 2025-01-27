package org.example.oracle.controller;

import org.example.oracle.service.BackupSchedulerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/schedule")
public class BackupScheduleController {

    private final BackupSchedulerService backupSchedulerService;

    @Autowired
    public BackupScheduleController(BackupSchedulerService backupSchedulerService) {
        this.backupSchedulerService = backupSchedulerService;
    }

    @GetMapping("/configure")
    public String showScheduleForm() {
        return "schedule"; // Correspond au fichier schedule.html
    }

    @PostMapping("/configure")
    public String configureBackupSchedule(
            @RequestParam("dateTime") String dateTime,
            @RequestParam(name = "isIncremental", defaultValue = "true") boolean isIncremental,
            RedirectAttributes redirectAttributes) {
        String result = backupSchedulerService.scheduleBackup(dateTime, isIncremental);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/schedule";
    }

    @GetMapping
    public String showSchedulePage(Model model) {
        return "backup"; // Correspond au fichier backup.html
    }
}
