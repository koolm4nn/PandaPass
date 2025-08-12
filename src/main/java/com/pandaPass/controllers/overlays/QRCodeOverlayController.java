package com.pandaPass.controllers.overlays;

import com.pandaPass.controllers.MainController;
import com.pandaPass.controllers.SignUpController;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.view.ScenesLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class QRCodeOverlayController {
    @FXML
    private Button closeOverlayButton;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private StackPane qrCodePane;

    @FXML
    private void initialize(){

    }

    public void setQrCodeImage(Image qrCode){
        qrCodeImageView.setImage(qrCode);
    }

    public void setVisible(boolean isVisible){

    }

    @FXML
    private void handleExit(){
        MainController m = ServiceLocator.getScenesLoader().getController(ScenesLoader.SceneKey.MAIN);
        m.getOverlayContainer().getChildren().clear();
        m.getOverlayContainer().setManaged(false);
        m.getOverlayContainer().setVisible(false);

        // TODO: Clear sign up inputs
        SignUpController s = ServiceLocator.getScenesLoader().getController(ScenesLoader.SceneKey.SIGN_UP);
        s.resetInputs();

        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.LOGIN);
    }
}
