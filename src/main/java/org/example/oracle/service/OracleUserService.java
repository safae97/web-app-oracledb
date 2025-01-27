package org.example.oracle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OracleUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createUser(String username, String password, String tablespace, int quota) {
        System.out.println("Creating user with quota: " + quota);

        long quotaInBytes = (long) quota * 1024 * 1024;  // Convert quota from MB to bytes
        String sqlCreateUser = "CREATE USER " + username + " IDENTIFIED BY " + password +
                " DEFAULT TABLESPACE " + tablespace;

        jdbcTemplate.execute(sqlCreateUser);

        String sqlQuota = "ALTER USER " + username +
                " QUOTA " + quotaInBytes + " ON " + tablespace;

        jdbcTemplate.execute(sqlQuota);

        System.out.println("User " + username + " created with a quota of " + quota + " MB on tablespace " + tablespace);
    }






    public void updateUser(String username, String password, String tablespace, Integer quota) {
        username = username.toUpperCase();

        if (password != null && !password.isEmpty()) {
            updateUserPassword(username, password);
        }
        if (tablespace != null && !tablespace.isEmpty()) {
            updateUserTablespace(username, tablespace);
        }
        if (quota != null && quota > 0) {
            updateUserQuota(username, quota, tablespace);
        }
    }


    public List<Map<String, Object>> listUsers() {
        String sql = "SELECT " +
                "u.USERNAME AS user_name, " +
                "u.ACCOUNT_STATUS AS account_status, " +
                "u.DEFAULT_TABLESPACE AS default_tablespace, " +
                "q.TABLESPACE_NAME AS tablespace_name, " +
                "COALESCE(TO_CHAR(q.BYTES), '0') AS used_space, " +
                "CASE WHEN q.MAX_BYTES = -1 THEN 'UNLIMITED' ELSE COALESCE(TO_CHAR(q.MAX_BYTES), '0') END AS quota, " +
                "COALESCE(CASE " +
                "    WHEN u.USERNAME = 'SYS' THEN 'ALL' " +
                "    ELSE LISTAGG(r.GRANTED_ROLE, ', ') WITHIN GROUP (ORDER BY r.GRANTED_ROLE) " +
                "END, 'NONE') AS granted_roles " +  // Fallback to 'NONE' if no roles are assigned
                "FROM DBA_USERS u " +
                "LEFT JOIN DBA_TS_QUOTAS q ON u.USERNAME = q.USERNAME " +
                "LEFT JOIN DBA_ROLE_PRIVS r ON u.USERNAME = r.GRANTEE " +  // Ensure correct join with roles
                "GROUP BY u.USERNAME, u.ACCOUNT_STATUS, u.DEFAULT_TABLESPACE, q.TABLESPACE_NAME, q.BYTES, q.MAX_BYTES";

        return jdbcTemplate.queryForList(sql);
    }

    public void updateUserQuota(String username, int quota, String tablespace) {
        username = username.toUpperCase();
        String sql = String.format("ALTER USER %s QUOTA %dM ON %s", username, quota, tablespace);  // Format SQL for max quota
        jdbcTemplate.execute(sql);
    }





    public void deleteUser(String username) {

        String sql = "DROP USER " + username + " CASCADE";

        jdbcTemplate.execute(sql);
    }


    public void updateUserPassword(String username, String newPassword) {
        String sql = "ALTER USER " + username + " IDENTIFIED BY " + newPassword;
        jdbcTemplate.execute(sql);
    }

    // Update default tablespace
    public void updateUserTablespace(String username, String tablespace) {
        String sql = "ALTER USER " + username + " DEFAULT TABLESPACE " + tablespace;
        jdbcTemplate.execute(sql);
    }

    // Lock user account
    public void lockUserAccount(String username) {
        String sql = "ALTER USER " + username + " ACCOUNT LOCK";
        jdbcTemplate.execute(sql);
    }

    // Unlock user account
    public void unlockUserAccount(String username) {
        String sql = "ALTER USER " + username + " ACCOUNT UNLOCK";
        jdbcTemplate.execute(sql);
    }
    // Method to assign a role to a user, with an optional admin option
    public void assignRoleToUser(String username, String role, boolean withAdminOption) {
        // Base SQL statement
        username = username.toUpperCase();

        String sql = "GRANT " + role + " TO " + username;

        // Add 'WITH ADMIN OPTION' if required
        if (withAdminOption) {
            sql += " WITH ADMIN OPTION";
        }

        // Execute the SQL
        jdbcTemplate.execute(sql);
    }


    // Grant privilege to user
    public void grantPrivilegeToUser(String username, String privilege, String tableName, boolean isAdmin) {
        username = username.toUpperCase();
        String grantQuery = String.format("GRANT %s ON %s TO %s", privilege, tableName, username);
        jdbcTemplate.execute(grantQuery);
    }

    public List<String> getUserRoles(String username) {
        username = username.toUpperCase();
        String sql = "SELECT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTEE = ?";
        return jdbcTemplate.queryForList(sql, String.class, username);
    }

    // Get privileges granted to a user
    public List<Map<String, Object>> getUserPrivileges(String username) {
        username = username.toUpperCase();

        String sql = "SELECT PRIVILEGE, TABLE_NAME FROM DBA_TAB_PRIVS WHERE GRANTEE = ?";
        return jdbcTemplate.queryForList(sql, username);
    }
    public void changePasswordMinLength(int minLength) {
        String sql = "ALTER PROFILE DEFAULT LIMIT PASSWORD_VERIFY_FUNCTION 'VERIFY_FUNCTION_MINLEN' PASSWORD_LIFE_TIME UNLIMITED PASSWORD_REUSE_TIME UNLIMITED PASSWORD_REUSE_MAX UNLIMITED PASSWORD_LOCK_TIME UNLIMITED FAILED_LOGIN_ATTEMPTS 5 PASSWORD_GRACE_TIME UNLIMITED";
        jdbcTemplate.execute(sql);
        // Customize the function VERIFY_FUNCTION_MINLEN to check minimum length
    }

    // Method to change password expiration period
    public void changePasswordExpiration(int expirationDays) {
        String sql = "ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME " + expirationDays;
        jdbcTemplate.execute(sql);
    }

    // Method to change account lockout password lock time
    public void changePasswordLockTime(int lockTime) {
        String sql = "ALTER PROFILE DEFAULT LIMIT PASSWORD_LOCK_TIME " + lockTime;
        jdbcTemplate.execute(sql);
    }
    public Map<String, String> getPasswordAndLockSettings() {
        String sql = "SELECT RESOURCE_NAME, LIMIT " +
                "FROM DBA_PROFILES " +
                "WHERE PROFILE = 'DEFAULT' " +
                "AND RESOURCE_NAME IN ('PASSWORD_LIFE_TIME', 'PASSWORD_LOCK_TIME')";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        Map<String, String> settings = new HashMap<>();
        for (Map<String, Object> row : results) {
            settings.put((String) row.get("RESOURCE_NAME"), (String) row.get("LIMIT"));
        }
        return settings;
    }
}
