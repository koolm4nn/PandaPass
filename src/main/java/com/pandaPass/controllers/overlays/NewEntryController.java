package com.pandaPass.controllers.overlays;

import com.pandaPass.models.Category;
import com.pandaPass.models.Entry;
import com.pandaPass.models.PwnedCheckResult;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.services.SessionManager;
import com.pandaPass.utils.PasswordGenerationUtil;
import com.pandaPass.utils.SessionTimerBinderUtil;
import com.pandaPass.viewModels.VaultViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NewEntryController {
    @FXML
    private VBox newEntryPane;

    @FXML private TextField
            serviceInput, usernameInput;

    @FXML private PasswordField passwordInput;

    @FXML private Button
            saveNewEntryButton, cancelNewEntryButton;

    @FXML private Label serviceNameCollisionMessage;
    @FXML private ChoiceBox<Category> categoryInput;

    private VaultViewModel vaultViewModel;

    @FXML
    private void initialize(){
        bindTimeoutReset();
        addActionToButtons();
    }

    public void setVaultViewModel(VaultViewModel vaultViewModel){
        this.vaultViewModel = vaultViewModel;
        categoryInput.setItems(vaultViewModel.getCategories());
    }

    public void setVisible(boolean isVisible){
        resetForm();
        newEntryPane.setVisible(isVisible);
    }

    private void bindTimeoutReset(){
        SessionTimerBinderUtil.bind(serviceInput);
        SessionTimerBinderUtil.bind(usernameInput);
        SessionTimerBinderUtil.bind(passwordInput);
        SessionTimerBinderUtil.bind(saveNewEntryButton);
        SessionTimerBinderUtil.bind(cancelNewEntryButton);
    }

    public void resetForm(){
        serviceInput.clear();
        usernameInput.clear();
        passwordInput.clear();
        setErrorMessage("", false);
        setCategoryInputToDefault();
    }

    public void setErrorMessage(String message, boolean isVisible){
        serviceNameCollisionMessage.setText(message);
        serviceNameCollisionMessage.setVisible(isVisible);
        serviceNameCollisionMessage.setManaged(isVisible);
    }

    private void setCategoryInputToDefault(){
        Optional<Category> defaultCategory = vaultViewModel
                .getCategories()
                .stream()
                .filter(category -> category.getTitle().equals("Default"))
                .findFirst();
        defaultCategory.ifPresent(category -> categoryInput.getSelectionModel().select(category));
    }

    @FXML
    private void handleGeneratingPassword(){
        String newPassword = PasswordGenerationUtil.generatePassword(64);

        passwordInput.setText(newPassword);
    }

    private void addActionToButtons(){
        saveNewEntryButton.setOnAction(_ -> handleSavingNewEntry());
        cancelNewEntryButton.setOnAction(_ -> handleCancelNewEntry());
    }

    @FXML
    private void handleSavingNewEntry(){
        setInputsEnabled(false);
        String service = serviceInput.getText();
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        Category category = categoryInput.getSelectionModel().getSelectedItem();

        if(service.isBlank() || username.isBlank() || password.isBlank()){
            setErrorMessage("Invalid inputs: Inputs are missing.", true);
            setInputsEnabled(true);
            return;
        }

        // Check if password is pwned
        CompletableFuture<PwnedCheckResult> pwnedFuture = ServiceLocator.getPwnedPasswordService().checkSinglePasswordAsync(service, password);
        pwnedFuture.thenAccept(result -> {
            Platform.runLater(() -> {
                if(result.isPwned()){
                    setErrorMessage("Password is pwned for " + result.getPwnCount() + " times. Please change the password.", true);
                    setInputsEnabled(true);
                } else {

                    Optional<Entry> potentialEntry = SessionManager.getVaultSession().getVault().getEntryByServiceName(service);

                    if(potentialEntry.isPresent()){
                        setErrorMessage("Entry with service \"" + service + "\" already exists.", true);
                        setInputsEnabled(true);
                        return;
                    }

                    Entry addedEntry = SessionManager.getVaultSession().getVault().add(service, username, password, category);

                    if(addedEntry != null){
                        vaultViewModel.refreshEntries();
                    } else {
                        System.out.println("New entry could not be added.");
                        setInputsEnabled(true);
                        return;
                    }

                    setVisible(false);
                    setInputsEnabled(true);
                }
            });
        });
    }

    @FXML
    private void handleCancelNewEntry(){
        setVisible(false);
    }

    private void setInputsEnabled(boolean isEnabled){
        passwordInput.setDisable(!isEnabled);
        serviceInput.setDisable(!isEnabled);
        usernameInput.setDisable(!isEnabled);
        cancelNewEntryButton.setDisable(!isEnabled);
        saveNewEntryButton.setDisable(!isEnabled);
    }
}
