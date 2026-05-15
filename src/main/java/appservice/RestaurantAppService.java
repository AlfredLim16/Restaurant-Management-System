
package appservice;

import dataservice.RestaurantDataService;
import dataservice.RestaurantDataService.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import model.RestaurantModel;
import model.RestaurantModel.*;

/**
 *
 * @author admin
 */
public class RestaurantAppService {

    public static final class OrderStatus {

        public static final String PREPARING = "Preparing";
        public static final String READY = "Ready";
        public static final String SERVED = "Served";
        public static final String COMPLETED = "Completed";
        public static final String CANCELLED = "Cancelled";

        private OrderStatus(){

        }
    }

    public static final class PaymentStatus {

        public static final String COMPLETED = "Completed";
        public static final String PENDING = "Pending";
        public static final String FAILED = "Failed";
        public static final String REFUNDED = "Refunded";

        private PaymentStatus(){

        }
    }

    public static final class PaymentMethod {

        public static final String CASH = "cash";
        public static final String CARD = "card";

        public static final String GCASH = "GCash";
        public static final String MAYA = "Maya";

        private PaymentMethod(){

        }
    }

    public static class BusinessException extends Exception {

        public BusinessException(String message){
            super(message);
        }

        public BusinessException(String message, Throwable cause){
            super(message, cause);
        }
    }

    public static class InsufficientInventoryException extends BusinessException {

        public InsufficientInventoryException(String message){
            super(message);
        }

        public InsufficientInventoryException(String message, Throwable cause){
            super(message, cause);
        }
    }

    public static class PaymentFailedException extends BusinessException {

        public PaymentFailedException(String message){
            super(message);
        }

        public PaymentFailedException(String message, Throwable cause){
            super(message, cause);
        }
    }

    public static class ValidationException extends BusinessException {

        public ValidationException(String message){
            super(message);
        }

        public ValidationException(String message, Throwable cause){
            super(message, cause);
        }
    }

    public static abstract class AbstractAppService {

        // returns the model if it is not null
        protected <Model> Model getOrThrow(Model model, String message) throws ValidationException{
            if(model == null){
                throw new ValidationException(message);
            }
            return model;
        }

        // if the string is null or contains only whitespace
        protected void ensureNotEmpty(String valueToCheck, String fieldName) throws ValidationException{
            if(valueToCheck == null || valueToCheck.trim().isEmpty()){
                throw new ValidationException(fieldName + " is required");
            }
        }

        // ? - it's accepts any kind of arraylist type like String, Integer and etc
        protected void ensureNotNullNotEmpty(ArrayList<?> listToCheck, String fieldName) throws ValidationException{
            if(listToCheck == null || listToCheck.isEmpty()){
                throw new ValidationException(fieldName + " must contain at least one item");
            }
        }

        // if the int value is 0 or negative
        protected void ensurePositive(int valueToCheck, String fieldName) throws ValidationException{
            if(valueToCheck <= 0){
                throw new ValidationException(fieldName + " must be greater than 0");
            }
        }

        // if the double value is 0 or negative
        protected void ensurePositive(double valueToCheck, String fieldName) throws ValidationException{
            if(valueToCheck <= 0){
                throw new ValidationException(fieldName + " must be greater than 0");
            }
        }

        // if the int value is negative
        protected void ensureNotNegative(int valueToCheck, String fieldName) throws ValidationException{
            if(valueToCheck < 0){
                throw new ValidationException(fieldName + " cannot be negative");
            }
        }

        // if the double value is negative
        protected void ensureNotNegative(double valueToCheck, String fieldName) throws ValidationException{
            if(valueToCheck < 0){
                throw new ValidationException(fieldName + " cannot be negative");
            }
        }

        // if either date is null, or if startDate comes after endDate
        protected void ensureDateRange(LocalDate startDate, LocalDate endDate) throws ValidationException{
            if(startDate == null || endDate == null){
                throw new ValidationException("Start date and end date are required");
            }
            if(startDate.isAfter(endDate)){
                throw new ValidationException("Start date cannot be after end date");
            }
        }

        // add value in a arraylist inside of hashmap, if no keys exist arraylist will created first
        protected <Key, Value> void putIntoGroupedMap(HashMap<Key, ArrayList<Value>> targetMap, Key key, Value value){
            ArrayList<Value> existingList = targetMap.get(key);
            if(existingList == null){
                existingList = new ArrayList<>();
                targetMap.put(key, existingList);
            }
            existingList.add(value);
        }
    }

    public static class AuthenticationService extends AbstractAppService implements IAuthentication {

        private final IUser _user;

        public AuthenticationService(RestaurantDataService.IUser userData){
            this._user = userData;
        }

        // returns the matching User if username and password are correct; otherwise returns null
        @Override
        public RestaurantModel.User login(String username, String password){
            RestaurantModel.User user = _user.findByUsername(username);
            if(user != null && user.getUserPassword().equals(password)){
                return user;
            }
            return null;
        }
    }

    public static class PermissionService extends AbstractAppService implements IPermission {

        private final IUser userStorage;

        public PermissionService(IUser user){
            this.userStorage = user;
        }

        @Override
        public boolean canAccessOrder(RestaurantModel.Role role){
            return role == RestaurantModel.Role.CASHIER;
        }

        @Override
        public boolean canAccessInventory(RestaurantModel.Role role){
            return role == RestaurantModel.Role.MANAGER;
        }

        @Override
        public boolean canAccessPayment(RestaurantModel.Role role){
            return role == RestaurantModel.Role.CASHIER;
        }

        @Override
        public boolean canAccessReports(RestaurantModel.Role role){
            return role == RestaurantModel.Role.MANAGER;
        }

