
package dataservice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import model.RestaurantModel.*;
import dataservice.RestaurantDataService.*;
import java.time.LocalDateTime;

/**
 *
 * @author Allysa
 */
public class RestaurantInMemory {

    public static class InMemoryUser implements IUser {

        private final HashMap<String, User> users;

        public InMemoryUser(){
            users = new HashMap<>();

            // Username: admin, Passowrd: password
            // Username: cashier, Passowrd: cashier123
            users.put("admin", new User("admin", "password", Role.MANAGER));
            users.put("cashier", new User("cashier", "cashier123", Role.CASHIER));
        }

        @Override
        public void addUser(User user){
            if(user == null || user.getUserName() == null){
                throw new IllegalArgumentException("User or username cannot be null");
            }
            users.put(user.getUserName(), user);
        }

        @Override
        public User findByUsername(String username){
            return users.get(username);
        }

        @Override
        public void updateUser(User user){
            if(user == null || user.getUserName() == null){
                throw new IllegalArgumentException("User or username cannot be null");
            }
            if(!users.containsKey(user.getUserName())){
                throw new IllegalArgumentException("User not found: " + user.getUserName());
            }
            users.put(user.getUserName(), user);
        }

        @Override
        public void deleteUser(String username){
            users.remove(username);
        }

        @Override
        public boolean userExists(String username){
            return users.containsKey(username);
        }

        @Override
        public ArrayList<User> getAllUsers(){
            return new ArrayList<>(users.values());
        }
    }

    public static class InMemoryInventoryItem extends AbstractDataService<InventoryItem> implements IInventoryItem {

        private final ArrayList<InventoryItem> lowStockItems = new ArrayList<>();
        private final ArrayList<InventoryItem> matchingItems = new ArrayList<>();
        private final ArrayList<InventoryItem> expiringItems = new ArrayList<>();

        public InMemoryInventoryItem(){
            sampleInventoryData();
        }

        private void sampleInventoryData(){
            // ID 0 (auto), Name: Chicken Breast, Category: Meat, Qty: 50, Unit: kg, Cost: 30.00, Reorder: 20, Supplier: Puregold, Restocked: today, Expiry: 7 days
            create(new InventoryItem(0, "Chicken Breast", "Meat", 50, "kg", 30.00, 20, "Puregold", LocalDate.now(), LocalDate.now().plusDays(7)));

            // ID 0 (auto), Name: Beef Patty, Category: Meat, Qty: 40, Unit: kg, Cost: 45.00, Reorder: 15, Supplier: Puregold, Restocked: today, Expiry: 5 days
            create(new InventoryItem(0, "Beef Patty", "Meat", 40, "kg", 45.00, 15, "Puregold", LocalDate.now(), LocalDate.now().plusDays(5)));

            // ID 0 (auto), Name: Fish Fillet, Category: Meat, Qty: 8, Unit: kg, Cost: 35.00, Reorder: 10, Supplier: Oceana, Restocked: 3 days ago, Expiry: 2 days
            create(new InventoryItem(0, "Fish Fillet", "Meat", 8, "kg", 35.00, 10, "Oceana", LocalDate.now().minusDays(3), LocalDate.now().plusDays(2)));

            // ID 0 (auto), Name: Lettuce, Category: Vegetable, Qty: 25, Unit: kg, Cost: 5.00, Reorder: 10, Supplier: Binan Bayan, Restocked: today, Expiry: 4 days
            create(new InventoryItem(0, "Lettuce", "Vegetable", 25, "kg", 5.00, 10, "Binan Bayan", LocalDate.now(), LocalDate.now().plusDays(4)));

            // ID 0 (auto), Name: Tomato, Category: Vegetable, Qty: 5, Unit: kg, Cost: 4.00, Reorder: 8, Supplier: Binan Bayan, Restocked: 2 days ago, Expiry: 3 days
            create(new InventoryItem(0, "Tomato", "Vegetable", 5, "kg", 4.00, 8, "Binan Bayan", LocalDate.now().minusDays(2), LocalDate.now().plusDays(3)));

            // ID 0 (auto), Name: Coke Syrup, Category: Beverage, Qty: 30, Unit: liters, Cost: 12.00, Reorder: 10, Supplier: Coca-Cola, Restocked: today, Expiry: 90 days
            create(new InventoryItem(0, "Coke Syrup", "Beverage", 30, "liters", 12.00, 10, "Coca-Cola", LocalDate.now(), LocalDate.now().plusDays(90)));

            // ID 0 (auto), Name: Ice Cream Mix, Category: Beverage, Qty: 12, Unit: liters, Cost: 8.00, Reorder: 5, Supplier: Dairy Best, Restocked: today, Expiry: 14 days
            create(new InventoryItem(0, "Ice Cream Mix", "Beverage", 12, "liters", 8.00, 5, "Dairy Best", LocalDate.now(), LocalDate.now().plusDays(14)));

            // ID 0 (auto), Name: Cooking Oil, Category: Supply, Qty: 20, Unit: liters, Cost: 3.50, Reorder: 5, Supplier: Pure Oil, Restocked: today, Expiry: 180 days
            create(new InventoryItem(0, "Cooking Oil", "Supply", 20, "liters", 3.50, 5, "Pure Oil", LocalDate.now(), LocalDate.now().plusDays(180)));

            // ID 0 (auto), Name: Paper Wrapper, Category: Supply, Qty: 500, Unit: pieces, Cost: 0.10, Reorder: 100, Supplier: Julies, Restocked: today, Expiry: 365 days
            create(new InventoryItem(0, "Paper Wrapper", "Supply", 500, "pieces", 0.10, 100, "Julies", LocalDate.now(), LocalDate.now().plusDays(365)));
        }

