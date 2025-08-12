package com.pandaPass.controllers;

import com.pandaPass.services.ServiceLocator;
import com.pandaPass.utils.UserInterfaceUtil;
import com.pandaPass.view.ScenesLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    private TextField emailInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button loginButton, signUpButton;

    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private VBox loadingOverlay;

    @FXML
    private AnchorPane dummyFocusPane;

    @FXML
    private Label authenticationErrorMessage;

    @FXML
    private void initialize(){
        javafx.application.Platform.runLater(() -> dummyFocusPane.requestFocus());
        addListenersToInputs();

        UserInterfaceUtil.setButtonIconRight(loginButton, "/icons/enter.png");

        rootPane.setPrefHeight(Double.MAX_VALUE);
        rootPane.setPrefWidth(Double.MAX_VALUE);
    }

    public void clearInputs(){
        emailInput.clear();
        passwordInput.clear();
    }

    private void addListenersToInputs(){
        emailInput.textProperty().addListener((_, _, _) -> {
            if(authenticationErrorMessage.isManaged()){
                authenticationErrorMessage.setManaged(false);
                authenticationErrorMessage.setVisible(false);
            }
        });

        passwordInput.textProperty().addListener((_, _, _) -> {
            if(authenticationErrorMessage.isManaged()){
                authenticationErrorMessage.setManaged(false);
                authenticationErrorMessage.setVisible(false);
            }
        });
    }

    @FXML
    private void switchToSignUpScene(){
        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.SIGN_UP);
    }

    @FXML
    private void handleToSignUp(){
        switchToSignUpScene();
    }

    @FXML
    private void handleLogin(){
        loadingOverlay.setVisible(true);

        String mail = emailInput.getText();
        String password = passwordInput.getText();

        // Run login in background thread
        new Thread(() -> {
            boolean authenticated = ServiceLocator.getUserService().authenticateAndCreateSession(mail, password);

            javafx.application.Platform.runLater(() -> {
                if(authenticated && isMailValid()){
                    ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.TOTP_CODE);
                    //AppLifecycle.startUserSession(mail, password);
                    loadingOverlay.setVisible(false);
                    passwordInput.clear();
                    emailInput.clear();
                } else {
                    passwordInput.clear();
                    authenticationErrorMessage.setManaged(true);
                    authenticationErrorMessage.setVisible(true);

                    loadingOverlay.setVisible(false);
                }
            });
        }).start();
    }

    private boolean isMailValid(){
        // ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
        return emailInput.getText().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
}
