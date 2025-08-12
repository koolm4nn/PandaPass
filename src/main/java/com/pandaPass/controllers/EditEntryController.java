package com.pandaPass.controllers;

import com.pandaPass.models.Category;
import com.pandaPass.models.Entry;
import com.pandaPass.models.PwnedCheckResult;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.services.SessionManager;
import com.pandaPass.utils.SessionTimerBinderUtil;
import com.pandaPass.viewModels.VaultViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EditEntryController {
    private VaultViewModel vaultViewModel;

    private Entry editedEntry;

    @FXML
    private VBox editEntryPane;

    @FXML private TextField
            editServiceInput, editUsernameInput;

    @FXML private TextArea editPasswordInput;
    @FXML private Label showPasswordLabel, editingErrorMessage, compromisedMessage;

    @FXML   private Button saveEditEntryButton, revertChangesButton, showPasswordButton;

    @FXML private ChoiceBox<Category> editCategoryInput;

    private final BooleanProperty fieldsEditedProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty formEnabledProperty = new SimpleBooleanProperty(true);

    private final String ENTRY_COMPROMISED_MESSAGE = "WARNING: The password of this entry has been found in at least one leak and is thus compromised.\nIt is STRONGLY recommended to change the password of this service!";

    @FXML
    private void initialize(){
        bindTimeoutReset();
        bindChangesDetection();
        addActionToButtons();
        setVisible(false);
    }

    public void setVaultViewModel(VaultViewModel vaultViewModel){
        this.vaultViewModel = vaultViewModel;
        editCategoryInput.setItems(vaultViewModel.getCategories());
    }

    public void setVisible(boolean isVisible){
        //editEntryPane.setManaged(isVisible);
        editEntryPane.setVisible(isVisible);
    }

    private void bindTimeoutReset(){
        SessionTimerBinderUtil.bind(editServiceInput);
        SessionTimerBinderUtil.bind(editUsernameInput);
        SessionTimerBinderUtil.bind(editPasswordInput);

        SessionTimerBinderUtil.bind(saveEditEntryButton);
        SessionTimerBinderUtil.bind(revertChangesButton);
    }

    private void bindChangesDetection(){
        fieldsEditedProperty.bind(Bindings.createBooleanBinding(
                this::fieldsAreEdited,
                editServiceInput.textProperty(),
                editUsernameInput.textProperty(),
                editPasswordInput.textProperty(),
                editCategoryInput.getSelectionModel().selectedItemProperty()
        ));

        // Disable buttons if not editing is detected or form is disabled
        saveEditEntryButton.disableProperty().bind(fieldsEditedProperty.not().or(formEnabledProperty.not()));
        revertChangesButton.disableProperty().bind(fieldsEditedProperty.not().or(formEnabledProperty.not()));

        // Disable inputs only if form is disabled
        editServiceInput.disableProperty().bind(formEnabledProperty.not());
        editUsernameInput.disableProperty().bind(formEnabledProperty.not());
        editPasswordInput.disableProperty().bind(formEnabledProperty.not());
        editCategoryInput.disableProperty().bind(formEnabledProperty.not());

        ChangeListener<String> listener = (_, _, _) -> {
            setShowEditingErrorMessage(false, "");
        };

        editServiceInput.textProperty().addListener(listener);
        editUsernameInput.textProperty().addListener(listener);
        editPasswordInput.textProperty().addListener(listener);
        editCategoryInput.selectionModelProperty().addListener(_ -> {
            setShowEditingErrorMessage(false, "");
        });
    }

    public void setEntry(Entry entry){
        editedEntry = entry;
        populateFieldsFromEntry();
    }

    private void populateFieldsFromEntry(){
        // Populating fields
        editServiceInput.setText(editedEntry.getService());
        editUsernameInput.setText(editedEntry.getUsername());
        editPasswordInput.setText(editedEntry.getDecryptedPassword());
        editCategoryInput.getSelectionModel().select(ServiceLocator.getCategoryService().getCategoryById(editedEntry.getCategoryId()));

        // Reset inputs
        showPasswordLabel.setVisible(false);
        showPasswordLabel.setManaged(false);
        editPasswordInput.setVisible(false);
        editPasswordInput.setManaged(false);
        showPasswordButton.setText("Show Password");

        // If entry is compromised, set message
        setShowCompromisedMessage(editedEntry.isCompromised(), ENTRY_COMPROMISED_MESSAGE);
        setShowEditingErrorMessage(false, "");
    }

    private boolean fieldsAreEdited(){
        if(editedEntry == null){
            return false;
        }
        Category selectedCategory = editCategoryInput.getSelectionModel().getSelectedItem();
        boolean categoryIsUnchanged = (selectedCategory != null && selectedCategory.getId() == editedEntry.getCategoryId()) ||
                (selectedCategory == null && editedEntry.getCategoryId() == -1);

        return !(editServiceInput.getText().equals(editedEntry.getService())
                && (editUsernameInput.getText().equals(editedEntry.getUsername()))
                && (editPasswordInput.getText().equals(editedEntry.getDecryptedPassword()))
                && (categoryIsUnchanged));
    }

    @FXML
    private void handleShowPassword(){
        boolean isShowing = editPasswordInput.isVisible();

         showPasswordLabel.setVisible(!isShowing);
         showPasswordLabel.setManaged(!isShowing);
         editPasswordInput.setManaged(!isShowing);
         editPasswordInput.setVisible(!isShowing);
         showPasswordButton.setText(isShowing? "Hide Password" : "Show Password");
    }

    private void addActionToButtons(){
        saveEditEntryButton.setOnAction(_ -> handleSaveEditEntry());
        revertChangesButton.setOnAction(_ -> handleRevertChanges());
    }

    @FXML
    private void handleSaveEditEntry(){
        setFormEnabled(false);
        Optional<Entry> potentialEntry = SessionManager.getVaultSession().getVault().getEntryByServiceName(editServiceInput.getText());
        if(potentialEntry.isPresent() && potentialEntry.get() != editedEntry){
            return;
        }

        // Before updating entry, check if given password has been pwned
        CompletableFuture<PwnedCheckResult> passwordIsPwnedFuture = ServiceLocator.getPwnedPasswordService().checkSinglePasswordAsync(editedEntry.getRuntimeId(), editPasswordInput.getText());

        passwordIsPwnedFuture.thenAccept(result -> {
            if(result.isPwned()){
                setShowCompromisedMessage(true, ENTRY_COMPROMISED_MESSAGE);
            } else {
                setShowCompromisedMessage(false, "");
                boolean updateWasSuccessful = SessionManager
                        .getVaultSession()
                        .getVault()
                        .updateEntry(
                                editedEntry,
                                editServiceInput.getText(),
                                editUsernameInput.getText(),
                                editPasswordInput.getText(),
                                editCategoryInput.getSelectionModel().getSelectedItem());
                if(updateWasSuccessful){
                    vaultViewModel.refreshEntries(editedEntry);
                    setFormEnabled(true);
                } else {
                    setShowEditingErrorMessage(true, "Error saving updates for entry.");
                }
            }
        });
    }

    @FXML
    private void handleRevertChanges(){
        populateFieldsFromEntry();
    }

    private void setShowEditingErrorMessage(boolean isShown, String message){
        editingErrorMessage.setManaged(isShown);
        editingErrorMessage.setVisible(isShown);
        editingErrorMessage.setText(message);
    }

    private void setShowCompromisedMessage(boolean isVisible, String message){
        compromisedMessage.setManaged(isVisible);
        compromisedMessage.setVisible(isVisible);
        compromisedMessage.setText(message);
    }

    private void setFormEnabled(boolean isEnabled){
        formEnabledProperty.set(isEnabled);
    }
}
