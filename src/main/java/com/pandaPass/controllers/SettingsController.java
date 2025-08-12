package com.pandaPass.controllers;

import com.pandaPass.services.ServiceLocator;
import com.pandaPass.view.ScenesLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettingsController {
    @FXML
    private Button exitSettingsButton;

    @FXML
    private void initialize(){
    }

    @FXML
    private void handleExitSettingsButton(){
        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.VAULT);
    }
}
