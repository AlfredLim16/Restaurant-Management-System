package cashier;

import java.time.LocalDate;
import java.util.ArrayList;

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
    ArrayList<LocalDate> getAvailableDates();
}
