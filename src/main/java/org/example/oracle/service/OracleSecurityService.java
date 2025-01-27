package org.example.oracle.service;//package org.example.oracle.service;
//
//
//import jakarta.transaction.Transactional;
//import org.example.oracle.dao.EncryptionPolicyRepository;
//import org.example.oracle.model.EncryptionPolicy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@Transactional
//public class DatabaseSecurityService {
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private EncryptionPolicyRepository encryptionPolicyRepository;
//
////    public void enableTDE(String tableName, String columnName) {
////        try {
////            // Check if the table exists in the correct schema
////            String checkTableOwnershipQuery = "SELECT owner FROM all_tables WHERE table_name = ?";
////
////            String owner = jdbcTemplate.queryForObject(checkTableOwnershipQuery, String.class, tableName);
////            if (owner != null && !owner.equalsIgnoreCase("SYS")) {
////                // Apply TDE encryption if table is in the correct schema
////                String sql = "ALTER TABLE c##safae." + tableName +
////                        " MODIFY (" + columnName + " ENCRYPT USING 'AES256' NO SALT)";
////                jdbcTemplate.execute(sql);
////
////                // Save encryption policy to repository
////                EncryptionPolicy policy = new EncryptionPolicy();
////                policy.setTableName(tableName);
////                policy.setColumnName(columnName);
////                policy.setEncryptionType("AES256");
////                policy.setEnabled(true);
////                encryptionPolicyRepository.save(policy);
////            } else {
////                throw new RuntimeException("Table must not be owned by SYS user.");
////            }
////        } catch (Exception e) {
////            throw new RuntimeException("Error enabling TDE: " + e.getMessage(), e);
////        }
////    }
//public void enableTDE(String tableName, String columnName) {
//    try {
//        // Ensure the table name is uppercase, as Oracle stores unquoted table names in uppercase
//        tableName = tableName.toUpperCase();
//
//        // Check if the table exists in the correct schema and get the owner
//        String checkTableOwnershipQuery = "SELECT owner FROM all_tables WHERE UPPER(table_name) = UPPER(?) AND owner = 'C##SAFAE'";
//
//        // Query to get a list of owners
//        List<String> owners = jdbcTemplate.queryForList(checkTableOwnershipQuery, String.class, tableName);
//
//        // If there are multiple owners, you may want to handle that case differently
//        if (owners.size() == 1 && owners.get(0).equalsIgnoreCase("C##SAFAE")) {
//            // Switch to CDB$ROOT container
////            jdbcTemplate.execute("ALTER SESSION SET CONTAINER = CDB$ROOT");
//
//            // Check the wallet status
//            String checkWalletStatusQuery = "SELECT status FROM v$encryption_wallet";
//            String walletStatus = jdbcTemplate.queryForObject(checkWalletStatusQuery, String.class);
//
//            // If the wallet is not open, open it
//            if (!"OPEN".equalsIgnoreCase(walletStatus)) {
//                String openWalletQuery = "ALTER SYSTEM SET ENCRYPTION WALLET OPEN IDENTIFIED BY 'osotonase199'";
//                jdbcTemplate.execute(openWalletQuery);
//            }
//
//            // Apply TDE encryption to the specified column
//            String sql = "ALTER TABLE c##safae." + tableName + " MODIFY (" + columnName + " ENCRYPT USING 'AES256' NO SALT)";
//            jdbcTemplate.execute(sql);
//
//            // Save encryption policy to repository
//            EncryptionPolicy policy = new EncryptionPolicy();
//            policy.setTableName(tableName);
//            policy.setColumnName(columnName);
//            policy.setEncryptionType("AES256");
//            policy.setEnabled(true);
//            encryptionPolicyRepository.save(policy);
//        } else {
//            throw new RuntimeException("Table must be owned by C##SAFAE.");
//        }
//    } catch (Exception e) {
//        throw new RuntimeException("Error enabling TDE: " + e.getMessage(), e);
//    }
//}
//
//
//
//
//
//    public void enableAudit(String tableName) {
//        String sql = "AUDIT SELECT, INSERT, UPDATE, DELETE ON " + tableName;
//        jdbcTemplate.execute(sql);
//    }
//
//    public void configureVPD(String tableName, String policyFunction) {
//        // First drop existing policy if it exists
//        String dropPolicySQL = "BEGIN " +
//                "DBMS_RLS.DROP_POLICY(" +
//                "  object_schema => USER, " +
//                "  object_name => ?, " +
//                "  policy_name => ? " +
//                "); EXCEPTION WHEN OTHERS THEN NULL; END;";
//
//        jdbcTemplate.update(dropPolicySQL, tableName, tableName + "_policy");
//
//        // Create function and add policy (rest of your existing code)
//        String checkFunctionSQL = "SELECT COUNT(*) FROM USER_OBJECTS WHERE OBJECT_TYPE = 'FUNCTION' AND OBJECT_NAME = ?";
//        Integer functionCount = jdbcTemplate.queryForObject(checkFunctionSQL, Integer.class, policyFunction);
//
//        if (functionCount == null || functionCount == 0) {
//            String functionCreationSQL = "CREATE OR REPLACE FUNCTION " + policyFunction +
//                    " (schema_name IN VARCHAR2, table_name IN VARCHAR2) " +
//                    "RETURN VARCHAR2 AS BEGIN RETURN 'id = 1'; END;";
//            jdbcTemplate.execute(functionCreationSQL);
//        }
//
//        String vpdSQL = "BEGIN DBMS_RLS.ADD_POLICY(" +
//                "object_schema => USER, " +
//                "object_name => ?, " +
//                "policy_name => ?, " +
//                "function_schema => USER, " +
//                "policy_function => ?); END;";
//
//        jdbcTemplate.update(vpdSQL, tableName, tableName + "_policy", policyFunction);
//}
//}
//


import org.example.oracle.classes.model.TDEConfig;
import org.example.oracle.classes.model.VPDPolicy;

import java.util.List;

public interface OracleSecurityService {

    // TDE Methods
    public TDEConfig enableColumnEncryption(String tableName, String columnName, String algorithm, String username) ;

    void disableColumnEncryption(String tableName, String columnName);

    List<TDEConfig> getAllTDEConfigurations();

    // VPD Methods
    VPDPolicy createPolicy(VPDPolicy policy, String username);

    void dropPolicy(String policyName);

    List<VPDPolicy> getAllPolicies();

}

