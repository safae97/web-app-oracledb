//package org.example.oracle.SECURITY;//package org.example.oracle.SECURITY;
////import org.springframework.security.authentication.AuthenticationProvider;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.AuthenticationException;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.authentication.BadCredentialsException;
////
////public class JaasAuthenticationProvider implements AuthenticationProvider {
////
////    @Override
////    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
////        String username = authentication.getName();
////        String password = (String) authentication.getCredentials();
////
////        // Appeler votre service d'authentification JAAS ici
////        boolean isAuthenticated = new DBAAuthenticationService().authenticate(username, password);
////
////        if (isAuthenticated) {
////            // Retourner une instance d'Authentication réussie
////            return new UsernamePasswordAuthenticationToken(username, password);
////        } else {
////            throw new BadCredentialsException("Invalid credentials");
////        }
////    }
////
////    @Override
////    public boolean supports(Class<?> authentication) {
////        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
////    }
////}
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//
//import javax.security.auth.login.LoginContext;
//
//public class JAASAuthenticationProvider implements AuthenticationProvider {
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        LoginContext loginContext;
//        try {
//            loginContext = new LoginContext("OracleAuth", new JAASCallbackHandler(authentication));
//            loginContext.login();
//            return new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials());
//        } catch (Exception e) {
//            throw new AuthenticationException("JAAS authentication failed", e) {};
//        }
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}
