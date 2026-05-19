
package dataservice;

import java.time.LocalDate;
import java.util.ArrayList;
import model.RestaurantModel.MenuItem;
import model.RestaurantModel.Order;
import model.RestaurantModel.Payment;

/**
 *
 * @author Allysa
 */
public class RestaurantDataService {

    public interface IMenuItem {

        void create(MenuItem item);
        MenuItem get(int itemId);
        ArrayList<MenuItem> getAll();
        void update(MenuItem item);
        void delete(int itemId);

        ArrayList<MenuItem> findAvailable();
        ArrayList<MenuItem> findByCategory(String category);
    }

    public interface IOrder {

        void create(Order order);
        Order get(int orderId);
        ArrayList<Order> getAll();
        void update(Order order);
        void delete(int orderId);

        ArrayList<Order> findByStatus(String status);
        ArrayList<Order> findByTable(String tableNumber);
    }

    public interface IPayment {

        void create(Payment payment);
        Payment get(int paymentId);
        ArrayList<Payment> getAll();
        void update(Payment payment);
        void delete(int paymentId);

        ArrayList<Payment> findByDate(LocalDate date);
        ArrayList<Payment> findByOrderId(int orderId);
        ArrayList<Payment> findByPaymentMethod(String method);
        ArrayList<Payment> findByStatus(String status);
    }

}
