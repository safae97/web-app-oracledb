package org.example.oracle.service;

import lombok.RequiredArgsConstructor;
import org.example.oracle.classes.dao.TDEConfigRepository;
import org.example.oracle.classes.dao.VPDPolicyRepository;
import org.example.oracle.classes.model.TDEConfig;
import org.example.oracle.classes.model.VPDPolicy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OracleSecurityServiceImpl implements OracleSecurityService {
    private final TDEConfigRepository tdeConfigRepository;
    private final VPDPolicyRepository vpdPolicyRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String SCHEMA_NAME = "c##SAFAE";

    // Audit Methods
    @Transactional
    public void enableAuditing(String policyName, String tableName, List<String> actions) {
        try {
            // Build the actions string (e.g., SELECT, INSERT, UPDATE, DELETE)
            String actionsString = String.join(", ", actions);

            // Create an audit policy
            String createPolicySql = String.format(
                    "CREATE AUDIT POLICY %s ACTIONS %s ON %s.%s",
                    policyName,
                    actionsString,
                    "SYS",
                    tableName
            );
            jdbcTemplate.execute(createPolicySql);

            // Enable the audit policy for the table
            String enablePolicySql = String.format("AUDIT POLICY %s BY ACCESS", policyName);
            jdbcTemplate.execute(enablePolicySql);
        } catch (Exception e) {
            System.out.println("Failed to enable auditing for table " + tableName + ": " + e.getMessage());
        }
    }
    @Transactional
    public void disableAuditing(String policyName) {
        try {
            // Disable the audit policy
            String disablePolicySql = String.format("NOAUDIT POLICY %s", policyName);
            jdbcTemplate.execute(disablePolicySql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable auditing for policy " + policyName + ": " + e.getMessage(), e);
        }
    }




    // TDE Methods
    @Transactional
    public TDEConfig enableColumnEncryption(String tableName, String columnName, String algorithm, String username) {
        try {
            String checkWalletSql = "SELECT STATUS FROM V$ENCRYPTION_WALLET";
            List<String> walletStatuses = jdbcTemplate.queryForList(checkWalletSql, String.class);

            if (!walletStatuses.contains("OPEN")) {
                throw new RuntimeException("Encryption wallet is not open. Please contact database administrator.");
            }

            String sql = String.format(
                    "ALTER TABLE %s.%s MODIFY %s ENCRYPT USING '%s'",
                    SCHEMA_NAME, tableName, columnName, algorithm
            );
            jdbcTemplate.execute(sql);

            TDEConfig config = new TDEConfig();
            config.setTableName(tableName);
            config.setColumnName(columnName);
            config.setEncryptionAlgorithm(algorithm);
            config.setCreatedAt(LocalDateTime.now());
            config.setCreatedBy(username);
            config.setActive(true);

            return tdeConfigRepository.save(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enable TDE encryption", e);
        }
    }

    @Transactional
    public void disableColumnEncryption(String tableName, String columnName) {
        TDEConfig config = tdeConfigRepository.findByTableNameAndColumnName(tableName, columnName);
        if (config == null) {
            throw new RuntimeException("No TDE configuration found");
        }

        try {
            // Decrypt the column in the database
            String sql = String.format(
                    "ALTER TABLE %s.%s MODIFY %s DECRYPT",
                    SCHEMA_NAME, tableName, columnName
            );
            jdbcTemplate.execute(sql);

            // Remove the record from the repository (delete)
            tdeConfigRepository.delete(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable TDE encryption", e);
        }
    }


    @Override
    public List<TDEConfig> getAllTDEConfigurations() {
        return tdeConfigRepository.findByActiveTrue(); // Fetch only active TDE configurations
    }

    @Transactional
    public VPDPolicy createPolicy(VPDPolicy policy, String username) {
        if (vpdPolicyRepository.existsByPolicyName(policy.getPolicyName())) {
            dropPolicy(policy.getPolicyName());
        }

        try {
            // Validate and format statementTypes
            String[] validStatements = {"SELECT", "INSERT", "UPDATE", "DELETE"};
            String[] inputStatements = policy.getStatementTypes().split(",");
            Set<String> sanitizedStatements = Arrays.stream(inputStatements)
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .filter(type -> Arrays.asList(validStatements).contains(type))
                    .collect(Collectors.toSet());

            if (sanitizedStatements.isEmpty()) {
                throw new IllegalArgumentException("Invalid statement types provided.");
            }

            String formattedStatementTypes = String.join(",", sanitizedStatements);

            // Create the policy function in the database
            String createFunctionSql = String.format(
                    "CREATE OR REPLACE FUNCTION %s.%s(schema_var IN VARCHAR2, table_var IN VARCHAR2) " +
                            "RETURN VARCHAR2 AS BEGIN %s END;",
                    SCHEMA_NAME, policy.getFunctionName(), policy.getPolicyFunction()
            );
            jdbcTemplate.execute(createFunctionSql);

            // Add the policy using DBMS_RLS.ADD_POLICY
            String addPolicySql = String.format(
                    "BEGIN DBMS_RLS.ADD_POLICY(" +
                            "object_schema => '%s', " +
                            "object_name => '%s', " +
                            "policy_name => '%s', " +
                            "function_schema => '%s', " +
                            "policy_function => '%s', " +
                            "statement_types => '%s'); END;",
                    SCHEMA_NAME, policy.getTableName(), policy.getPolicyName(),
                    SCHEMA_NAME, policy.getFunctionName(), formattedStatementTypes
            );
            jdbcTemplate.execute(addPolicySql);

            policy.setStatementTypes(formattedStatementTypes);
            policy.setCreatedAt(LocalDateTime.now());
            policy.setCreatedBy(username);
            policy.setActive(true);

            return vpdPolicyRepository.save(policy);
        } catch (Exception e) {
            // Log the full exception before throwing
            e.printStackTrace();
            throw new RuntimeException("Failed to create VPD policy: " + e.getMessage(), e);
        }
    }


    @Transactional
    public void dropPolicy(String policyName) {
        VPDPolicy policy = vpdPolicyRepository.findByPolicyName(policyName);
        if (policy == null) {
            throw new RuntimeException("Policy not found");
        }

        try {
            String dropPolicySql = String.format(
                    "BEGIN DBMS_RLS.DROP_POLICY(" +
                            "object_schema => '%s', " +
                            "object_name => '%s', " +
                            "policy_name => '%s'); END;",
                    SCHEMA_NAME, policy.getTableName(), policy.getPolicyName()
            );
            jdbcTemplate.execute(dropPolicySql);

            String dropFunctionSql = String.format(
                    "DROP FUNCTION %s.%s",
                    SCHEMA_NAME, policy.getFunctionName()
            );
            jdbcTemplate.execute(dropFunctionSql);

            policy.setActive(false);
            vpdPolicyRepository.save(policy);
        } catch (Exception e) {
            throw new RuntimeException("Failed to drop VPD policy", e);
        }
    }

    @Override
    public List<VPDPolicy> getAllPolicies() {
        return vpdPolicyRepository.findByActiveTrue();
    }





}
