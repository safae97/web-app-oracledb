//package org.example.oracle.SECURITY;//package org.example.oracle.SECURITY;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.web.SecurityFilterChain;
////
////
////@Configuration
////@EnableWebSecurity
////public class SecurityConfig {
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .csrf().disable()
////                .authorizeHttpRequests(authz -> authz
////                        .anyRequest().authenticated()
////                )
////                .formLogin(form -> form
////                        .loginPage("/login")
////                        .permitAll()
////                )
////                .logout(logout -> logout
////                        .permitAll()
////                );
////
////        http.authenticationProvider(new JaasAuthenticationProvider());
////
////        return http.build();
////    }
////
////
////}
////
//import javax.security.auth.spi.LoginModule;
//import javax.security.auth.Subject;
//import javax.security.auth.callback.CallbackHandler;
//import javax.security.auth.callback.NameCallback;
//import javax.security.auth.callback.PasswordCallback;
//import javax.security.auth.callback.Callback;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.Map;
//
//public class OracleLoginModule implements LoginModule {
//
//    private Subject subject;
//    private CallbackHandler callbackHandler;
//    private String username;
//    private String password;
//
//    @Override
//    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
//        this.subject = subject;
//        this.callbackHandler = callbackHandler;
//    }
//
//    @Override
//    public boolean login() throws SecurityException {
//        try {
//            Callback[] callbacks = new Callback[2];
//            callbacks[0] = new NameCallback("Username: ");
//            callbacks[1] = new PasswordCallback("Password: ", false);
//            callbackHandler.handle(callbacks);
//
//            username = ((NameCallback) callbacks[0]).getName();
//            password = new String(((PasswordCallback) callbacks[1]).getPassword());
//
//            return authenticateUser(username, password);
//        } catch (Exception e) {
//            throw new SecurityException("Login failed", e);
//        }
//    }
//
//    private boolean authenticateUser(String username, String password) {
//        String jdbcUrl = "jdbc:oracle:thin:@//localhost:1521/FREE";
//        String dbUsername = "sys as sysdba";
//        String dbPassword = "oracle";
//
//        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
//            String sql = "SELECT 1 FROM all_users WHERE username = ? AND password = ?";
//            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//                stmt.setString(1, username.toUpperCase());
//                stmt.setString(2, password); // Assume password is stored as plaintext (use hashing in production)
//
//                try (ResultSet rs = stmt.executeQuery()) {
//                    return rs.next();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    @Override
//    public boolean commit() {
//        return true;
//    }
//
//    @Override
//    public boolean abort() {
//        return false;
//    }
//
//    @Override
//    public boolean logout() {
//        return true;
//    }
//}