        @Override
        public boolean canAccessFoodWaste(RestaurantModel.Role role){
            return role == RestaurantModel.Role.MANAGER;
        }

        @Override
        public boolean canAccessSettings(RestaurantModel.Role role){
            return role == RestaurantModel.Role.MANAGER;
        }
    }

    public static class InventoryService extends AbstractAppService {

        private final IInventoryItem _inventoryItem;

        public InventoryService(IInventoryItem inventoryItem){
            this._inventoryItem = inventoryItem;
        }

        public int getLowStockCount(){
            return _inventoryItem.findLowStock().size();
        }

        // counts days from today until expiry
        public long getDaysUntilExpiry(int inventoryIdentifier){
            InventoryItem existingItem = _inventoryItem.get(inventoryIdentifier);
            if(existingItem == null || existingItem.getInventoryExpiryDate() == null){
                return Long.MAX_VALUE;
            }

            LocalDate today = LocalDate.now();
            LocalDate expiry = existingItem.getInventoryExpiryDate();
            long days = 0;

            while(today.isBefore(expiry)){
                today = today.plusDays(1);
                days++;
            }
            return days;
        }

        public ArrayList<InventoryItem> getAllInventoryItems(){
            return new ArrayList<>(_inventoryItem.getAll());
        }

        public ArrayList<InventoryItem> getLowStockItems(){
            return new ArrayList<>(_inventoryItem.findLowStock());
        }

        public ArrayList<InventoryItem> getItemsExpiringSoon(int daysThreshold){
            return new ArrayList<>(_inventoryItem.findExpiringSoon(daysThreshold));
        }

        public ArrayList<InventoryItem> getItemsByCategory(String targetCategory){
            return new ArrayList<>(_inventoryItem.findByCategory(targetCategory));
        }

        // returns items at or below reorder level, it's sorted by quantity ascending using selection sort algo
        public ArrayList<InventoryItem> getItemsNeedingReorder(){
            ArrayList<InventoryItem> itemsNeedingReorder = new ArrayList<>();
            for(InventoryItem item : _inventoryItem.getAll()){
                if(item.getInventoryQuantity() <= item.getInventoryReorderLevel()){
                    itemsNeedingReorder.add(item);
                }
            }

            for(int i = 0; i < itemsNeedingReorder.size() - 1; i++){
                int minIndex = i;
                for(int j = i + 1; j < itemsNeedingReorder.size(); j++){
                    if(itemsNeedingReorder.get(j).getInventoryQuantity() < itemsNeedingReorder.get(minIndex).getInventoryQuantity()){
                        minIndex = j;
                    }
                }
                InventoryItem temp = itemsNeedingReorder.get(i);
                itemsNeedingReorder.set(i, itemsNeedingReorder.get(minIndex));
                itemsNeedingReorder.set(minIndex, temp);
            }
            return itemsNeedingReorder;
        }

        // validates all fields and set restock date to today, then creates the item
        public InventoryItem addInventoryItem(String itemName, String category, int quantity, String unit, double costPerUnit, int reorderLevel, String supplier, LocalDate expiryDate) throws ValidationException{
            ensureNotEmpty(itemName, "Item name");
            ensureNotEmpty(category, "Category");
            ensureNotNegative(quantity, "Quantity");
            ensureNotEmpty(unit, "Unit");
            ensureNotNegative(costPerUnit, "Cost per unit");
            ensureNotNegative(reorderLevel, "Reorder level");
            ensureNotEmpty(supplier, "Supplier");

            InventoryItem newItem = new InventoryItem();
            newItem.setInventoryItemName(itemName);
            newItem.setInventoryCategory(category);
            newItem.setInventoryQuantity(quantity);
            newItem.setInventoryUnit(unit);
            newItem.setInventoryCostPerUnit(costPerUnit);
            newItem.setInventoryReorderLevel(reorderLevel);
            newItem.setInventorySupplier(supplier);
            newItem.setInventoryLastRestockedDate(LocalDate.now());
            newItem.setInventoryExpiryDate(expiryDate);

            _inventoryItem.create(newItem);
            return newItem;
        }

        public void updateQuantity(int inventoryId, int newQuantity) throws ValidationException{
            InventoryItem existingItem = getInventoryItemOrThrow(inventoryId);
            ensureNotNegative(newQuantity, "New quantity");
            existingItem.setInventoryQuantity(newQuantity);
            _inventoryItem.update(existingItem);
        }

        public void updateReorderLevel(int inventoryId, int newReorderLevel) throws ValidationException{
            InventoryItem existingItem = getInventoryItemOrThrow(inventoryId);
            ensureNotNegative(newReorderLevel, "Reorder level");
            existingItem.setInventoryReorderLevel(newReorderLevel);
            _inventoryItem.update(existingItem);
        }

        public void deleteInventoryItem(int inventoryId) throws ValidationException{
            getInventoryItemOrThrow(inventoryId);
            _inventoryItem.delete(inventoryId);
        }

        // validates stock is sufficient before minus consumed quantity
        public void consumeInventory(int inventoryId, int quantityToConsume) throws ValidationException, InsufficientInventoryException{
            InventoryItem existingItem = getInventoryItemOrThrow(inventoryId);
            ensureNotNegative(quantityToConsume, "Quantity used");

            if(existingItem.getInventoryQuantity() < quantityToConsume){
                throw new InsufficientInventoryException("Insufficient inventory for " + existingItem.getInventoryItemName() + ". Available: " + existingItem.getInventoryQuantity() + ", Required: " + quantityToConsume);
            }
            existingItem.setInventoryQuantity(existingItem.getInventoryQuantity() - quantityToConsume);
            _inventoryItem.update(existingItem);
        }

