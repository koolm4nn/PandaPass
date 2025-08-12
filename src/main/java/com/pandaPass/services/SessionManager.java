package com.pandaPass.services;

public class SessionManager {
    private static LoginSession loginSession;
    private static VaultSession vaultSession;

    public static boolean isUserLoggedIn(){
        return vaultSession != null;
    }

    public static void setLoginSession(LoginSession session){
        loginSession = session;
    }

    public static void clearLoginSession(){
        loginSession.clear();
        loginSession = null;
    }

    public static LoginSession getLoginSession(){
        return loginSession;
    }

    public static void setVaultSession(VaultSession session){
        vaultSession = session;
    }

    public static void clearVaultSession(){
        vaultSession.clear();
        vaultSession = null;
    }

    public static VaultSession getVaultSession(){
        return vaultSession;
    }
}
