package manager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import user.DatabaseConnection;

public class DbInventoryItem implements IInventoryItem {

    private InventoryItem toInventoryItem(ResultSet rs) throws SQLException{
        InventoryItem item = new InventoryItem();
        item.setInventoryItemId(rs.getInt("inventory_item_id"));
        item.setInventoryItemName(rs.getString("name"));
        item.setInventoryCategory(rs.getString("category"));
        item.setInventoryQuantity(rs.getInt("quantity"));
        item.setInventoryUnit(rs.getString("unit"));
        item.setInventoryCostPerUnit(rs.getDouble("cost_per_unit"));
        item.setInventoryReorderLevel(rs.getInt("reorder_level"));
        item.setInventorySupplier(rs.getString("supplier"));
        Date restocked = rs.getDate("last_restocked_date");
        if(restocked != null){
            item.setInventoryLastRestockedDate(restocked.toLocalDate());
        }
        Date expiry = rs.getDate("expiry_date");
        if(expiry != null){
            item.setInventoryExpiryDate(expiry.toLocalDate());
        }
        return item;
    }

    @Override
    public void create(InventoryItem item){
        String sql = "INSERT INTO inventory_items (name, category, quantity, unit, cost_per_unit, reorder_level, supplier, last_restocked_date, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, item.getInventoryItemName());
                statement.setString(2, item.getInventoryCategory());
                statement.setInt(3, item.getInventoryQuantity());
                statement.setString(4, item.getInventoryUnit());
                statement.setDouble(5, item.getInventoryCostPerUnit());
                statement.setInt(6, item.getInventoryReorderLevel());
                statement.setString(7, item.getInventorySupplier());
                statement.setDate(8, item.getInventoryLastRestockedDate() != null ? Date.valueOf(item.getInventoryLastRestockedDate()) : null);
                statement.setDate(9, item.getInventoryExpiryDate() != null ? Date.valueOf(item.getInventoryExpiryDate()) : null);
                statement.executeUpdate();

                try(ResultSet keys = statement.getGeneratedKeys()){
                    if(keys.next()){
                        item.setInventoryItemId(keys.getInt(1));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to create InventoryItem '" + item.getInventoryItemName() + "'. Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public InventoryItem get(int inventoryId){
        String sql = "SELECT * FROM inventory_items WHERE inventory_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, inventoryId);
                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()){
                        return toInventoryItem(rs);
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving InventoryItem with ID " + inventoryId + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return null;
    }

    @Override
    public ArrayList<InventoryItem> getAll(){
        ArrayList<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_items";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        items.add(toInventoryItem(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving all InventoryItems. Details: " + sqlException.getMessage(), sqlException);
        }
        return items;
    }

    @Override
    public void update(InventoryItem item){
        String sql = "UPDATE inventory_items SET name = ?, category = ?, quantity = ?, unit = ?, cost_per_unit = ?, reorder_level = ?, supplier = ?, last_restocked_date = ?, expiry_date = ? WHERE inventory_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, item.getInventoryItemName());
                statement.setString(2, item.getInventoryCategory());
                statement.setInt(3, item.getInventoryQuantity());
                statement.setString(4, item.getInventoryUnit());
                statement.setDouble(5, item.getInventoryCostPerUnit());
                statement.setInt(6, item.getInventoryReorderLevel());
                statement.setString(7, item.getInventorySupplier());
                statement.setDate(8, item.getInventoryLastRestockedDate() != null ? Date.valueOf(item.getInventoryLastRestockedDate()) : null);
                statement.setDate(9, item.getInventoryExpiryDate() != null ? Date.valueOf(item.getInventoryExpiryDate()) : null);
                statement.setInt(10, item.getInventoryItemId());
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to update InventoryItem with ID " + item.getInventoryItemId() + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public void delete(int inventoryId){
        String sql = "DELETE FROM inventory_items WHERE inventory_item_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, inventoryId);
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to delete InventoryItem with ID " + inventoryId + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public ArrayList<InventoryItem> findByCategory(String category){
        ArrayList<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_items WHERE category = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, category);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        items.add(toInventoryItem(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving InventoryItems in category '" + category + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return items;
    }

    @Override
    public ArrayList<InventoryItem> findExpiringSoon(int daysThreshold){
        ArrayList<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_items WHERE expiry_date IS NOT NULL AND expiry_date <= ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setDate(1, Date.valueOf(LocalDate.now().plusDays(daysThreshold)));
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        items.add(toInventoryItem(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving InventoryItems expiring within " + daysThreshold + " days. Details: " + sqlException.getMessage(), sqlException);
        }
        return items;
    }

    @Override
    public ArrayList<InventoryItem> findLowStock(){
        ArrayList<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_items WHERE quantity <= reorder_level";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        items.add(toInventoryItem(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving low-stock InventoryItems. Details: " + sqlException.getMessage(), sqlException);
        }
        return items;
    }
}
