package com.pandaPass.repositories;

import com.pandaPass.models.Category;
import com.pandaPass.persistence.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

// Categories are used as per-entry mapping to categories
public class CategoryRepository {
    // Get list of categories
    public List<Category> findCategories(){
        String query = "select * from panda_pass.categories";

        ResultSet result;
        List<Category> categories = new ArrayList<>();
        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            result = stmt.executeQuery();

            while(result.next()){
                categories.add(new Category(result.getString("title"), result.getInt("id")));
            }
        } catch (Exception e){
            System.err.println("Error while querying categories: " + e.getMessage());
        }

        return categories;
    }


    // add to categories
    public boolean insertCategory(String category){
        String query = "insert into panda_pass.categories(title) values (?)";

        int rowsAffected;
        try(
                Connection conn = DB.connect();
                PreparedStatement stmt = conn.prepareStatement(query)
        ){
            stmt.setString(1, category);

            rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0){
                System.err.println("Error inserting category: No category was added.");

            } else if(rowsAffected > 1){
                System.err.println("Error inserting category: More than one category was added.");

            } else {
                System.out.println("Category was successful added.");
            }

            return rowsAffected == 1;
        } catch (Exception e) {
            System.err.println("Error while inserting new category: " + e.getMessage());
            return false;
        }
    }

    // Remove from categories
    public boolean deleteCategoryByTitle(String title){
        String query = "delete from panda_pass.categories where title = ?";

        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1,title);

            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0){
                System.err.println("Deleting category: update did not affect any row.");
            } else if(rowsAffected > 1){
                System.err.println("Deleting category: more than one row was affected.");
            } else {
                System.out.println("Deleting category was successful.");
            }

            return rowsAffected == 1;

        } catch (Exception e) {
            System.err.println("Error while deleting category: " + e.getMessage());
            return false;
        }
    }

    // Update a category
    public boolean updateCategory(String category, String newTitle){
        String query = "update panda_pass.categories set title = ? where title = ?";

        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1, newTitle);
            stmt.setString(2, category);

            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0){
                System.err.println("Updating category: update did not affect any row.");
            } else if(rowsAffected > 1){
                System.err.println("Updating category: more than one row was affected.");
            } else {
                System.out.println("Updating category was successful.");
            }

            return rowsAffected == 1;
        } catch (Exception e) {
            System.err.println("Error while updating category: " + e.getMessage());
            return false;
        }
    }

    public boolean findCategoryByTitle(String titleToFind){
        String query = "SELECT * FROM panda_pass.categories WHERE title = ?";

        ResultSet result;
        try(
                Connection connection = DB.connect();
                PreparedStatement stmt = connection.prepareStatement(query)
        ){
            stmt.setString(1, titleToFind);
            result = stmt.executeQuery();

            return result.next();
        } catch (Exception e){
            System.err.println("Error while querying for category \"" + titleToFind + "\": " + e.getMessage());
        }

        return false;
    }


}
