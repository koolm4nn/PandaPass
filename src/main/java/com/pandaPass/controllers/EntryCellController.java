package com.pandaPass.controllers;

import com.pandaPass.models.Entry;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.services.SessionManager;
import com.pandaPass.utils.ClipboardUtil;
import com.pandaPass.utils.SessionTimerBinderUtil;
import com.pandaPass.view.ScenesLoader;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class EntryCellController {
    @FXML
    private Label serviceLabel, categoryLabel;

    @FXML
    private Button deleteButton, copyButton;

    @FXML
    private Slider deleteSlider;

    @FXML
    private ImageView trashIcon;

    @FXML
    private HBox cellButtonsContainer, rootContainer;

    private boolean deleteIsPressed;

    private Entry entry;

    private final PseudoClass COMPROMISED_CLASS = PseudoClass.getPseudoClass("compromised");
    private final PseudoClass AWAITING_DELETION_CLASS = PseudoClass.getPseudoClass("awaiting-deletion");

    @FXML
    private void initialize(){
        initializeSlider();

        addActionsToButtons();

        setDeletionIcon();
    }

    private void setDeletionIcon(){
        // Load trash can icon
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/trash_can.png")));
        trashIcon.setImage(img);
    }

    private void initializeSlider(){
        deleteSlider.setVisible(false);
        deleteIsPressed = false;

        // Add listener to slider
        deleteSlider.valueProperty().addListener((_, _, newVal) -> {
            if(newVal.doubleValue() >= 100){
                boolean deletionWasSuccessful = SessionManager.getVaultSession().getVault().deleteEntryByName(entry.getService());
                if(deletionWasSuccessful){
                    VaultController controller = ServiceLocator.getScenesLoader().getController(ScenesLoader.SceneKey.VAULT);
                    controller.getVaultViewModel().deleteEntry(this.entry);
                } else {
                    System.err.println("Deleting entry " + entry.getService() + " was not successful.");
                }
            }
        });
    }

    private void addActionsToButtons(){
        //setupButton(editButton, this::handleEdit);
        setupButton(deleteButton, this::handleDelete);
        setupButton(copyButton, this::handleCopy);

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/clipboard.png")));
        ImageView view = new ImageView();
        view.setImage(img);
        copyButton.setGraphic(view);
    }

    public HBox getCellButtonsContainer() {
        return cellButtonsContainer;
    }

    public void setEntry(Entry entry){
        this.entry = entry;
        serviceLabel.setText(entry.getService());
        categoryLabel.setText(ServiceLocator.getCategoryService().getCategoryById(entry.getCategoryId()).getTitle());

        entry.getIsCompromisedProperty().addListener((obs, oldValue, newValue) -> {
            rootContainer.pseudoClassStateChanged(COMPROMISED_CLASS, newValue);
        });

        rootContainer.pseudoClassStateChanged(COMPROMISED_CLASS, entry.isCompromised());
    }

    @FXML
    private void handleDelete(){
        deleteIsPressed = !deleteIsPressed;
        deleteSlider.setVisible(deleteIsPressed);
        deleteSlider.setValue(0);
        deleteButton.setText(deleteIsPressed? "Keep" : "Delete");
        deleteButton.pseudoClassStateChanged(AWAITING_DELETION_CLASS, deleteIsPressed);
        //deleteButton.setStyle("-fx-background-color: " + (deleteIsPressed? "green" : "red") + ";");
        //deleteButton.setStyle("-fx-text-fill: " + (deleteIsPressed? "white" : "red") + ";");
        trashIcon.setVisible(deleteIsPressed);
    }

    @FXML
    private void handleCopy(){
        ClipboardUtil.copyContentToClipboardWithTimeout(entry.getDecryptedPassword(), 5);
    }

    private void setupButton(Button button, Runnable action) {
        SessionTimerBinderUtil.bind(button);
        button.setOnAction(_ -> action.run());
    }
}
