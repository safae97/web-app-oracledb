package org.example.oracle.controller;

import org.example.oracle.service.PerformanceMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/performance")
public class PerformanceMonitoringController {

    @Autowired
    private PerformanceMonitoringService monitoringService;

    @GetMapping
    public String getPerformance(Model model) {
        return "performance-dashboard";
    }
    @GetMapping("/awrReport")
    @ResponseBody
    public ResponseEntity<byte[]> getAwrReport() throws IOException {
        File awrReport = monitoringService.generateAwrReport();
        return downloadFile(awrReport);
    }

    @GetMapping("/ashReport")
    @ResponseBody
    public ResponseEntity<byte[]> getAshReport() throws IOException {
        File ashReport = monitoringService.generateAshReport();
        return downloadFile(ashReport);
    }

    private ResponseEntity<byte[]> downloadFile(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        byte[] fileBytes = inputStream.readAllBytes();

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .body(fileBytes);
    }
    @GetMapping("/realtime")
    @ResponseBody
    public Map<String, Object> getRealTimeStats() {
        return monitoringService.getRealTimeStats();
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "performance-dashboard"; // Return the HTML template name
    }
}