        // add quantity to existing stock and updates last restocked date to today
        public void restockInventory(int inventoryId, int quantityToAdd) throws ValidationException{
            InventoryItem existingItem = getInventoryItemOrThrow(inventoryId);
            ensurePositive(quantityToAdd, "Quantity added");

            existingItem.setInventoryQuantity(existingItem.getInventoryQuantity() + quantityToAdd);
            existingItem.setInventoryLastRestockedDate(LocalDate.now());
            _inventoryItem.update(existingItem);
        }

        public boolean isLowStock(int inventoryId){
            InventoryItem existingItem = _inventoryItem.get(inventoryId);
            if(existingItem == null){
                return false;
            }
            return existingItem.getInventoryReorderLevel() > existingItem.getInventoryQuantity();
        }

        public boolean isExpiringSoon(int inventoryId, int daysThreshold){
            InventoryItem existingItem = _inventoryItem.get(inventoryId);
            if(existingItem == null || existingItem.getInventoryExpiryDate() == null){
                return false;
            }
            LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
            return !existingItem.getInventoryExpiryDate().isAfter(thresholdDate);
        }

        public double calculateItemValue(InventoryItem inventoryItem){
            return inventoryItem.getInventoryQuantity() * inventoryItem.getInventoryCostPerUnit();
        }

        public double calculateTotalInventoryValue(){
            double totalInventoryValue = 0.0;
            for(InventoryItem currentItem : _inventoryItem.getAll()){
                totalInventoryValue += calculateItemValue(currentItem);
            }
            return totalInventoryValue;
        }

        public HashMap<String, ArrayList<InventoryItem>> groupByCategory(ArrayList<InventoryItem> inventoryItemList){
            HashMap<String, ArrayList<InventoryItem>> itemsGroupedByCategory = new HashMap<>();
            for(InventoryItem currentItem : inventoryItemList){
                putIntoGroupedMap(itemsGroupedByCategory, currentItem.getInventoryCategory(), currentItem);
            }
            return itemsGroupedByCategory;
        }

        private InventoryItem getInventoryItemOrThrow(int inventoryId) throws ValidationException{
            return getOrThrow(_inventoryItem.get(inventoryId), "Inventory item not found");
        }
    }

    public static class OrderService extends AbstractAppService {

        private final IOrder _order;
        private final IMenuItem _menuItem;

        public OrderService(IOrder order, IMenuItem menuItem){
            this._order = order;
            this._menuItem = menuItem;
        }

        public long getActiveOrderCount(){
            long activeOrderCounter = 0;
            for(Order currentOrder : _order.getAll()){
                if(!OrderStatus.COMPLETED.equals(currentOrder.getOrderStatus())
                    && !OrderStatus.CANCELLED.equals(currentOrder.getOrderStatus())){
                    activeOrderCounter++;
                }
            }
            return activeOrderCounter;
        }

        public int getTotalItemCount(Order restaurantOrder){
            if(restaurantOrder.getOrderItems() == null){
                return 0;
            }
            int totalItemQuantity = 0;
            for(OrderItem orderItem : restaurantOrder.getOrderItems()){
                totalItemQuantity += orderItem.getOrderItemQuantity();
            }
            return totalItemQuantity;
        }

        public int getTotalCompletedOrders(){
            return _order.findByStatus(OrderStatus.COMPLETED).size();
        }

        public ArrayList<Order> getAllOrders(){
            return new ArrayList<>(_order.getAll());
        }

        public ArrayList<Order> getOrdersByStatus(String targetStatus){
            return new ArrayList<>(_order.findByStatus(targetStatus));
        }

        public ArrayList<Order> getOrdersByTable(String targetTableNumber){
            return new ArrayList<>(_order.findByTable(targetTableNumber));
        }

        public ArrayList<MenuItem> getAvailableMenuItems(){
            return new ArrayList<>(_menuItem.findAvailable());
        }

        public Order createOrder(String tableNumber, ArrayList<OrderItem> orderItems) throws ValidationException{
            ensureNotEmpty(tableNumber, "Table number");
            ensureNotNullNotEmpty(orderItems, "Order items");

            for(OrderItem orderItem : orderItems){
                MenuItem requestedMenuItem = orderItem.getLinkedMenuItem();
                ensureMenuItemAvailable(requestedMenuItem, requestedMenuItem.getMenuItemId());
                ensurePositive(orderItem.getOrderItemQuantity(), "Item quantity");
            }

            double calculatedTotalAmount = calculateOrderTotal(orderItems);
            Order newOrder = new Order();
            newOrder.setOrderTableNumber(tableNumber);
            newOrder.setOrderItems(new ArrayList<>(orderItems));
            newOrder.setOrderStatus(OrderStatus.PREPARING);
            newOrder.setOrderCreatedTime(LocalDateTime.now());
            newOrder.setOrderTotalAmount(calculatedTotalAmount);
            _order.create(newOrder);
            return newOrder;
        }

        public double calculateOrderTotal(ArrayList<OrderItem> orderItems){
            double calculatedTotal = 0.0;
            for(OrderItem orderItem : orderItems){
                MenuItem currentItem = orderItem.getLinkedMenuItem();
                if(currentItem != null){
                    calculatedTotal += currentItem.getMenuItemPrice() * orderItem.getOrderItemQuantity();
                }
            }
            return calculatedTotal;
        }

        public double calculateOrderRevenue(){
            double accumulatedRevenue = 0.0;
            for(Order completedOrder : _order.findByStatus(OrderStatus.COMPLETED)){
                accumulatedRevenue += completedOrder.getOrderTotalAmount();
            }
            return accumulatedRevenue;
        }

