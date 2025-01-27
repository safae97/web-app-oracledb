package org.example.oracle.controller;//package org.example.oracle.controller;
//
//
//import org.example.oracle.service.DatabaseSecurityService;
//import org.example.oracle.service.TableManagementService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/security")
//public class SecurityController {
//    @Autowired
//    private DatabaseSecurityService securityService;
//
//    @Autowired
//    private TableManagementService tableManagementService;
//
//    @PostMapping("/tde/enable")
//    public ResponseEntity<?> enableTDE(@RequestParam String tableName,
//                                       @RequestParam String columnName) {
//        try {
//            securityService.enableTDE(tableName, columnName);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error enabling TDE: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/audit/enable")
//    public ResponseEntity<?> enableAudit(@RequestParam String tableName) {
//        try {
//            securityService.enableAudit(tableName);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error enabling audit: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/vpd/configure")
//    public ResponseEntity<?> configureVPD(@RequestParam String tableName,
//                                          @RequestParam String policyFunction) {
//        try {
//            securityService.configureVPD(tableName, policyFunction);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error configuring VPD: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/tables")
//    public ResponseEntity<List<String>> getTables() {
//        try {
//            List<String> tables = tableManagementService.listTableNames();
//            return ResponseEntity.ok(tables);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
//    }
//
//    @GetMapping("/columns")
//    public ResponseEntity<List<String>> getColumns(@RequestParam String tableName) {
//        try {
//            List<String> columns = tableManagementService.listColumns(tableName);
//            return ResponseEntity.ok(columns);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
// }
//    }
//}

import lombok.RequiredArgsConstructor;
import org.example.oracle.classes.model.TDEConfig;
import org.example.oracle.classes.model.VPDPolicy;
import org.example.oracle.service.OracleSecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {
    private final OracleSecurityService securityService;
    private static final String DEFAULT_USER = "SYSTEM"; // Temporary solution

    @GetMapping("/tde")
    public String getSecurityPage() {
        return "security";
    }
    @GetMapping("/security-vdp")
    public String getSecurityVDP() {
        return "security-vdp";
    }
    @PostMapping("/tde/enable")
    public String enableTDE(
            @RequestParam String tableName,
            @RequestParam String columnName,
            @RequestParam String algorithm,@RequestParam String username) {
        securityService.enableColumnEncryption(tableName, columnName,algorithm,username);
        return "redirect:/api/security/tde";
    }

    @PostMapping("/tde/disable")
    public String disableTDE(
            @RequestParam String tableName,
            @RequestParam String columnName) {
        securityService.disableColumnEncryption(tableName, columnName);
        return "redirect:/api/security/tde";    }

    @GetMapping("/tde/configurations")
    public ResponseEntity<List<TDEConfig>> getTDEConfigurations() {
        return ResponseEntity.ok(securityService.getAllTDEConfigurations());
    }

    @PostMapping("/vdp/policies")
    public String createVPDPolicy(
            @RequestParam String tableName,
            @RequestParam String policyName,
            @RequestParam String statementTypes) {
        try {
            VPDPolicy policy = new VPDPolicy();
            policy.setTableName(tableName);
            policy.setPolicyName(policyName);
            policy.setStatementTypes(statementTypes);
            policy.setCreatedAt(LocalDateTime.now());
            policy.setCreatedBy(DEFAULT_USER);
            policy.setActive(true);

            String functionName = tableName + "_VPD_FUNCTION";
            String policyFunction = "BEGIN " +
                    "  -- Example policy function logic for table " + tableName + " and policy " + policyName + " " +
                    "  RETURN 'OK'; " +
                    "END;";

            policy.setFunctionName(functionName);
            policy.setPolicyFunction(policyFunction);

            securityService.createPolicy(policy, DEFAULT_USER);

            return "redirect:/api/security/security-vdp";
        } catch (IllegalArgumentException e) {
            return "redirect:/api/security/security-vdp?error=true";
        } catch (Exception e) {
            return "redirect:/api/security/security-vdp?error=true";
        }
    }




    @PostMapping("/vdp/drop")
    public String dropVPDPolicy(@RequestParam String tableName, @RequestParam String policyName) {
        securityService.dropPolicy(policyName);
        return "redirect:/api/security/security-vdp";
    }


    @GetMapping("/vdp/policies")
    public ResponseEntity<List<VPDPolicy>> getVPDPolicies() {
        return ResponseEntity.ok(securityService.getAllPolicies()); // This will return only active policies now
    }


    // Audit Endpoints
//    @PostMapping("/audit/enable")
//    public ResponseEntity<AuditConfig> enableAuditing(@RequestBody AuditConfig config) {
//        return ResponseEntity.ok(securityService.enableAuditing(config, DEFAULT_USER));
//    }
//
//    @PostMapping("/audit/disable")
//    public ResponseEntity<Void> disableAuditing(@RequestParam String tableName) {
//        securityService.disableAuditing(tableName);
//        return ResponseEntity.ok().build();
//    }
//    @PostMapping("/enable")
//    public ResponseEntity<String> enableAudit(@RequestParam String auditOption) {
//        securityService.enableAudit(auditOption);
//        return ResponseEntity.ok("Audit enabled successfully.");
//    }

//    @PostMapping("/disable")
//    public ResponseEntity<String> disableAudit(@RequestParam String auditOption) {
//        securityService.disableAudit(auditOption);
//        return ResponseEntity.ok("Audit disabled successfully.");
//    }
//
//    @GetMapping("/status")
//    public ResponseEntity<List<Map<String, Object>>> getAuditStatus() {
//        List<Map<String, Object>> audits = securityService.getAuditStatus();
//        return ResponseEntity.ok(audits);
//    }
//    @GetMapping("/audit/configurations")
//    public ResponseEntity<List<AuditConfig>> getAuditConfigurations() {
//        return ResponseEntity.ok(securityService.getAllAuditConfigurations());
//}
// Audit Endpoints
//@PostMapping("/audit/enable")
//public ResponseEntity<String> enableAuditing(
//        @RequestParam String policyName,
//        @RequestParam String tableName,
//        @RequestParam List<String> actions) {
//
//    securityService.enableAuditing(policyName, tableName, actions);
//    return ResponseEntity.ok("Auditing enabled for table " + tableName + " with policy " + policyName + " successfully");
//}
//
//
//    @PostMapping("/audit/disable")
//    public ResponseEntity<Void> disableAuditing(@RequestParam String policyName) {
//        securityService.disableAuditing(policyName);
//        return ResponseEntity.ok().build();
//    }
//
//
//
//    @GetMapping("/audit/configurations")
//    public ResponseEntity<List<AuditConfig>> getAuditConfigurations() {
//        return ResponseEntity.ok(securityService.getAllAuditConfigurations());
//    }


}
