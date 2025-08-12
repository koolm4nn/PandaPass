package com.pandaPass.controllers;

import com.pandaPass.services.ThemeManager;
import com.pandaPass.utils.UserInterfaceUtil;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    private StackPane content, overlayContainer;

    @FXML
    private VBox loadingOverlay;

    @FXML
    private Button themeButton, aboutButton;
    @FXML
    private void initialize(){
        UserInterfaceUtil.setButtonIconRight(themeButton, ThemeManager.getDarkIconPath());

        overlayContainer.setManaged(false);
        overlayContainer.setVisible(false);
    }

    @FXML
    private void handleAbout(){

    }

    public void setContent(Parent view){
        content.getChildren().setAll(view);
    }

    public MainController getInstance(){
        return this;
    }

    public StackPane getOverlayContainer(){
        return overlayContainer;
    }

    @FXML
    private void handleSwitchTheme(){
        if(ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK){
            UserInterfaceUtil.setButtonIconCenterWithoutText(themeButton, ThemeManager.getDarkIconPath());
            ThemeManager.switchTheme(ThemeManager.Theme.LIGHT);
        } else {
            UserInterfaceUtil.setButtonIconCenterWithoutText(themeButton, ThemeManager.getLightIconPath());
            ThemeManager.switchTheme(ThemeManager.Theme.DARK);
        }
    }
}
