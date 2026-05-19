
package dataservice;

import dataservice.RestaurantDataService.IMenuItem;
import dataservice.RestaurantDataService.IOrder;
import dataservice.RestaurantDataService.IPayment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import model.RestaurantModel.MenuItem;
import model.RestaurantModel.Order;
import model.RestaurantModel.Payment;
import user.AbstractDataService;

/**
 *
 * @author Allysa
 */
public class RestaurantInMemory {

    public static class InMemoryMenuItem extends AbstractDataService<MenuItem> implements IMenuItem {

        private final ArrayList<MenuItem> matchingMenuItems = new ArrayList<>();
        private final ArrayList<MenuItem> availableMenuItems = new ArrayList<>();

        public InMemoryMenuItem(){
            sampleMenuData();
        }

        private void sampleMenuData(){
            // ID 0 (auto), Name: Chicken, Price: 70.00, Category: Main, Available: true
            create(new MenuItem(0, "Chicken", 70.00, "Main", true));

            // ID 0 (auto), Name: Nuggets, Price: 70.00, Category: Main, Available: true
            create(new MenuItem(0, "Nuggets", 70.00, "Main", true));

            // ID 0 (auto), Name: Fish Fillet, Price: 50.00, Category: Main, Available: true
            create(new MenuItem(0, "Fish Fillet", 50.00, "Main", true));

            // ID 0 (auto), Name: McCafe, Price: 20.00, Category: Drink, Available: true
            create(new MenuItem(0, "McCafe", 20.00, "Drink", true));

            // ID 0 (auto), Name: Coke Float, Price: 20.00, Category: Drink, Available: true
            create(new MenuItem(0, "Coke Float", 20.00, "Drink", true));

            // ID 0 (auto), Name: Sundae, Price: 20.00, Category: Drink, Available: true
            create(new MenuItem(0, "Sundae", 20.00, "Drink", true));

            // ID 0 (auto), Name: McFlurry, Price: 25.00, Category: Drink, Available: true
            create(new MenuItem(0, "McFlurry", 25.00, "Drink", true));

            // ID 0 (auto), Name: Beef Burger, Price: 85.00, Category: Main, Available: false
            create(new MenuItem(0, "Beef Burger", 85.00, "Main", false));

            // ID 0 (auto), Name: Fries, Price: 40.00, Category: Side, Available: false
            create(new MenuItem(0, "Fries", 40.00, "Side", false));

            // ID 0 (auto), Name: Milk Shake, Price: 30.00, Category: Drink, Available: false
            create(new MenuItem(0, "Milk Shake", 30.00, "Drink", false));
        }

        @Override
        protected int getModelId(MenuItem menuItem){
            return menuItem.getMenuItemId();
        }

        @Override
        protected void setModelId(MenuItem menuItem, int itemId){
            menuItem.setMenuItemId(itemId);
        }

        @Override
        public ArrayList<MenuItem> findAvailable(){
            availableMenuItems.clear();
            for(MenuItem currentItem : storage.values()){
                if(currentItem.isAvailable()){
                    availableMenuItems.add(currentItem);
                }
            }
            return availableMenuItems;
        }

        @Override
        public ArrayList<MenuItem> findByCategory(String targetCategory){
            matchingMenuItems.clear();
            for(MenuItem currentItem : storage.values()){
                if(currentItem.getMenuItemCategory().equals(targetCategory)){
                    matchingMenuItems.add(currentItem);
                }
            }
            return matchingMenuItems;
        }
    }

    public static class InMemoryOrder extends AbstractDataService<Order> implements IOrder {

        private final ArrayList<Order> matchingOrders = new ArrayList<>();
        private final ArrayList<Order> tableOrders = new ArrayList<>();

        public InMemoryOrder(){
            sampleOrderData();
        }

        private void sampleOrderData(){
            /* Order 1: ID 0 (auto), Table T1, Status Completed, Created 2 hours ago, Items empty, Total 140.00
            The order was served and paid successfully (see Payment 1). */
            create(new Order(0, "T1", "Completed", LocalDateTime.now().minusHours(2), new ArrayList<>(), 140.00));

            /* Order 2: ID 0 (auto), Table T2, Status Served, Created 1 hour ago, Items empty, Total 50.00
            The order is ready for payment but the payment attempt failed (see Payment 2), so it stays open. */
            create(new Order(0, "T2", "Served", LocalDateTime.now().minusHours(1), new ArrayList<>(), 50.00));

            /* Order 3: ID 0 (auto), Table T3, Status Preparing, Created 30 minutes ago, Items empty, Total 70.00
            Still in kitchen; payment is not allowed yet. */
            create(new Order(0, "T3", "Preparing", LocalDateTime.now().minusMinutes(30), new ArrayList<>(), 70.00));

            /* Order 4: ID 0 (auto), Table T4, Status Ready, Created 45 minutes ago, Items empty, Total 20.00
            Ready to served; payment is allowed but not been attempted. */
            create(new Order(0, "T4", "Ready", LocalDateTime.now().minusMinutes(45), new ArrayList<>(), 20.00));
        }

