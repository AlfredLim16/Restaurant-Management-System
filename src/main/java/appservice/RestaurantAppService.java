
package appservice;

import dataservice.RestaurantDataService.IMenuItem;
import dataservice.RestaurantDataService.IOrder;
import dataservice.RestaurantDataService.IPayment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import model.RestaurantModel.MenuItem;
import model.RestaurantModel.Order;
import model.RestaurantModel.OrderItem;
import model.RestaurantModel.Payment;
import user.AbstractAppService;
import user.PaymentFailedException;
import user.ValidationException;

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

}
