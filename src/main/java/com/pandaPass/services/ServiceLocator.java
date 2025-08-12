package com.pandaPass.services;

import com.pandaPass.AppLifecycle;
import com.pandaPass.repositories.CategoryRepository;
import com.pandaPass.repositories.UserRepository;
import com.pandaPass.view.ScenesLoader;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Provider for multiple instances for a user session.
 */
public class ServiceLocator {
    private static UserRepository userRepository;
    private static UserService userService;
    private static CategoryRepository categoryRepository;
    private static CategoryService categoryService;
    //private static VaultSessionService vaultSessionService;
    private static VaultSessionTimeoutService vaultSessionTimeoutService;
    private static ScenesLoader scenesLoader;
    private static PwnedPasswordService pwnedPasswordService;

    private static final int timeOutDuration = 60;

    private ServiceLocator(){}

    /**
     * Initializes all required services for a session.
     */
    public static void init(){
        userRepository = new UserRepository();
        //vaultSessionService = new VaultSessionService();
        userService = new UserService(userRepository);

        categoryRepository = new CategoryRepository();
        categoryService = new CategoryService(categoryRepository);
        pwnedPasswordService = new PwnedPasswordService();
    }

    public static void startTimeoutService(){
        // Add session timeout after 120 seconds.
        vaultSessionTimeoutService = new VaultSessionTimeoutService(timeOutDuration, () -> Platform.runLater(AppLifecycle::endUserSession));
        vaultSessionTimeoutService.resetTimer();
    }

    public static void initializeScenesLoader(Stage primaryStage){
        scenesLoader = new ScenesLoader(primaryStage);
    }

    public static ScenesLoader getScenesLoader(){
        return scenesLoader;
    }

    public static UserRepository getUserRepository(){
        return userRepository;
    }

    public static UserService getUserService(){
        return userService;
    }

    public static PwnedPasswordService getPwnedPasswordService(){ return pwnedPasswordService;}

    //public static VaultSessionService getVaultSessionService(){
    //    return vaultSessionService;
    //}

    public static VaultSessionTimeoutService getVaultSessionTimeoutService(){
        return vaultSessionTimeoutService;
    }

    public static CategoryService getCategoryService(){
        return categoryService;
    }

    // Ends the thread of the timeout-service and sets instances to null.
    public static void shutdown(){
        if(vaultSessionTimeoutService != null){
            vaultSessionTimeoutService.stopTimer();
        }
        userRepository = null;
        userService = null;
        vaultSessionTimeoutService = null;
        categoryRepository = null;
        categoryService = null;
    }

    public static void startSessionServices(){
        startTimeoutService();
    }

    public static void endUserSessionServices(){
        if(vaultSessionTimeoutService != null){
            vaultSessionTimeoutService.stopTimer();
        }
        vaultSessionTimeoutService = null;
        SessionManager.clearVaultSession();
    }
}
