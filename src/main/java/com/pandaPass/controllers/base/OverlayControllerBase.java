package com.pandaPass.controllers.base;

import com.pandaPass.viewModels.VaultViewModel;
import javafx.scene.layout.StackPane;

public abstract class OverlayControllerBase {
    protected VaultViewModel vaultViewModel;
    protected StackPane overlayContainer;

    public void setVaultViewModel(VaultViewModel vaultViewModel){
        this.vaultViewModel = vaultViewModel;
    }

    public void setOverlayContainer(StackPane overlayContainer){
        this.overlayContainer = overlayContainer;
    }

    public void showOverlay(){
        enableOverlayContainer();
    }

    public void hideOverlay(){
        overlayContainer.getChildren().forEach(child -> child.setVisible(false));
        disableOverlayContainer();
    }

    private void enableOverlayContainer(){
        overlayContainer.setVisible(true);
        overlayContainer.setManaged(true);
    }

    private void disableOverlayContainer(){
        overlayContainer.setVisible(false);
        overlayContainer.setManaged(false);
    }
}
