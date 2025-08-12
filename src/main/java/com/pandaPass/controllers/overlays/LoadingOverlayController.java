package com.pandaPass.controllers.overlays;

import com.pandaPass.controllers.base.OverlayControllerBase;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class LoadingOverlayController extends OverlayControllerBase {
    @FXML
    private StackPane loadingOverlay;

    @Override
    public void showOverlay(){
        super.showOverlay();
        loadingOverlay.setVisible(true);
    }
}
