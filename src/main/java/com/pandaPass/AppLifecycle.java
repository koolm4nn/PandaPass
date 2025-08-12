package com.pandaPass;

import com.pandaPass.persistence.DB;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.services.SessionManager;
import com.pandaPass.view.ScenesLoader;
import javafx.stage.Stage;

public class AppLifecycle {
    private static Stage primaryStage;

    public static void startApplication(Stage stage){
        primaryStage = stage;
        initializePrimaryStage();

        // init connection to database
        DB.connect();

        // Init instances
        ServiceLocator.init();

        // Preload ui scenes
        ServiceLocator.initializeScenesLoader(primaryStage);

        // Load login scene
        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.LOGIN);
    }

    private static void initializePrimaryStage(){
        primaryStage.setTitle("PandaPass");
        primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            shutdownApplication();
        });
    }

    public static void startUserSession(){
        // Start user session: open vault, start timeout service
        if(!ServiceLocator.getUserService().unlockVaultForUser()){
            System.err.println("Vault could not be opened. Returning to Login.");
            ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.LOGIN);
            return;
        }
        ServiceLocator.startSessionServices();
        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.VAULT);
    }

    public static void endUserSession(){
        // Lock vault
        lockUserVault();
        // End session-specific services
        ServiceLocator.endUserSessionServices();
        // Load login screen
        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.LOGIN);
    }

    public static void shutdownApplication(){
        // Lock vault
        lockUserVault();
        // Terminate app-services-instances
        ServiceLocator.shutdown();
        System.exit(0);
    }

    private static void lockUserVault(){
        if(SessionManager.isUserLoggedIn()){
            if(!ServiceLocator.getUserService().saveVaultForUser()){
                System.err.println("Saving the vault was not successful.");
            }
        }
    }
}
