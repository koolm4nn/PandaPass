package com.pandaPass.controllers;

import com.pandaPass.AppLifecycle;
import com.pandaPass.controllers.overlays.NewEntryController;
import com.pandaPass.models.Category;
import com.pandaPass.models.EntryListCell;
import com.pandaPass.services.ServiceLocator;
import com.pandaPass.models.Entry;
import com.pandaPass.services.SessionManager;
import com.pandaPass.utils.SessionTimerBinderUtil;
import com.pandaPass.utils.UserInterfaceUtil;
import com.pandaPass.view.ScenesLoader;
import com.pandaPass.viewModels.VaultViewModel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class VaultController {
    private VaultViewModel vaultViewModel;

    private NewEntryController newEntryOverlayController;

    @FXML
    private EditEntryController editEntryController;

    @FXML private StackPane overLayContainer, entryPane;
    @FXML private TextField searchInput;
    @FXML private ListView<Entry> entriesListView;
    @FXML private Button
            addEntryButton,
            exitVaultButton,
            clearSearchButton,
            settingsButton,
            resetCategorySelectionButton;

    @FXML private VBox categoryToggleContainer;
    private final ToggleGroup categoryToggleGroup = new ToggleGroup();

    private final String EDIT_ENTRY_FXML_PATH = "/fxml/editEntryView.fxml";
    private final String NEW_ENTRY_FXML_PATH = "/fxml/newEntryView.fxml";
    private final String ICON_SETTINGS_PATH = "/icons/settings.png";
    private final String ICON_EXIT_PATH = "/icons/exit.png";

    @FXML
    private void initialize(){
        vaultViewModel = new VaultViewModel(SessionManager.getVaultSession(), ServiceLocator.getCategoryService());
        entriesListView.setItems(vaultViewModel.getObservedEntries());

        Map<String, String> identifierToPassword = new HashMap<>();
        vaultViewModel.getObservedEntries().forEach(entry -> {
            identifierToPassword.put(entry.getRuntimeId(), entry.getDecryptedPassword());
        });

        // Check if passwords are pwned
        ServiceLocator.getPwnedPasswordService().checkPasswordsAsync(identifierToPassword).thenAccept(results -> {
            results.forEach(result -> {
                if(result.isPwned()){
                    // Update entry if compromised
                    vaultViewModel.getEntryForRuntimeId(result.getPasswordIdentifier())
                            .ifPresent(entry -> entry.setIsCompromised(true));
                }
            });
        });


        setCategoryToggleGroup();
        resetCategorySelectionButton.setDisable(true);

        MainController m = ServiceLocator.getScenesLoader().getController(ScenesLoader.SceneKey.MAIN);
        overLayContainer = m.getOverlayContainer();

        //preloadOverlays();
        preloadEntryViews();

        bindTimeoutReset();
        addListenerToInputs();
        addActionToButtons();
        configureIcons();
        setCellFactory();

        entriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldEntry, newEntry) -> {
            if(newEntry != null){
                editEntryController.setEntry(newEntry);
                showEditEntryForm();
            }
        });
    }

    @FXML
    private void handleResetCategorySelection(){
        resetCategorySelectionButton.setDisable(true);
        if(categoryToggleGroup.getSelectedToggle() != null){
            categoryToggleGroup.getSelectedToggle().setSelected(false);
        }
        vaultViewModel.getCurrentSelectedCategoryProperty().set(Category.EMPTY_CATEGORY);
        //vaultViewModel.setSearchCategory(Category.EMPTY_CATEGORY);
    }

    private void setCategoryToggleGroup(){
        List<Category> categoryList = vaultViewModel.getCategories();

        for(Category cat : categoryList){
            ToggleButton toggleButton = new ToggleButton(cat.getTitle());
            toggleButton.setToggleGroup(categoryToggleGroup);
            toggleButton.setUserData(cat);
            categoryToggleContainer.getChildren().add(toggleButton);
        }

        categoryToggleGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
            if(newToggle != null){
                Category selectedCategory = (Category) ((ToggleButton) newToggle).getUserData();
                //vaultViewModel.setSearchCategory(selectedCategory);
                resetCategorySelectionButton.setDisable(false);
            }
        });
    }

    private void showNewEntryForm(){
        entryPane.getChildren().forEach(child -> child.setVisible(false));
        entriesListView.getSelectionModel().select(null);
        newEntryOverlayController.setVisible(true);
    }

    public void showEditEntryForm(){
        entryPane.getChildren().forEach(child -> child.setVisible(false));
        editEntryController.setVisible(true);
    }

    private void preloadEntryViews(){
        try{
            FXMLLoader editEntryLoader = new FXMLLoader(getClass().getResource(EDIT_ENTRY_FXML_PATH));
            VBox editEntryView = editEntryLoader.load();

            editEntryController = editEntryLoader.getController();
            editEntryController.setVaultViewModel(vaultViewModel);
            editEntryView.setVisible(false);

            FXMLLoader newEntryLoader = new FXMLLoader(getClass().getResource(NEW_ENTRY_FXML_PATH));
            VBox newEntryView = newEntryLoader.load();

            newEntryOverlayController = newEntryLoader.getController();
            newEntryOverlayController.setVaultViewModel(vaultViewModel);
            newEntryView.setVisible(false);

            entryPane.getChildren().setAll(editEntryView, newEntryView);
        } catch (IOException e) {
            System.out.println("Could not preload entry view: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private void preloadOverlays(){
        try{
            FXMLLoader newEntryLoader = new FXMLLoader(getClass().getResource(NEW_ENTRY_FXML_PATH));
            StackPane newEntryPane = newEntryLoader.load();

            newEntryPane.prefWidthProperty().bind(overLayContainer.widthProperty());
            newEntryPane.prefHeightProperty().bind(overLayContainer.heightProperty());

            newEntryOverlayController = newEntryLoader.getController();
            newEntryOverlayController.setVaultViewModel(vaultViewModel);
            //newEntryOverlayController.setOverlayContainer(overLayContainer);

            // Add to container
            newEntryPane.setManaged(true);
            newEntryPane.setVisible(false);
            overLayContainer.getChildren().addAll(newEntryPane);
        } catch (Exception e) {
            System.err.println("Error loading overlays: " + e.getMessage());
        }
    }

    private void bindTimeoutReset(){
        SessionTimerBinderUtil.bind(searchInput);
        SessionTimerBinderUtil.bind(addEntryButton);
        SessionTimerBinderUtil.bind(clearSearchButton);
        SessionTimerBinderUtil.bind(exitVaultButton);
        SessionTimerBinderUtil.bind(settingsButton);
        SessionTimerBinderUtil.bind(resetCategorySelectionButton);
    }

    private void addListenerToInputs(){
        searchInput.textProperty().bindBidirectional(vaultViewModel.getCurrentSearchTextProperty());
        searchInput.textProperty().addListener((_, _, newText) -> {
            clearSearchButton.setDisable(newText.isEmpty());
            //vaultViewModel.setSearchFilter(newText);
        });

        clearSearchButton.disableProperty().bind(vaultViewModel.getCurrentSearchTextProperty().isEmpty());
    }

    private void addActionToButtons(){
        addEntryButton.setOnAction(_ -> handleAddEntry());
        clearSearchButton.setOnAction(_ -> handleClearSearch());
        exitVaultButton.setOnAction(_ -> handleLogout());
        settingsButton.setOnAction(_ -> handleOpenSettings());
        resetCategorySelectionButton.setOnAction(_ -> handleResetCategorySelection());
    }

    private void setCellFactory(){
        entriesListView.setCellFactory(_ -> new EntryListCell(entriesListView));
    }

    private void configureIcons(){
        // Icons
        UserInterfaceUtil.setButtonIconRight(settingsButton, ICON_SETTINGS_PATH);
        UserInterfaceUtil.setButtonIconLeft(exitVaultButton, ICON_EXIT_PATH);
    }

    @FXML
    private void handleOpenSettings(){
        ServiceLocator.getScenesLoader().loadScene(ScenesLoader.SceneKey.SETTINGS);
    }

    private void handleAddEntry(){
        showNewEntryForm();
    }

    @FXML
    private void handleLogout(){
        AppLifecycle.endUserSession();
    }

    @FXML
    private void handleClearSearch(){
        searchInput.clear();
    }

    public VaultViewModel getVaultViewModel(){
        return vaultViewModel;
    }
}
