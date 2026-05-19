
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class RestaurantModel {

    public static class MenuItem {

        private int menuItemId;
        private String menuItemName;
        private double menuItemPrice;
        private String menuItemCategory;
        private boolean isAvailable;

        public MenuItem(){

        }

        public MenuItem(int menuItemId, String menuItemName, double menuItemPrice,
            String menuItemCategory, boolean isAvailable){
            this.menuItemId = menuItemId;
            this.menuItemName = menuItemName;
            this.menuItemPrice = menuItemPrice;
            this.menuItemCategory = menuItemCategory;
            this.isAvailable = isAvailable;
        }

        public int getMenuItemId(){
            return menuItemId;
        }
        public void setMenuItemId(int menuItemId){
            this.menuItemId = menuItemId;
        }

        public String getMenuItemName(){
            return menuItemName;
        }
        public void setMenuItemName(String menuItemName){
            this.menuItemName = menuItemName;
        }

        public double getMenuItemPrice(){
            return menuItemPrice;
        }
        public void setMenuItemPrice(double menuItemPrice){
            this.menuItemPrice = menuItemPrice;
        }

        public String getMenuItemCategory(){
            return menuItemCategory;
        }
        public void setMenuItemCategory(String menuItemCategory){
            this.menuItemCategory = menuItemCategory;
        }

        public boolean isAvailable(){
            return isAvailable;
        }
        public void setAvailable(boolean available){
            isAvailable = available;
        }
    }

    public static class Order {

        private int orderId;
        private String orderTableNumber;
        private String orderStatus;
        private LocalDateTime orderCreatedTime;
        private ArrayList<OrderItem> orderItems;
        private double orderTotalAmount;

        public Order(){

        }

        public Order(int orderId, String orderTableNumber, String orderStatus, LocalDateTime orderCreatedTime, ArrayList<OrderItem> orderItems, double orderTotalAmount){
            this.orderId = orderId;
            this.orderTableNumber = orderTableNumber;
            this.orderStatus = orderStatus;
            this.orderCreatedTime = orderCreatedTime;
            this.orderItems = orderItems;
            this.orderTotalAmount = orderTotalAmount;
        }

        public int getOrderId(){
            return orderId;
        }
        public void setOrderId(int orderId){
            this.orderId = orderId;
        }

        public String getOrderTableNumber(){
            return orderTableNumber;
        }
        public void setOrderTableNumber(String orderTableNumber){
            this.orderTableNumber = orderTableNumber;
        }

        public String getOrderStatus(){
            return orderStatus;
        }
        public void setOrderStatus(String orderStatus){
            this.orderStatus = orderStatus;
        }

        public LocalDateTime getOrderCreatedTime(){
            return orderCreatedTime;
        }
        public void setOrderCreatedTime(LocalDateTime orderCreatedTime){
            this.orderCreatedTime = orderCreatedTime;
        }

        public ArrayList<OrderItem> getOrderItems(){
            return orderItems;
        }
        public void setOrderItems(ArrayList<OrderItem> orderItems){
            this.orderItems = orderItems;
        }

        public double getOrderTotalAmount(){
            return orderTotalAmount;
        }
        public void setOrderTotalAmount(double orderTotalAmount){
            this.orderTotalAmount = orderTotalAmount;
        }
    }

    public static class OrderItem {

        private String orderItemId;
        private MenuItem linkedMenuItem;
        private int orderItemQuantity;

        public OrderItem(){

        }

        public OrderItem(String orderItemId, MenuItem linkedMenuItem, int orderItemQuantity){
            this.orderItemId = orderItemId;
            this.linkedMenuItem = linkedMenuItem;
            this.orderItemQuantity = orderItemQuantity;
        }

        public String getOrderItemId(){
            return orderItemId;
        }
        public void setOrderItemId(String orderItemId){
            this.orderItemId = orderItemId;
        }

        public MenuItem getLinkedMenuItem(){
            return linkedMenuItem;
        }
        public void setLinkedMenuItem(MenuItem linkedMenuItem){
            this.linkedMenuItem = linkedMenuItem;
        }

        public int getOrderItemQuantity(){
            return orderItemQuantity;
        }
        public void setOrderItemQuantity(int orderItemQuantity){
            this.orderItemQuantity = orderItemQuantity;
        }
    }

    public static class Payment {

        private int paymentId;
        private int linkedOrderId;
        private double paymentAmount;
        private double paymentTipAmount;
        private String paymentMethod;
        private String paymentStatus;
        private LocalDateTime paymentTimestamp;
        private String paymentTransactionId;

        public Payment(){

        }

        public Payment(int paymentId, int linkedOrderId, double paymentAmount, double paymentTipAmount, String paymentMethod, String paymentStatus, LocalDateTime paymentTimestamp, String paymentTransactionId){
            this.paymentId = paymentId;
            this.linkedOrderId = linkedOrderId;
            this.paymentAmount = paymentAmount;
            this.paymentTipAmount = paymentTipAmount;
            this.paymentMethod = paymentMethod;
            this.paymentStatus = paymentStatus;
            this.paymentTimestamp = paymentTimestamp;
            this.paymentTransactionId = paymentTransactionId;
        }

        public int getPaymentId(){
            return paymentId;
        }
        public void setPaymentId(int paymentId){
            this.paymentId = paymentId;
        }

        public int getLinkedOrderId(){
            return linkedOrderId;
        }
        public void setLinkedOrderId(int linkedOrderId){
            this.linkedOrderId = linkedOrderId;
        }

        public double getPaymentAmount(){
            return paymentAmount;
        }
        public void setPaymentAmount(double paymentAmount){
            this.paymentAmount = paymentAmount;
        }

        public double getPaymentTipAmount(){
            return paymentTipAmount;
        }
        public void setPaymentTipAmount(double paymentTipAmount){
            this.paymentTipAmount = paymentTipAmount;
        }

        public String getPaymentMethod(){
            return paymentMethod;
        }
        public void setPaymentMethod(String paymentMethod){
            this.paymentMethod = paymentMethod;
        }

        public String getPaymentStatus(){
            return paymentStatus;
        }
        public void setPaymentStatus(String paymentStatus){
            this.paymentStatus = paymentStatus;
        }

        public LocalDateTime getPaymentTimestamp(){
            return paymentTimestamp;
        }
        public void setPaymentTimestamp(LocalDateTime paymentTimestamp){
            this.paymentTimestamp = paymentTimestamp;
        }

        public String getPaymentTransactionId(){
            return paymentTransactionId;
        }
        public void setPaymentTransactionId(String paymentTransactionId){
            this.paymentTransactionId = paymentTransactionId;
        }
    }

}