        public void updateOrderStatus(int orderIdentifier, String newOrderStatus) throws ValidationException{
            Order existingOrder = getOrderOrThrow(orderIdentifier);
            if(!isValidStatusTransition(existingOrder.getOrderStatus(), newOrderStatus)){
                throw new ValidationException("Invalid status transition from " + existingOrder.getOrderStatus() + " to " + newOrderStatus);
            }
            existingOrder.setOrderStatus(newOrderStatus);
            _order.update(existingOrder);
        }

        public void cancelOrder(int orderIdentifier) throws ValidationException{
            Order existingOrder = getOrderOrThrow(orderIdentifier);
            if(existingOrder.getOrderStatus().equals(OrderStatus.COMPLETED)){
                throw new ValidationException("Cannot cancel completed order");
            }
            existingOrder.setOrderStatus(OrderStatus.CANCELLED);
            _order.update(existingOrder);
        }

        private boolean isValidStatusTransition(String currentOrderStatus, String proposedNewStatus){
            if(currentOrderStatus.equals(OrderStatus.PREPARING)){
                return proposedNewStatus.equals(OrderStatus.READY) || proposedNewStatus.equals(OrderStatus.CANCELLED);
            }else if(currentOrderStatus.equals(OrderStatus.READY)){
                return proposedNewStatus.equals(OrderStatus.SERVED) || proposedNewStatus.equals(OrderStatus.CANCELLED);
            }else if(currentOrderStatus.equals(OrderStatus.SERVED)){
                return proposedNewStatus.equals(OrderStatus.COMPLETED);
            }
            return false;
        }

        private void ensureMenuItemAvailable(MenuItem menuItemToValidate, int itemIdentifier) throws ValidationException{
            if(menuItemToValidate == null){
                throw new ValidationException("Menu item with ID " + itemIdentifier + " does not exist");
            }
            if(!menuItemToValidate.isAvailable()){
                throw new ValidationException("Menu item " + menuItemToValidate.getMenuItemName() + " is not available");
            }
        }

        public HashMap<String, ArrayList<Order>> groupByStatus(ArrayList<Order> orderList){
            HashMap<String, ArrayList<Order>> ordersGroupedByStatus = new HashMap<>();
            for(Order currentOrder : orderList){
                putIntoGroupedMap(ordersGroupedByStatus, currentOrder.getOrderStatus(), currentOrder);
            }
            return ordersGroupedByStatus;
        }

        public HashMap<String, ArrayList<Order>> groupByTable(ArrayList<Order> orderList){
            HashMap<String, ArrayList<Order>> ordersGroupedByTable = new HashMap<>();
            for(Order currentOrder : orderList){
                putIntoGroupedMap(ordersGroupedByTable, currentOrder.getOrderTableNumber(), currentOrder);
            }
            return ordersGroupedByTable;
        }

        private Order getOrderOrThrow(int orderIdentifier) throws ValidationException{
            return getOrThrow(_order.get(orderIdentifier), "Order not found");
        }
    }

    public static class PaymentService extends AbstractAppService {

        private final IPayment _payment;
        private final IOrder _order;

        public PaymentService(IPayment payment, IOrder order){
            this._payment = payment;
            this._order = order;
        }

        public double getTotalWithTip(Payment paymentRecord){
            return paymentRecord.getPaymentAmount() + paymentRecord.getPaymentTipAmount();
        }

        public long getTransactionCount(){
            long completedTransactionCount = 0;
            for(Payment currentPayment : _payment.getAll()){
                if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                    completedTransactionCount++;
                }
            }
            return completedTransactionCount;
        }

        public HashMap<String, Long> getCountByMethod(){
            HashMap<String, Long> paymentCountByMethod = new HashMap<>();
            for(Payment currentPayment : _payment.getAll()){
                String paymentMethod = currentPayment.getPaymentMethod();
                Long currentCount = paymentCountByMethod.get(paymentMethod);
                if(currentCount == null){
                    paymentCountByMethod.put(paymentMethod, 1L);
                }else{
                    paymentCountByMethod.put(paymentMethod, currentCount + 1);
                }
            }
            return paymentCountByMethod;
        }

        public int getTransactionCountAsInt(){
            return (int) getTransactionCount();
        }

        public ArrayList<Payment> getAllPayments(){
            return new ArrayList<>(_payment.getAll());
        }

        public ArrayList<Payment> getPaymentsByStatus(String targetStatus){
            return new ArrayList<>(_payment.findByStatus(targetStatus));
        }

        public ArrayList<Payment> getPendingPayments(){
            return new ArrayList<>(_payment.findByStatus(PaymentStatus.PENDING));
        }

