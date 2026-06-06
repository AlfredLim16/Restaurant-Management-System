package cashier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import user.DatabaseConnection;

public class DbMenuItem implements IMenuItem {

    private MenuItem toMenuItem(ResultSet rs) throws SQLException{
        MenuItem item = new MenuItem();
        item.setMenuItemId(rs.getInt("menu_item_id"));
        item.setMenuItemName(rs.getString("name"));
        item.setMenuItemPrice(rs.getDouble("price"));
        item.setMenuItemCategory(rs.getString("category"));
        item.setAvailable(rs.getBoolean("is_available"));
        return item;
    }

    @Override
    public void create(MenuItem item){
        String sql = "INSERT INTO menu_items (name, price, category, is_available) VALUES (?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, item.getMenuItemName());
                statement.setDouble(2, item.getMenuItemPrice());
                statement.setString(3, item.getMenuItemCategory());
                statement.setBoolean(4, item.isAvailable());
                statement.executeUpdate();
                try(ResultSet keys = statement.getGeneratedKeys()){
                    if(keys.next()){
                        item.setMenuItemId(keys.getInt(1));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to create MenuItem: " + item.getMenuItemName() + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public MenuItem get(int itemId){
        String sql = "SELECT * FROM menu_items WHERE menu_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, itemId);
                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()){
                        return toMenuItem(rs);
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving MenuItem with ID " + itemId + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return null;
    }

    @Override
    public ArrayList<MenuItem> getAll(){
        ArrayList<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        items.add(toMenuItem(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving all MenuItems. Details: " + sqlException.getMessage(), sqlException);
        }
        return items;
    }

    @Override
    public void update(MenuItem item){
        String sql = "UPDATE menu_items SET name = ?, price = ?, category = ?, is_available = ? WHERE menu_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, item.getMenuItemName());
                statement.setDouble(2, item.getMenuItemPrice());
                statement.setString(3, item.getMenuItemCategory());
                statement.setBoolean(4, item.isAvailable());
                statement.setInt(5, item.getMenuItemId());
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving all MenuItems. Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public void delete(int itemId){
        String sql = "DELETE FROM menu_items WHERE menu_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, itemId);
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to delete MenuItem with ID " + itemId + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public ArrayList<MenuItem> findAvailable(){
        ArrayList<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE is_available = TRUE";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        items.add(toMenuItem(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving available MenuItems. Details: " + sqlException.getMessage(), sqlException);
        }
        return items;
    }

    @Override
    public ArrayList<MenuItem> findByCategory(String category){
        ArrayList<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE category = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, category);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        items.add(toMenuItem(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving MenuItems in category '" + category + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return items;
    }
}
