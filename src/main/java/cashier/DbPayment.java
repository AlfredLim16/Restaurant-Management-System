package cashier;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import user.DatabaseConnection;

public class DbPayment implements IPayment {

    private Payment toPayment(ResultSet rs) throws SQLException{
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setLinkedOrderId(rs.getInt("order_id"));
        payment.setPaymentAmount(rs.getDouble("amount"));
        payment.setPaymentTipAmount(rs.getDouble("tip_amount"));
        payment.setPaymentMethod(rs.getString("method"));
        payment.setPaymentStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("payment_timestamp");
        if(ts != null){
            payment.setPaymentTimestamp(ts.toLocalDateTime());
        }
        payment.setPaymentTransactionId(rs.getString("transaction_id"));
        return payment;
    }

    @Override
    public void create(Payment payment){
        String sql = "INSERT INTO payments (order_id, amount, tip_amount, method, status, payment_timestamp, transaction_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                statement.setInt(1, payment.getLinkedOrderId());
                statement.setDouble(2, payment.getPaymentAmount());
                statement.setDouble(3, payment.getPaymentTipAmount());
                statement.setString(4, payment.getPaymentMethod());
                statement.setString(5, payment.getPaymentStatus());
                statement.setTimestamp(6, payment.getPaymentTimestamp() != null ? Timestamp.valueOf(payment.getPaymentTimestamp()) : null);
                statement.setString(7, payment.getPaymentTransactionId());
                statement.executeUpdate();
                try(ResultSet keys = statement.getGeneratedKeys()){
                    if(keys.next()){
                        payment.setPaymentId(keys.getInt(1));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to create Payment for Order ID " + payment.getLinkedOrderId() + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public Payment get(int paymentId){
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, paymentId);
                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()){
                        return toPayment(rs);
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Payment with ID " + paymentId + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return null;
    }

    @Override
    public ArrayList<Payment> getAll(){
        ArrayList<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        payments.add(toPayment(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving all Payments. Details: " + sqlException.getMessage(), sqlException);
        }
        return payments;
    }

    @Override
    public void update(Payment payment){
        String sql = "UPDATE payments SET order_id = ?, amount = ?, tip_amount = ?, method = ?, status = ?, payment_timestamp = ?, transaction_id = ? WHERE payment_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, payment.getLinkedOrderId());
                statement.setDouble(2, payment.getPaymentAmount());
                statement.setDouble(3, payment.getPaymentTipAmount());
                statement.setString(4, payment.getPaymentMethod());
                statement.setString(5, payment.getPaymentStatus());
                statement.setTimestamp(6, payment.getPaymentTimestamp() != null ? Timestamp.valueOf(payment.getPaymentTimestamp()) : null);
                statement.setString(7, payment.getPaymentTransactionId());
                statement.setInt(8, payment.getPaymentId());
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to update Payment with ID " + payment.getPaymentId() + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public void delete(int paymentId){
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, paymentId);
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to delete Payment with ID " + paymentId + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public ArrayList<Payment> findByDate(LocalDate targetDate){
        ArrayList<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE DATE(payment_timestamp) = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setDate(1, Date.valueOf(targetDate));
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        payments.add(toPayment(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Payments on date " + targetDate + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return payments;
    }

    @Override
    public ArrayList<Payment> findByOrderId(int orderId){
        ArrayList<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, orderId);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        payments.add(toPayment(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Payments for Order ID " + orderId + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return payments;
    }

    @Override
    public ArrayList<Payment> findByPaymentMethod(String method){
        ArrayList<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE method = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, method);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        payments.add(toPayment(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Payments with method '" + method + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return payments;
    }

    @Override
    public ArrayList<Payment> findByStatus(String status){
        ArrayList<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE status = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, status);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        payments.add(toPayment(rs));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Payments with status '" + status + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return payments;
    }

    @Override
    public ArrayList<LocalDate> getAvailableDates(){
        ArrayList<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE(payment_timestamp) AS pay_date FROM payments WHERE payment_timestamp IS NOT NULL ORDER BY pay_date DESC";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        Date d = rs.getDate("pay_date");
                        if(d != null){
                            dates.add(d.toLocalDate());
                        }
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving available payment dates. Details: " + sqlException.getMessage(), sqlException);
        }
        return dates;
    }
}
