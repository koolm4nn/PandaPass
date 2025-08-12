package com.pandaPass.viewModels;

import com.pandaPass.models.Category;
import com.pandaPass.models.Entry;
import com.pandaPass.services.CategoryService;
import com.pandaPass.services.VaultSession;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VaultViewModel {
    private final VaultSession vaultSession;
    private final CategoryService categoryService;

    private final ObservableList<Entry> observedEntries = FXCollections.observableArrayList();
    private final FilteredList<Entry> filteredEntries = new FilteredList<>(observedEntries, _ -> true);
    private final ObservableList<Category> observedCategories = FXCollections.observableArrayList();

    private final StringProperty currentSearchTextProperty = new SimpleStringProperty("");
    private final ObjectProperty<Category> currentSelectedCategoryProperty = new SimpleObjectProperty<>(new Category("", -1));
    private final ObjectProperty<Entry> currentSelectedEntryProperty = new SimpleObjectProperty<>();

    public VaultViewModel(VaultSession vaultSession, CategoryService categoryService){
        this.vaultSession = vaultSession;
        this.categoryService = categoryService;

        refreshCategories();
        refreshEntries();

        currentSearchTextProperty.addListener((_, _, _) -> filterEntries());
        currentSelectedCategoryProperty.addListener((_, _, _) -> filterEntries());
    }

    public void refreshEntries(){
        Entry e = currentSelectedEntryProperty.get();
        List<Entry> vaultEntries = vaultSession.getVault().getEntries();
        observedEntries.setAll(vaultEntries);
        currentSelectedEntryProperty.set(e);
    }

    public void refreshEntries(Entry e){
        List<Entry> vaultEntries = vaultSession.getVault().getEntries();
        observedEntries.setAll(vaultEntries);
        currentSelectedEntryProperty.set(e);
    }

    public void selectEntry(Entry entry){
        currentSelectedEntryProperty.set(entry);
    }

    public void refreshCategories(){
        List<Category> categories = categoryService.getCategories();
        observedCategories.setAll(categories);
    }

    public ObservableList<Entry> getObservedEntries(){
        return FXCollections.unmodifiableObservableList(filteredEntries);
    }

    public void deleteEntry(Entry entry){
        observedEntries.removeIf(entry::equals);
    }

    /**
     * Get an unmodifiable copy of the categories available.
     * @return Unmodifiable List of categories
     */
    public ObservableList<Category> getCategories(){
        return FXCollections.unmodifiableObservableList(observedCategories);
    }

    private void filterEntries(){
        filteredEntries.setPredicate(entry -> {
            boolean matchesSearch = currentSearchTextProperty.get().isEmpty() || entry.getService().toLowerCase().contains(currentSearchTextProperty.get().toLowerCase());
            boolean matchesCategory = currentSelectedCategoryProperty.get().getId() == -1 || entry.getCategoryId() == currentSelectedCategoryProperty.get().getId();

            return matchesSearch && matchesCategory;
        });
    }

    public StringProperty getCurrentSearchTextProperty(){
        return currentSearchTextProperty;
    }

    public ObjectProperty<Category> getCurrentSelectedCategoryProperty(){
        return currentSelectedCategoryProperty;
    }

    public ObjectProperty<Entry> getCurrentSelectedEntryProperty(){
        return currentSelectedEntryProperty;
    }

    public Optional<Entry> getEntryForRuntimeId(String runtimeId){
        return observedEntries.stream().filter(entry -> Objects.equals(entry.getRuntimeId(), runtimeId)).findFirst();
    }
}
