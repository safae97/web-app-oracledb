package org.example.oracle.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/security")
public class SecurityRuleController {

    @Autowired
    private SecurityRuleService securityRuleService;

    /**
     * Appliquer une restriction sur une table pour un utilisateur donné.
     */
    @PostMapping("/restrict-access")
    public String restrictAccess(@RequestParam String schemaName,
                                 @RequestParam String userName,
                                 @RequestParam String tableName) {
        securityRuleService.restrictTableAccessForUser(schemaName, userName, tableName);
        return "Accès restreint appliqué avec succès pour l'utilisateur : " + userName +
                " sur la table : " + tableName + " dans le schéma : " + schemaName;
    }
}
