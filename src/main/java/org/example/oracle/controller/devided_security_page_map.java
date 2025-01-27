package org.example.oracle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/audit-logs")
public class devided_security_page_map {



    // Afficher le formulaire de recherche des logs
    @GetMapping("/search")
    public String showAuditLogSearchForm() {
        return "audit-log-search"; // Correspond au fichier audit-log-search.html
    }


}
