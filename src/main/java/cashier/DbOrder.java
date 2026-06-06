package cashier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import user.DatabaseConnection;

public class DbOrder implements IOrder {

    private ArrayList<OrderItem> getOrderItems(Connection connection, int orderId) throws SQLException{
        ArrayList<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.order_item_id, oi.quantity, m.menu_item_id, m.name, m.price, m.category, m.is_available " + "FROM order_items oi JOIN menu_items m ON oi.menu_item_id = m.menu_item_id " + "WHERE oi.order_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, orderId);
            try(ResultSet rs = statement.executeQuery()){
                while(rs.next()){
                    MenuItem menuItem = new MenuItem();
                    menuItem.setMenuItemId(rs.getInt("menu_item_id"));
                    menuItem.setMenuItemName(rs.getString("name"));
                    menuItem.setMenuItemPrice(rs.getDouble("price"));
                    menuItem.setMenuItemCategory(rs.getString("category"));
                    menuItem.setAvailable(rs.getBoolean("is_available"));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderItemId(rs.getString("order_item_id"));
                    orderItem.setLinkedMenuItem(menuItem);
                    orderItem.setOrderItemQuantity(rs.getInt("quantity"));
                    items.add(orderItem);
                }
            }
        }
        return items;
    }

    private Order toOrder(ResultSet rs, Connection connection) throws SQLException{
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderTableNumber(rs.getString("table_number"));
        order.setOrderStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_time");
        if(ts != null){
            order.setOrderCreatedTime(ts.toLocalDateTime());
        }
        order.setOrderTotalAmount(rs.getDouble("total_amount"));
        order.setOrderItems(getOrderItems(connection, order.getOrderId()));
        return order;
    }

    @Override
    public void create(Order order){
        String sql = "INSERT INTO orders (table_number, status, created_time, total_amount) VALUES (?, ?, ?, ?)";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, order.getOrderTableNumber());
                statement.setString(2, order.getOrderStatus());
                statement.setTimestamp(3, order.getOrderCreatedTime() != null ? Timestamp.valueOf(order.getOrderCreatedTime()) : null);
                statement.setDouble(4, order.getOrderTotalAmount());
                statement.executeUpdate();
                try(ResultSet keys = statement.getGeneratedKeys()){
                    if(keys.next()){
                        order.setOrderId(keys.getInt(1));
                    }
                }
            }
            if(order.getOrderItems() != null){
                saveOrderItems(connection, order);
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to create Order for table " + order.getOrderTableNumber() + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    private void saveOrderItems(Connection connection, Order order) throws SQLException{
        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity) VALUES (?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            for(OrderItem item : order.getOrderItems()){
                statement.setInt(1, order.getOrderId());
                statement.setInt(2, item.getLinkedMenuItem().getMenuItemId());
                statement.setInt(3, item.getOrderItemQuantity());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @Override
    public Order get(int orderId){
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, orderId);
                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()){
                        return toOrder(rs, connection);
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Order with ID " + orderId + ". Details: " + sqlException.getMessage(), sqlException);
        }
        return null;
    }

    @Override
    public ArrayList<Order> getAll(){
        ArrayList<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        orders.add(toOrder(rs, connection));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving all Orders. Details: " + sqlException.getMessage(), sqlException);
        }
        return orders;
    }

    @Override
    public void update(Order order){
        String sql = "UPDATE orders SET table_number = ?, status = ?, created_time = ?, total_amount = ? WHERE order_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, order.getOrderTableNumber());
                statement.setString(2, order.getOrderStatus());
                statement.setTimestamp(3, order.getOrderCreatedTime() != null ? Timestamp.valueOf(order.getOrderCreatedTime()) : null);
                statement.setDouble(4, order.getOrderTotalAmount());
                statement.setInt(5, order.getOrderId());
                statement.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to update Order with ID " + order.getOrderId() + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public void delete(int orderId){
        String sqlItems = "DELETE FROM order_items WHERE order_id = ?";
        String sqlOrder = "DELETE FROM orders WHERE order_id = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statementItems = connection.prepareStatement(sqlItems)){
                statementItems.setInt(1, orderId);
                statementItems.executeUpdate();
            }
            try(PreparedStatement statementOrder = connection.prepareStatement(sqlOrder)){
                statementOrder.setInt(1, orderId);
                statementOrder.executeUpdate();
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Failed to delete Order with ID " + orderId + ". Details: " + sqlException.getMessage(), sqlException);
        }
    }

    @Override
    public ArrayList<Order> findByStatus(String status){
        ArrayList<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, status);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        orders.add(toOrder(rs, connection));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Orders with status '" + status + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return orders;
    }

    @Override
    public ArrayList<Order> findByTable(String tableNumber){
        ArrayList<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE table_number = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, tableNumber);
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        orders.add(toOrder(rs, connection));
                    }
                }
            }
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
            throw new RuntimeException("Error retrieving Orders for table '" + tableNumber + "'. Details: " + sqlException.getMessage(), sqlException);
        }
        return orders;
    }
}