        @Override
        protected int getModelId(Order restaurantOrder){
            return restaurantOrder.getOrderId();
        }

        @Override
        protected void setModelId(Order restaurantOrder, int orderId){
            restaurantOrder.setOrderId(orderId);
        }

        @Override
        public ArrayList<Order> findByStatus(String targetStatus){
            matchingOrders.clear();
            for(Order currentOrder : storage.values()){
                if(currentOrder.getOrderStatus().equals(targetStatus)){
                    matchingOrders.add(currentOrder);
                }
            }
            return matchingOrders;
        }

        @Override
        public ArrayList<Order> findByTable(String targetTableNumber){
            tableOrders.clear();
            for(Order currentOrder : storage.values()){
                if(currentOrder.getOrderTableNumber().equals(targetTableNumber)){
                    tableOrders.add(currentOrder);
                }
            }
            return tableOrders;
        }
    }

    public static class InMemoryPayment extends AbstractDataService<Payment> implements IPayment {

        private final ArrayList<Payment> orderPayments = new ArrayList<>();
        private final ArrayList<Payment> matchingPayments = new ArrayList<>();
        private final ArrayList<Payment> datePayments = new ArrayList<>();
        private final ArrayList<Payment> methodPayments = new ArrayList<>();

        public InMemoryPayment(){
            samplePaymentData();
        }

        private void samplePaymentData(){
            /*Payment 1: ID 0 (auto), Order ID 1, Amount 140.00, Tip 10.00, Method cash, Status Completed, Timestamp 90 minutes ago, TXN RMS-TXN-20260515-1234
            Successful payment. AppService automatically sets the linked order to Completed. */
            create(new Payment(0, 1, 140.00, 10.00, "cash", "Completed", LocalDateTime.now().minusMinutes(90), "RMS-TXN-20260515-1234"));

            /*Payment 2: ID 0 (auto), Order ID 2, Amount 50.00, Tip 5.00, Method GCash, Status Failed, Timestamp 45 minutes ago, TXN RMS-TXN-20260515-5678
            Failed payment. AppService leaves the linked order as Served so it can be paid again. */
            create(new Payment(0, 2, 50.00, 5.00, "GCash", "Failed", LocalDateTime.now().minusMinutes(45), "RMS-TXN-20260515-5678"));
        }

        @Override
        protected int getModelId(Payment paymentRecord){
            return paymentRecord.getPaymentId();
        }

        @Override
        protected void setModelId(Payment paymentRecord, int paymentId){
            paymentRecord.setPaymentId(paymentId);
        }

        @Override
        public ArrayList<Payment> findByDate(LocalDate targetDate){
            datePayments.clear();
            for(Payment currentPayment : storage.values()){
                LocalDate paymentDate = currentPayment.getPaymentTimestamp().toLocalDate();
                if(paymentDate.equals(targetDate)){
                    datePayments.add(currentPayment);
                }
            }
            return datePayments;
        }

        @Override
        public ArrayList<Payment> findByOrderId(int targetOrderId){
            orderPayments.clear();
            for(Payment currentPayment : storage.values()){
                if(currentPayment.getLinkedOrderId() == targetOrderId){
                    orderPayments.add(currentPayment);
                }
            }
            return orderPayments;
        }

        @Override
        public ArrayList<Payment> findByPaymentMethod(String targetPaymentMethod){
            methodPayments.clear();
            for(Payment currentPayment : storage.values()){
                if(currentPayment.getPaymentMethod().equals(targetPaymentMethod)){
                    methodPayments.add(currentPayment);
                }
            }
            return methodPayments;
        }

        @Override
        public ArrayList<Payment> findByStatus(String targetStatus){
            matchingPayments.clear();
            for(Payment currentPayment : storage.values()){
                if(currentPayment.getPaymentStatus().equals(targetStatus)){
                    matchingPayments.add(currentPayment);
                }
            }
            return matchingPayments;
        }
    }

}
