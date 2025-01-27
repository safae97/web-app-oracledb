package org.example.oracle.controller;

import org.example.oracle.classes.SlowQuery;
import org.example.oracle.service.SlowQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/slow-queries")
public class SlowQueryController {

    @Autowired
    private SlowQueryService slowQueryService;

    @GetMapping
    public String showSlowQueriesPage(Model model) {
        List<SlowQuery> slowQueries = slowQueryService.getSlowQueries();
        model.addAttribute("slowQueries", slowQueries);
        return "slow-queries";
    }

    @PostMapping("/optimize/{sqlId}")
    public String optimizeQuery(@PathVariable String sqlId, Model model, RedirectAttributes redirectAttributes) {
        try {
            String tuningReport = slowQueryService.optimizeQuery(sqlId);
            model.addAttribute("tuningReport", tuningReport);
            return "optimization-report";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'optimisation : " + e.getMessage());
            return "redirect:/slow-queries";
        }
    }
}
