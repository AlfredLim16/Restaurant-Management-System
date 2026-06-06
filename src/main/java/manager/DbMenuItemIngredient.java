package manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import user.DatabaseConnection;

public class DbMenuItemIngredient implements IMenuItemIngredient {

    @Override
    public void saveIngredients(int menuItemId, ArrayList<MenuItemIngredient> ingredients){
        String sql = "INSERT INTO menu_item_ingredients (menu_item_id, inventory_item_id, quantity_required) VALUES (?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                for(MenuItemIngredient ingredient : ingredients){
                    statement.setInt(1, menuItemId);
                    statement.setInt(2, ingredient.getInventoryItemId());
                    statement.setDouble(3, ingredient.getQuantityRequired());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to save ingredients for menu item ID " + menuItemId + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public ArrayList<MenuItemIngredient> getByMenuItemId(int menuItemId){
        ArrayList<MenuItemIngredient> ingredients = new ArrayList<>();
        String sql = "SELECT mii.menu_item_id, mii.inventory_item_id, mii.quantity_required, ii.name " +
                     "FROM menu_item_ingredients mii " +
                     "JOIN inventory_items ii ON mii.inventory_item_id = ii.inventory_item_id " +
                     "WHERE mii.menu_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, menuItemId);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        MenuItemIngredient ingredient = new MenuItemIngredient();
                        ingredient.setMenuItemId(rs.getInt("menu_item_id"));
                        ingredient.setInventoryItemId(rs.getInt("inventory_item_id"));
                        ingredient.setInventoryItemName(rs.getString("name"));
                        ingredient.setQuantityRequired(rs.getDouble("quantity_required"));
                        ingredients.add(ingredient);
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to get ingredients for menu item ID " + menuItemId + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return ingredients;
    }

    @Override
    public void deleteByMenuItemId(int menuItemId){
        String sql = "DELETE FROM menu_item_ingredients WHERE menu_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, menuItemId);
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to delete ingredients for menu item ID " + menuItemId + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }
}
