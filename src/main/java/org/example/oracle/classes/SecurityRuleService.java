package org.example.oracle.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SecurityRuleService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Appliquer une politique VPD pour restreindre l'accès à une table pour un utilisateur donné.
     */
    public void restrictTableAccessForUser(String schemaName, String userName, String tableName) {
        String policyName = "POLICY_" + userName;

        try {
            jdbcTemplate.update(
                    "BEGIN DBMS_RLS.DROP_POLICY(?, ?, ?); END;",
                    schemaName, tableName, policyName
            );
        } catch (Exception e) {
            // Ignorer les erreurs si la politique n'existe pas
            System.err.println("Aucune politique existante à supprimer : " + e.getMessage());
        }

        jdbcTemplate.update(
                "BEGIN " +
                        "   DBMS_RLS.ADD_POLICY(?, ?, ?, ?, 'SECURITY_FUNCTION', 'SELECT, INSERT, UPDATE, DELETE'); " +
                        "END;",
                schemaName, tableName, policyName, schemaName
        );
    }
}
