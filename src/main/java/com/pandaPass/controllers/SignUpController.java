package com.pandaPass.controllers;

import com.pandaPass.controllers.overlays.QRCodeOverlayController;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.utils.EncodingUtil;
import com.pandaPass.utils.TotpUtil;
import com.pandaPass.view.ScenesLoader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField mailInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private Text errorMessage;
    @FXML
    private Button signUpButton, toLoginButton;

    private static final String USER_EXISTS_MSG = "Invalid Credentials";
    private static final String SIGN_UP_FAILED_MSG = "An error occurred while signing up.";
    private static final String SIGN_UP_INTERRUPTED_MSG = "Signing up was interrupted.";

    private byte[] totpSecret;

    @FXML
    private void initialize(){
        addListenerToInputs();
    }

    private void addListenerToInputs(){
        mailInput.textProperty().addListener((_, _, _) -> {
            hideErrorMessage();
            setErrorMessage("");
        });

        passwordInput.textProperty().addListener((_, _, _) -> {
            hideErrorMessage();
            setErrorMessage("");
        });
    }

    @FXML
    private void handleSignUp(){
        disableButtons();

        totpSecret = TotpUtil.generateTotpSecret();
        String totpSecretBase64 = EncodingUtil.encodeByteArrayToBase64(totpSecret);

        Task<Boolean> signUpTask = new Task<>() {
            @Override
            protected Boolean call() {
                return ServiceLocator
                        .getUserService()
                        .signUpUser(
                                mailInput.getText(),
                                passwordInput.getText(),
                                totpSecretBase64
                        );
            }
        };

        signUpTask.setOnSucceeded(_ -> {
            boolean isUserSignedUp = signUpTask.getValue();
            if(!isUserSignedUp){
                setErrorMessage(USER_EXISTS_MSG);
                showErrorMessage();
            } else {
                // Generate totp and show qr code
                Image qrCode = TotpUtil.generateQrCode(mailInput.getText(), "PandaPass", totpSecret);

                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/overlays/qrCodeOverlay.fxml"));
                    StackPane qrCodeView = loader.load();
                    QRCodeOverlayController controller = loader.getController();
                    controller.setQrCodeImage(qrCode);
                    controller.setVisible(true);

                    MainController m = ServiceLocator.getScenesLoader().getController(ScenesLoader.SceneKey.MAIN);
                    m.getOverlayContainer().getChildren().setAll(qrCodeView);
                    m.getOverlayContainer().setManaged(true);
                    m.getOverlayContainer().setVisible(true);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //toLoginScene();
                //resetInputs();
            }
            enableButtons();
        });

        signUpTask.setOnCancelled(_ -> {
            setErrorMessage(SIGN_UP_INTERRUPTED_MSG);
            showErrorMessage();
            enableButtons();
        });

        signUpTask.setOnFailed(_ -> {
            setErrorMessage(SIGN_UP_FAILED_MSG);
            showErrorMessage();
            enableButtons();
        });

        new Thread(signUpTask).start();
    }

    @FXML
    private void handleBackToLogin(){
        toLoginScene();
    }

    private void toLoginScene(){
        LoginController controller = ServiceLocator.getScenesLoader().getController(ScenesLoader.SceneKey.LOGIN);
        controller.clearInputs();
        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.LOGIN);
    }

    private void setErrorMessage(String message){
        errorMessage.setText(message);
    }

    private void showErrorMessage(){
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
    }

    private void hideErrorMessage(){
        errorMessage.setVisible(false);
        errorMessage.setManaged(false);
    }

    private void enableButtons(){
        signUpButton.setDisable(false);
        toLoginButton.setDisable(false);
    }

    private void disableButtons(){
        signUpButton.setDisable(true);
        toLoginButton.setDisable(true);
    }

    public void resetInputs(){
        mailInput.clear();
        passwordInput.clear();
    }
}
