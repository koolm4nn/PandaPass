package com.pandaPass.models;

import com.pandaPass.controllers.EntryCellController;
import com.pandaPass.controllers.VaultController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class EntryListCell extends ListCell<Entry> {
        private HBox cellRoot;
        private EntryCellController controller;

        public EntryListCell(ListView<Entry> entriesListView){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/entryCell.fxml"));
                cellRoot = loader.load();
                controller = loader.getController();
                //controller.setListview(entriesListView);

                itemProperty().addListener((_, _, _) -> updateButtonVisibility());
                selectedProperty().addListener((_, _, _) -> updateButtonVisibility());
            } catch (IOException e){
                System.err.println("Error in Cellfactory: " + e.getMessage());
            }
        }

    @Override
    protected void updateItem(Entry entry, boolean empty){
        super.updateItem(entry, empty);

        if(empty || entry == null){
            setGraphic(null);
        } else {
            controller.setEntry(entry);
            setGraphic(cellRoot);

            // Toggle button visibility
            updateButtonVisibility();
        }
    }

    private void updateButtonVisibility(){
        boolean isSelected = isSelected();
        controller.getCellButtonsContainer().setVisible(isSelected);
        controller.getCellButtonsContainer().setManaged(isSelected);
    }
}