        public Payment processPayment(int orderId, double tipAmount, String paymentMethod) throws ValidationException, PaymentFailedException{
            Order existingOrder = getOrderOrThrow(orderId);
            if(!existingOrder.getOrderStatus().equals(OrderStatus.SERVED) && !existingOrder.getOrderStatus().equals(OrderStatus.READY)){
                throw new ValidationException("Order must be served before payment");
            }else if(hasCompletedPayment(orderId)){
                throw new ValidationException("Order already paid");
            }else if(!isValidPaymentMethod(paymentMethod)){
                throw new ValidationException("Invalid payment method: " + paymentMethod);
            }

            ensureNotNegative(tipAmount, "Tip amount");

            Payment newRecord = new Payment();
            newRecord.setLinkedOrderId(orderId);
            newRecord.setPaymentAmount(existingOrder.getOrderTotalAmount());
            newRecord.setPaymentTipAmount(tipAmount);
            newRecord.setPaymentMethod(paymentMethod);
            newRecord.setPaymentTimestamp(LocalDateTime.now());
            newRecord.setPaymentTransactionId(generateTransactionId());

            try{
                boolean paymentSuccessful = simulatePaymentProcessing(paymentMethod);
                if(paymentSuccessful){
                    newRecord.setPaymentStatus(PaymentStatus.COMPLETED);
                    existingOrder.setOrderStatus(OrderStatus.COMPLETED);
                    _order.update(existingOrder);
                }else{
                    newRecord.setPaymentStatus(PaymentStatus.FAILED);
                    throw new PaymentFailedException("Payment processing failed");
                }
            }catch(PaymentFailedException paymentException){
                newRecord.setPaymentStatus(PaymentStatus.FAILED);
                _payment.create(newRecord);
                throw new PaymentFailedException("Payment processing error: " + paymentException.getMessage(), paymentException);
            }
            _payment.create(newRecord);
            return newRecord;
        }

        public void refundPayment(int paymentIdentifier) throws ValidationException{
            Payment existingPaymentRecord = getPaymentOrThrow(paymentIdentifier);

            if(!PaymentStatus.COMPLETED.equals(existingPaymentRecord.getPaymentStatus())){
                throw new ValidationException("Can only refund completed payments");
            }
            existingPaymentRecord.setPaymentStatus(PaymentStatus.REFUNDED);
            _payment.update(existingPaymentRecord);

            Order relatedOrder = _order.get(existingPaymentRecord.getLinkedOrderId());
            if(relatedOrder != null){
                relatedOrder.setOrderStatus(OrderStatus.SERVED);
                _order.update(relatedOrder);
            }
        }

