//package org.example.oracle.SECURITY;
//import javax.security.auth.callback.Callback;
//import javax.security.auth.callback.CallbackHandler;
//import javax.security.auth.callback.NameCallback;
//import javax.security.auth.callback.PasswordCallback;
//import org.springframework.security.core.Authentication;
//
//public class JAASCallbackHandler implements CallbackHandler {
//
//    private final Authentication authentication;
//
//    public JAASCallbackHandler(Authentication authentication) {
//        this.authentication = authentication;
//    }
//
//    @Override
//    public void handle(Callback[] callbacks) {
//        for (Callback callback : callbacks) {
//            if (callback instanceof NameCallback) {
//                ((NameCallback) callback).setName(authentication.getName());
//            } else if (callback instanceof PasswordCallback) {
//                ((PasswordCallback) callback).setPassword(authentication.getCredentials().toString().toCharArray());
//            }
//        }
//    }
//}