        @Override
        protected int getModelId(InventoryItem inventoryItem){
            return inventoryItem.getInventoryItemId();
        }

        @Override
        protected void setModelId(InventoryItem inventoryItem, int inventoryId){
            inventoryItem.setInventoryItemId(inventoryId);
        }

        @Override
        public ArrayList<InventoryItem> findByCategory(String targetCategory){
            matchingItems.clear();
            for(InventoryItem currentItem : storage.values()){
                if(currentItem.getInventoryCategory().equals(targetCategory)){
                    matchingItems.add(currentItem);
                }
            }
            return matchingItems;
        }

        @Override
        public ArrayList<InventoryItem> findExpiringSoon(int daysThreshold){
            expiringItems.clear();
            LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
            for(InventoryItem currentItem : storage.values()){
                if(currentItem.getInventoryExpiryDate() != null && !currentItem.getInventoryExpiryDate().isAfter(thresholdDate)){
                    expiringItems.add(currentItem);
                }
            }
            return expiringItems;
        }

        @Override
        public ArrayList<InventoryItem> findLowStock(){
            lowStockItems.clear();
            for(InventoryItem currentItem : storage.values()){
                if(currentItem.getInventoryQuantity() <= currentItem.getInventoryReorderLevel()){
                    lowStockItems.add(currentItem);
                }
            }
            return lowStockItems;
        }
    }

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

    public static class InMemoryFoodWaste extends AbstractDataService<FoodWaste> implements IFoodWaste {

        private final ArrayList<FoodWaste> dateRangeWasteRecords = new ArrayList<>();
        private final ArrayList<FoodWaste> matchingWasteRecords = new ArrayList<>();
        private final ArrayList<FoodWaste> categoryWasteRecords = new ArrayList<>();

        public InMemoryFoodWaste(){
            sampleFoodWasteData();
        }

        private void sampleFoodWasteData(){
            // Waste 1: ID 0 (auto), Item Chicken Breast, Qty 2.5, Unit kg, Reason Expired, Cost 75.00, Recorded yesterday, By admin, Category Meat
            create(new FoodWaste(0, "Chicken Breast", 2.5, "kg", "Expired", 75.00, LocalDateTime.now().minusDays(1), "admin", "Meat"));

            // Waste 2: ID 0 (auto), Item Nuggets, Qty 1.0, Unit kg, Reason Overcooked, Cost 70.00, Recorded today, By cashier, Category Main
            create(new FoodWaste(0, "Nuggets", 1.0, "kg", "Overcooked", 70.00, LocalDateTime.now(), "cashier", "Main"));
        }

        @Override
        protected int getModelId(FoodWaste foodWasteRecord){
            return foodWasteRecord.getFoodWasteId();
        }

        @Override
        protected void setModelId(FoodWaste foodWasteRecord, int wasteId){
            foodWasteRecord.setFoodWasteId(wasteId);
        }

        @Override
        public ArrayList<FoodWaste> findByCategory(String targetCategory){
            categoryWasteRecords.clear();
            for(FoodWaste currentRecord : storage.values()){
                if(currentRecord.getFoodWasteCategory().equals(targetCategory)){
                    categoryWasteRecords.add(currentRecord);
                }
            }
            return categoryWasteRecords;
        }

        @Override
        public ArrayList<FoodWaste> findByDateRange(LocalDate startDate, LocalDate endDate){
            dateRangeWasteRecords.clear();
            for(FoodWaste currentRecord : storage.values()){
                LocalDate wasteDate = currentRecord.getFoodWasteRecordedDate().toLocalDate();
                if(!wasteDate.isBefore(startDate) && !wasteDate.isAfter(endDate)){
                    dateRangeWasteRecords.add(currentRecord);
                }
            }
            return dateRangeWasteRecords;
        }

        @Override
        public ArrayList<FoodWaste> findByReason(String targetReason){
            matchingWasteRecords.clear();
            for(FoodWaste currentRecord : storage.values()){
                if(currentRecord.getFoodWasteReason().equals(targetReason)){
                    matchingWasteRecords.add(currentRecord);
                }
            }
            return matchingWasteRecords;
        }
    }

}