        private boolean hasCompletedPayment(int orderIdentifier){
            for(Payment currentPayment : _payment.findByOrderId(orderIdentifier)){
                if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                    return true;
                }
            }
            return false;
        }

        private boolean isValidPaymentMethod(String methodToValidate){
            if(methodToValidate == null){
                return false;
            }
            String lowerCaseMethod = methodToValidate.toLowerCase();
            return lowerCaseMethod.equals(PaymentMethod.CASH) || lowerCaseMethod.equals(PaymentMethod.CARD) || lowerCaseMethod.equals(PaymentMethod.GCASH) || lowerCaseMethod.equals(PaymentMethod.MAYA);
        }

        private boolean simulatePaymentProcessing(String paymentMethod){
            return Math.random() < 0.95;
        }

        private String generateTransactionId(){
            String date = LocalDate.now().toString().replace("-", "");
            int random = (int) (Math.random() * 10000);
            return "RMS-TXN-" + date + "-" + random;
        }

        public double calculateDailyRevenue(LocalDate targetDate){
            double dailyRevenueTotal = 0.0;
            for(Payment currentPayment : _payment.findByDate(targetDate)){
                if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                    dailyRevenueTotal += currentPayment.getPaymentAmount();
                }
            }
            return dailyRevenueTotal;
        }

        public double calculateDailyTips(LocalDate targetDate){
            double dailyTipsTotal = 0.0;
            for(Payment currentPayment : _payment.findByDate(targetDate)){
                if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                    dailyTipsTotal += currentPayment.getPaymentTipAmount();
                }
            }
            return dailyTipsTotal;
        }

        public double calculateAverageTransaction(){
            ArrayList<Payment> completedPaymentList = new ArrayList<>(_payment.findByStatus(PaymentStatus.COMPLETED));
            if(completedPaymentList.isEmpty()){
                return 0.0;
            }
            double accumulatedTotal = 0.0;
            for(Payment currentPayment : completedPaymentList){
                accumulatedTotal += currentPayment.getPaymentAmount();
            }
            return accumulatedTotal / completedPaymentList.size();
        }

        public double calculateTotalRevenue(){
            double accumulatedRevenue = 0.0;
            for(Payment completedPayment : _payment.findByStatus(PaymentStatus.COMPLETED)){
                accumulatedRevenue += completedPayment.getPaymentAmount();
            }
            return accumulatedRevenue;
        }

        public double calculateTotalTips(){
            double accumulatedTips = 0.0;
            for(Payment completedPayment : _payment.findByStatus(PaymentStatus.COMPLETED)){
                accumulatedTips += completedPayment.getPaymentTipAmount();
            }
            return accumulatedTips;
        }

        public HashMap<String, ArrayList<Payment>> groupByPaymentMethod(ArrayList<Payment> paymentList){
            HashMap<String, ArrayList<Payment>> paymentsGroupedByMethod = new HashMap<>();
            for(Payment currentPayment : paymentList){
                putIntoGroupedMap(paymentsGroupedByMethod, currentPayment.getPaymentMethod(), currentPayment);
            }
            return paymentsGroupedByMethod;
        }

        public HashMap<String, ArrayList<Payment>> groupByStatus(ArrayList<Payment> paymentList){
            HashMap<String, ArrayList<Payment>> paymentsGroupedByStatus = new HashMap<>();
            for(Payment currentPayment : paymentList){
                putIntoGroupedMap(paymentsGroupedByStatus, currentPayment.getPaymentStatus(), currentPayment);
            }
            return paymentsGroupedByStatus;
        }

        private Order getOrderOrThrow(int orderId) throws ValidationException{
            return getOrThrow(_order.get(orderId), "Order not found");
        }

        private Payment getPaymentOrThrow(int paymentId) throws ValidationException{
            return getOrThrow(_payment.get(paymentId), "Payment not found");
        }
    }

    public static class ReportService extends AbstractAppService {

        private final IOrder _order;
        private final IPayment _payment;
        private final IInventoryItem _inventoryItem;
        private final IFoodWaste _foodWaste;

        private static final int EXPIRY_THRESHOLD_DAYS = 7;
        private static final String TOTAL_REVENUE = "totalRevenue";
        private static final String TOTAL_TIPS = "totalTips";
        private static final String TRANSACTION_COUNT = "transactionCount";

        public ReportService(IOrder order, IPayment payment, IInventoryItem inventoryItem, IFoodWaste foodWaste){
            this._order = order;
            this._payment = payment;
            this._inventoryItem = inventoryItem;
            this._foodWaste = foodWaste;
        }

        // report completed payments for the date in revenue, tips, count, average, and method breakdown
        public HashMap<String, Object> dailySalesReport(LocalDate reportDate){
            HashMap<String, Object> reportData = new HashMap<>();
            HashMap<String, Long> paymentMethodBreakdown = new HashMap<>();

            double totalRevenue = 0.0;
            double totalTips = 0.0;
            int transactionCount = 0;

            for(Payment currentPayment : _payment.findByDate(reportDate)){
                if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                    transactionCount++;
                    totalRevenue += currentPayment.getPaymentAmount();
                    totalTips += currentPayment.getPaymentTipAmount();

                    String paymentMethod = currentPayment.getPaymentMethod();
                    Long methodCount = paymentMethodBreakdown.get(paymentMethod);
                    if(methodCount == null){
                        paymentMethodBreakdown.put(paymentMethod, 1L);
                    }else{
                        paymentMethodBreakdown.put(paymentMethod, methodCount + 1);
                    }
                }
            }
            double averageTransaction = transactionCount > 0 ? totalRevenue / transactionCount : 0.0;
            reportData.put("date", reportDate);
            reportData.put(TOTAL_REVENUE, totalRevenue);
            reportData.put(TOTAL_TIPS, totalTips);
            reportData.put(TRANSACTION_COUNT, transactionCount);
            reportData.put("averageTransaction", averageTransaction);
            reportData.put("paymentMethodBreakdown", paymentMethodBreakdown);
            return reportData;
        }

        // report total items, low stock count, expiring soon count, total value
        public HashMap<String, Object> inventoryReport(){
            HashMap<String, Object> reportData = new HashMap<>();
            HashMap<String, Long> categoryCountBreakdown = new HashMap<>();

            ArrayList<InventoryItem> allInventoryItems = _inventoryItem.getAll();
            ArrayList<InventoryItem> lowStockInventoryItems = _inventoryItem.findLowStock();
            ArrayList<InventoryItem> expiringSoonInventoryItems = _inventoryItem.findExpiringSoon(EXPIRY_THRESHOLD_DAYS);

            double totalInventoryValue = 0.0;
            for(InventoryItem currentInventoryItem : allInventoryItems){
                totalInventoryValue += currentInventoryItem.getInventoryQuantity() * currentInventoryItem.getInventoryCostPerUnit();

                String itemCategory = currentInventoryItem.getInventoryCategory();
                Long categoryCount = categoryCountBreakdown.get(itemCategory);
                if(categoryCount == null){
                    categoryCountBreakdown.put(itemCategory, 1L);
                }else{
                    categoryCountBreakdown.put(itemCategory, categoryCount + 1);
                }
            }
            reportData.put("totalItems", allInventoryItems.size());
            reportData.put("lowStockCount", lowStockInventoryItems.size());
            reportData.put("expiringSoonCount", expiringSoonInventoryItems.size());
            reportData.put("totalInventoryValue", totalInventoryValue);
            reportData.put("categoryBreakdown", categoryCountBreakdown);
            reportData.put("lowStockItems", lowStockInventoryItems);
            reportData.put("expiringSoonItems", expiringSoonInventoryItems);
            return reportData;
        }

        // report waste records in date range with cost totals, reason counts, and category costs
        public HashMap<String, Object> wasteReport(LocalDate reportStartDate, LocalDate reportEndDate){
            HashMap<String, Object> reportData = new HashMap<>();
            HashMap<String, Long> wasteReasonBreakdown = new HashMap<>();
            HashMap<String, Double> wasteCategoryBreakdown = new HashMap<>();

            double totalWasteCost = 0.0;
            ArrayList<FoodWaste> wasteRecordsInRange = _foodWaste.findByDateRange(reportStartDate, reportEndDate);

            for(FoodWaste currentWasteRecord : wasteRecordsInRange){
                totalWasteCost += currentWasteRecord.getFoodWasteEstimatedCost();

                String wasteReason = currentWasteRecord.getFoodWasteReason();
                Long reasonCount = wasteReasonBreakdown.get(wasteReason);
                if(reasonCount == null){
                    wasteReasonBreakdown.put(wasteReason, 1L);
                }else{
                    wasteReasonBreakdown.put(wasteReason, reasonCount + 1);
                }

                String wasteCategory = currentWasteRecord.getFoodWasteCategory();
                Double categoryCost = wasteCategoryBreakdown.get(wasteCategory);
                if(categoryCost == null){
                    wasteCategoryBreakdown.put(wasteCategory, currentWasteRecord.getFoodWasteEstimatedCost());
                }else{
                    wasteCategoryBreakdown.put(wasteCategory, categoryCost + currentWasteRecord.getFoodWasteEstimatedCost());
                }
            }
            reportData.put("startDate", reportStartDate);
            reportData.put("endDate", reportEndDate);
            reportData.put("totalWasteRecords", wasteRecordsInRange.size());
            reportData.put("totalWasteCost", totalWasteCost);
            reportData.put("reasonBreakdown", wasteReasonBreakdown);
            reportData.put("categoryBreakdown", wasteCategoryBreakdown);
            return reportData;
        }

        // report all orders for status counts, completed count, total revenue, and average order value
        public HashMap<String, Object> orderReport(){
            HashMap<String, Object> reportData = new HashMap<>();
            HashMap<String, Long> orderStatusBreakdown = new HashMap<>();
            ArrayList<Order> allRestaurantOrders = _order.getAll();

            for(Order currentOrder : allRestaurantOrders){
                String orderStatus = currentOrder.getOrderStatus();
                Long statusCount = orderStatusBreakdown.get(orderStatus);
                if(statusCount == null){
                    orderStatusBreakdown.put(orderStatus, 1L);
                }else{
                    orderStatusBreakdown.put(orderStatus, statusCount + 1);
                }
            }

            ArrayList<Order> completedRestaurantOrders = _order.findByStatus(OrderStatus.COMPLETED);
            double totalCompletedRevenue = 0.0;
            for(Order completedOrder : completedRestaurantOrders){
                totalCompletedRevenue += completedOrder.getOrderTotalAmount();
            }

            double averageOrderValue = completedRestaurantOrders.isEmpty() ? 0.0 : totalCompletedRevenue / completedRestaurantOrders.size();
            reportData.put("totalOrders", allRestaurantOrders.size());
            reportData.put("completedOrders", completedRestaurantOrders.size());
            reportData.put("totalRevenue", totalCompletedRevenue);
            reportData.put("averageOrderValue", averageOrderValue);
            reportData.put("statusBreakdown", orderStatusBreakdown);
            return reportData;
        }

        // returns summary for active orders, completed orders, total revenue, low stock alerts
        public HashMap<String, Object> dashboardSummary(){
            HashMap<String, Object> summaryData = new HashMap<>();
            ArrayList<Order> allRestaurantOrders = _order.getAll();

            long activeOrderCount = 0;
            long completedOrderCount = 0;
            double totalRevenueFromCompletedOrders = 0.0;

            for(Order currentOrder : allRestaurantOrders){
                String orderStatus = currentOrder.getOrderStatus();
                if(!OrderStatus.COMPLETED.equals(orderStatus)
                    && !OrderStatus.CANCELLED.equals(orderStatus)){
                    activeOrderCount++;
                }
                if(OrderStatus.COMPLETED.equals(orderStatus)){
                    completedOrderCount++;
                    totalRevenueFromCompletedOrders += currentOrder.getOrderTotalAmount();
                }
            }
            int lowStockAlertCount = _inventoryItem.findLowStock().size();
            summaryData.put("totalRevenue", totalRevenueFromCompletedOrders);
            summaryData.put("activeOrders", activeOrderCount);
            summaryData.put("totalOrders", completedOrderCount);
            summaryData.put("lowStockAlerts", lowStockAlertCount);
            return summaryData;
        }

        // combine daily sales and waste reports for the entire month
        public HashMap<String, Object> monthlyReport(int reportYear, int reportMonth){
            LocalDate monthStartDate = LocalDate.of(reportYear, reportMonth, 1);
            LocalDate monthEndDate = monthStartDate.withDayOfMonth(monthStartDate.lengthOfMonth());

            HashMap<String, Object> reportData = new HashMap<>();
            reportData.put("year", reportYear);
            reportData.put("month", reportMonth);
            reportData.put("salesData", dailySalesReport(monthStartDate));
            reportData.put("wasteData", wasteReport(monthStartDate, monthEndDate));
            return reportData;
        }
    }

    public static class FoodWasteService extends AbstractAppService {

        private final IFoodWaste _foodWaste;

        public FoodWasteService(IFoodWaste foodWaste){
            this._foodWaste = foodWaste;
        }

        public FoodWaste recordWaste(String itemName, double quantity, String unit, String reason, double estimatedCost, String recordedBy, String category) throws ValidationException{
            ensureNotEmpty(itemName, "Item name");
            ensurePositive(quantity, "Quantity");
            ensureNotEmpty(unit, "Unit");
            ensureNotEmpty(reason, "Reason");
            ensureNotNegative(estimatedCost, "Estimated cost");
            ensureNotEmpty(recordedBy, "Recorded by");
            ensureNotEmpty(category, "Category");

            FoodWaste newRecord = new FoodWaste();
            newRecord.setFoodWasteItemName(itemName);
            newRecord.setFoodWasteQuantity(quantity);
            newRecord.setFoodWasteUnit(unit);
            newRecord.setFoodWasteReason(reason);
            newRecord.setFoodWasteEstimatedCost(estimatedCost);
            newRecord.setFoodWasteRecordedDate(LocalDateTime.now());
            newRecord.setFoodWasteRecordedBy(recordedBy);
            newRecord.setFoodWasteCategory(category);

            _foodWaste.create(newRecord);
            return newRecord;
        }

        public void updateWaste(int wasteId, String itemName, double quantity, String unit, String reason, double estimatedCost, String category) throws ValidationException{
            FoodWaste existingRecord = getWasteOrThrow(wasteId);

            if(itemName != null && !itemName.trim().isEmpty()){
                existingRecord.setFoodWasteItemName(itemName);
            }else if(unit != null && !unit.trim().isEmpty()){
                existingRecord.setFoodWasteUnit(unit);
            }else if(reason != null && !reason.trim().isEmpty()){
                existingRecord.setFoodWasteReason(reason);
            }else if(category != null && !category.trim().isEmpty()){
                existingRecord.setFoodWasteCategory(category);
            }

            if(quantity > 0){
                existingRecord.setFoodWasteQuantity(quantity);
            }else if(estimatedCost >= 0){
                existingRecord.setFoodWasteEstimatedCost(estimatedCost);
            }
            _foodWaste.update(existingRecord);
        }

        public void deleteWaste(int wasteId) throws ValidationException{
            getWasteOrThrow(wasteId);
            _foodWaste.delete(wasteId);
        }

        // estimated cost of waste records within the date range
        public double calculateTotalWasteCost(LocalDate startDate, LocalDate endDate)
            throws ValidationException{
            ensureDateRange(startDate, endDate);
            double accumulatedWasteCost = 0.0;
            for(FoodWaste currentRecord : _foodWaste.findByDateRange(startDate, endDate)){
                accumulatedWasteCost += currentRecord.getFoodWasteEstimatedCost();
            }
            return accumulatedWasteCost;
        }

        // estimated cost of waste recorded today
        public double calculateDailyWasteCost(){
            LocalDate today = LocalDate.now();
            double dailyWasteCost = 0.0;
            for(FoodWaste currentWasteRecord : _foodWaste.getAll()){
                if(currentWasteRecord.getFoodWasteRecordedDate().toLocalDate().equals(today)){
                    dailyWasteCost += currentWasteRecord.getFoodWasteEstimatedCost();
                }
            }
            return dailyWasteCost;
        }

        // estimated cost of waste for the given year and month
        public double calculateMonthlyWasteCost(int year, int month){
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

            double monthlyCost = 0.0;
            for(FoodWaste currentRecord : _foodWaste.findByDateRange(startOfMonth, endOfMonth)){
                monthlyCost += currentRecord.getFoodWasteEstimatedCost();
            }
            return monthlyCost;
        }

        public ArrayList<FoodWaste> getAllWasteRecords(){
            return new ArrayList<>(_foodWaste.getAll());
        }

        public ArrayList<FoodWaste> getWasteByDateRange(LocalDate startDate, LocalDate endDate)
            throws ValidationException{
            ensureDateRange(startDate, endDate);
            return new ArrayList<>(_foodWaste.findByDateRange(startDate, endDate));
        }

        public ArrayList<FoodWaste> getWasteByReason(String targetReason){
            return new ArrayList<>(_foodWaste.findByReason(targetReason));
        }

        public ArrayList<FoodWaste> getWasteByCategory(String targetCategory){
            return new ArrayList<>(_foodWaste.findByCategory(targetCategory));
        }

        // return hashmap of waste reason count
        public HashMap<String, Long> getWasteCountByReason(){
            HashMap<String, Long> wasteCountByReason = new HashMap<>();
            for(FoodWaste currentRecord : _foodWaste.getAll()){
                String wasteReason = currentRecord.getFoodWasteReason();
                Long currentCount = wasteCountByReason.get(wasteReason);
                if(currentCount == null){
                    wasteCountByReason.put(wasteReason, 1L);
                }else{
                    wasteCountByReason.put(wasteReason, currentCount + 1);
                }
            }
            return wasteCountByReason;
        }

        // return hashmap of category to total estimated cost
        public HashMap<String, Double> getWasteCostByCategory(){
            HashMap<String, Double> wasteCostByCategory = new HashMap<>();
            for(FoodWaste currentRecord : _foodWaste.getAll()){
                String wasteCategory = currentRecord.getFoodWasteCategory();
                Double accumulatedCost = wasteCostByCategory.get(wasteCategory);
                if(accumulatedCost == null){
                    wasteCostByCategory.put(wasteCategory, currentRecord.getFoodWasteEstimatedCost());
                }else{
                    wasteCostByCategory.put(wasteCategory, accumulatedCost + currentRecord.getFoodWasteEstimatedCost());
                }
            }
            return wasteCostByCategory;
        }

        // return hashmap of an item name to total wasted quantity
        public HashMap<String, Double> getQuantityByItem(){
            HashMap<String, Double> quantityByItemName = new HashMap<>();
            for(FoodWaste currentRecord : _foodWaste.getAll()){
                String wastedItemName = currentRecord.getFoodWasteItemName();
                Double accumulatedQuantity = quantityByItemName.get(wastedItemName);
                if(accumulatedQuantity == null){
                    quantityByItemName.put(wastedItemName, currentRecord.getFoodWasteQuantity());
                }else{
                    quantityByItemName.put(wastedItemName, accumulatedQuantity + currentRecord.getFoodWasteQuantity());
                }
            }
            return quantityByItemName;
        }

        // group waste records into a hashmap, key by reason
        public HashMap<String, ArrayList<FoodWaste>> groupByReason(ArrayList<FoodWaste> wasteRecordList){
            HashMap<String, ArrayList<FoodWaste>> wasteRecordsGroupedByReason = new HashMap<>();
            for(FoodWaste currentRecord : wasteRecordList){
                putIntoGroupedMap(wasteRecordsGroupedByReason, currentRecord.getFoodWasteReason(), currentRecord);
            }
            return wasteRecordsGroupedByReason;
        }

        // group waste records into a hashmap, key by category
        public HashMap<String, ArrayList<FoodWaste>> groupByCategory(ArrayList<FoodWaste> wasteRecordList){
            HashMap<String, ArrayList<FoodWaste>> wasteRecordsGroupedByCategory = new HashMap<>();
            for(FoodWaste currentRecord : wasteRecordList){
                putIntoGroupedMap(wasteRecordsGroupedByCategory, currentRecord.getFoodWasteCategory(), currentRecord);
            }
            return wasteRecordsGroupedByCategory;
        }

        private FoodWaste getWasteOrThrow(int wasteIdentifier) throws ValidationException{
            return getOrThrow(_foodWaste.get(wasteIdentifier), "Waste record not found");
        }
    }

}
