package manager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import user.DatabaseConnection;

public class DbFoodWaste implements IFoodWaste {

    private FoodWaste toFoodWaste(ResultSet rs) throws SQLException{
        FoodWaste waste = new FoodWaste();
        waste.setFoodWasteId(rs.getInt("food_waste_id"));
        waste.setFoodWasteItemName(rs.getString("item_name"));
        waste.setFoodWasteQuantity(rs.getDouble("quantity"));
        waste.setFoodWasteUnit(rs.getString("unit"));
        waste.setFoodWasteReason(rs.getString("reason"));
        waste.setFoodWasteEstimatedCost(rs.getDouble("estimated_cost"));
        Timestamp ts = rs.getTimestamp("recorded_date");
        if(ts != null){
            waste.setFoodWasteRecordedDate(ts.toLocalDateTime());
        }
        waste.setFoodWasteRecordedBy(rs.getString("recorded_by"));
        waste.setFoodWasteCategory(rs.getString("category"));
        return waste;
    }

    @Override
    public void create(FoodWaste waste){
        String sql = "INSERT INTO food_waste (item_name, quantity, unit, reason, estimated_cost, recorded_date, recorded_by, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, waste.getFoodWasteItemName());
                statement.setDouble(2, waste.getFoodWasteQuantity());
                statement.setString(3, waste.getFoodWasteUnit());
                statement.setString(4, waste.getFoodWasteReason());
                statement.setDouble(5, waste.getFoodWasteEstimatedCost());
                statement.setTimestamp(6, waste.getFoodWasteRecordedDate() != null ? Timestamp.valueOf(waste.getFoodWasteRecordedDate()) : null);
                statement.setString(7, waste.getFoodWasteRecordedBy());
                statement.setString(8, waste.getFoodWasteCategory());
                statement.executeUpdate();
                try(ResultSet keys = statement.getGeneratedKeys()){
                    if(keys.next()){
                        waste.setFoodWasteId(keys.getInt(1));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to create FoodWaste record for item '" + waste.getFoodWasteItemName() + "'. Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public FoodWaste get(int wasteId){
        String sql = "SELECT * FROM food_waste WHERE food_waste_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, wasteId);
                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()){
                        return toFoodWaste(rs);
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving FoodWaste record with ID " + wasteId + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return null;
    }

    @Override
    public ArrayList<FoodWaste> getAll(){
        ArrayList<FoodWaste> records = new ArrayList<>();
        String sql = "SELECT * FROM food_waste";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        records.add(toFoodWaste(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving all FoodWaste records. Details: " + sqlException.getMessage(), sqlException);
        }
        return records;
    }

    @Override
    public void update(FoodWaste waste){
        String sql = "UPDATE food_waste SET item_name = ?, quantity = ?, unit = ?, reason = ?, estimated_cost = ?, recorded_date = ?, recorded_by = ?, category = ? WHERE food_waste_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, waste.getFoodWasteItemName());
                statement.setDouble(2, waste.getFoodWasteQuantity());
                statement.setString(3, waste.getFoodWasteUnit());
                statement.setString(4, waste.getFoodWasteReason());
                statement.setDouble(5, waste.getFoodWasteEstimatedCost());
                statement.setTimestamp(6, waste.getFoodWasteRecordedDate() != null ? Timestamp.valueOf(waste.getFoodWasteRecordedDate()) : null);
                statement.setString(7, waste.getFoodWasteRecordedBy());
                statement.setString(8, waste.getFoodWasteCategory());
                statement.setInt(9, waste.getFoodWasteId());
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to update FoodWaste record with ID " + waste.getFoodWasteId() + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public void delete(int wasteId){
        String sql = "DELETE FROM food_waste WHERE food_waste_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, wasteId);
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to delete FoodWaste record with ID " + wasteId + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public ArrayList<FoodWaste> findByCategory(String category){
        ArrayList<FoodWaste> records = new ArrayList<>();
        String sql = "SELECT * FROM food_waste WHERE category = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, category);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        records.add(toFoodWaste(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving FoodWaste records in category '" + category + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return records;
    }

    @Override
    public ArrayList<FoodWaste> findByDateRange(LocalDate startDate, LocalDate endDate){
        ArrayList<FoodWaste> records = new ArrayList<>();
        String sql = "SELECT * FROM food_waste WHERE DATE(recorded_date) BETWEEN ? AND ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setDate(1, Date.valueOf(startDate));
                statement.setDate(2, Date.valueOf(endDate));
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        records.add(toFoodWaste(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving FoodWaste records between " + startDate + " and " + endDate + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return records;
    }

    @Override
    public ArrayList<FoodWaste> findByReason(String reason){
        ArrayList<FoodWaste> records = new ArrayList<>();
        String sql = "SELECT * FROM food_waste WHERE reason = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, reason);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        records.add(toFoodWaste(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving FoodWaste records with reason '" + reason + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return records;
    }

    @Override
    public ArrayList<LocalDate> getAvailableDates(){
        ArrayList<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE(recorded_date) AS rec_date FROM food_waste WHERE recorded_date IS NOT NULL ORDER BY rec_date DESC";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        Date d = rs.getDate("rec_date");
                        if(d != null){
                            dates.add(d.toLocalDate());
                        }
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving available food waste dates. Details: " + sqlException.getMessage(), sqlException);
        }
        return dates;
    }
}
