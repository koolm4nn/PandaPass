package com.pandaPass.services;

import com.pandaPass.models.Category;
import com.pandaPass.repositories.CategoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final Map<Integer, Category> idToCategory;
    private List<Category> categories;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
        idToCategory = new HashMap<>();
        categories = new ArrayList<>();
        refreshCategories();
    }

    public boolean insertCategory(String title){
        if(categoryRepository.insertCategory(title)){
            refreshCategories();
            return true;
        } else {
            System.out.println("Category " + title + " was not inserted.");
            return false;
        }
    }

    public boolean updateCategory(String title, String newTitle){
        if(categoryRepository.updateCategory(title, newTitle)){
            refreshCategories();
            return true;
        } else {
            System.out.println("Category " + title + " was not updated to " + newTitle + " .");
            return false;
        }
    }

    public boolean deleteCategory(String title){
        if(categoryRepository.deleteCategoryByTitle(title)){
            refreshCategories();
            return true;
        } else {
            System.out.println("Category " + title + " was not deleted.");
            return false;
        }
    }

    public List<Category> getCategories(){
        return categories;
    }

    public void refreshCategories(){
        idToCategory.clear();
        categories = categoryRepository.findCategories();
        categories.forEach(
                category -> idToCategory.put(category.getId(), category));
    }

    public Category getCategoryById(int id){
        return idToCategory.getOrDefault(id, new Category("Unknown Category", -1));
    }
}
