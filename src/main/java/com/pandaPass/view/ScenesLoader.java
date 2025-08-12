package com.pandaPass.view;

import com.pandaPass.controllers.MainController;
import com.pandaPass.services.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScenesLoader {
    public enum SceneKey {
        LOGIN,
        SIGN_UP,
        TOTP_CODE,
        VAULT,
        SETTINGS,
        MAIN
    }

    private final Stage primaryStage;

    private final Map<SceneKey, String> scenePaths = new HashMap<>();
    private final Map<SceneKey, Scene> loadedScenes = new HashMap<>();
    private final Map<SceneKey, FXMLLoader> loadersByKey = new HashMap<>();

    private final String mainSceneFxmlPath = "/fxml/mainView.fxml";
    private Parent mainViewRoot;
    private MainController mainViewController;

    public ScenesLoader(Stage primaryStage) throws RuntimeException{
        this.primaryStage = primaryStage;

        scenePaths.put(SceneKey.LOGIN, "/fxml/loginView.fxml");
        scenePaths.put(SceneKey.SIGN_UP, "/fxml/signUpView.fxml");
        scenePaths.put(SceneKey.TOTP_CODE, "/fxml/totpCodeView.fxml");
        scenePaths.put(SceneKey.VAULT, "/fxml/vaultView.fxml");
        scenePaths.put(SceneKey.SETTINGS, "/fxml/settingsView.fxml");

        // Preload login and sign up
        //loadSceneFromFxml(scenePaths.get(SceneKey.LOGIN), SceneKey.LOGIN);
        //loadSceneFromFxml(scenePaths.get(SceneKey.SIGN_UP), SceneKey.SIGN_UP);

        loadMainView();
    }

    private Scene loadSceneFromFxml(String fxmlPath, SceneKey key){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            // Add scene to theme manager on first load
            //ThemeManager.registerScene(scene);
            loadersByKey.put(key, loader);
            return scene;
        } catch (IOException e) {
            throw new RuntimeException("Could not load scene " + fxmlPath + ": " + e.getMessage());
        }
    }

    private void loadMainView(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(mainSceneFxmlPath));
            Parent root = loader.load();
            mainViewController = loader.getController();
            loadersByKey.put(SceneKey.MAIN, loader);

            Scene scene = new Scene(root);
            // Add scene to theme manager on first load
            ThemeManager.registerMainScene(scene);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Could not load main scene " + mainSceneFxmlPath + ": " + e.getMessage());
        }
    }

    private void loadViewIntoContent(SceneKey key){
        String fxmlPath = scenePaths.get(key);

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            if(!loadersByKey.containsKey(key)){
                loadersByKey.put(key, loader);
            }
            mainViewController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load view: " + key + ": " + e.getMessage() + " - ");
        }
    }

    private Scene getScene(SceneKey key) throws RuntimeException{
        // Load scene
        if(!loadedScenes.containsKey(key)){
            // Throw exception if path for scene key does not exist
            if(!scenePaths.containsKey(key)){
                throw new RuntimeException("Scene requested for key \"" + key + "\", but no path is provided.");
            }
            //loadedScenes.put(key, loadSceneFromFxml(scenePaths.get(key), key));
        }
        return loadedScenes.get(key);
    }

    public void loadScene(SceneKey key){
        loadViewIntoContent(key);
    }

    /*public void loadScene(SceneKey key){
        Scene scene = getScene(key);
        double width = primaryStage.getWidth();
        double height = primaryStage.getHeight();

        primaryStage.setScene(scene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.show();
    }*/

    public <T> T getController(SceneKey key){
        FXMLLoader loader = loadersByKey.get(key);
        if(loader == null){
            System.err.println("Controller for key " + key + " not found.");
            return null;
        }

        return loader.getController();
    }
}