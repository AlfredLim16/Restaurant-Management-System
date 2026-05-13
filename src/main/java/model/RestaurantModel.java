
package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class RestaurantModel {

    public static enum Role {
        MANAGER,
        CASHIER
    }

    public static class User {

        private String userName;
        private String userPassword;
        private Role userRole;

        public User(){

        }

        public User(String userName, String userPassword, Role userRole){
            this.userName = userName;
            this.userPassword = userPassword;
            this.userRole = userRole;
        }

        public String getUserName(){
            return userName;
        }
        public void setUserName(String userName){
            this.userName = userName;
        }

        public String getUserPassword(){
            return userPassword;
        }
        public void setUserPassword(String userPassword){
            this.userPassword = userPassword;
        }

        public Role getUserRole(){
            return userRole;
        }
        public void setUserRole(Role userRole){
            this.userRole = userRole;
        }
    }

    public static class InventoryItem {

        private int inventoryItemId;
        private String inventoryItemName;
        private String inventoryCategory;
        private int inventoryQuantity;
        private String inventoryUnit;
        private double inventoryCostPerUnit;
        private int inventoryReorderLevel;
        private String inventorySupplier;
        private LocalDate inventoryLastRestockedDate;
        private LocalDate inventoryExpiryDate;

        public InventoryItem(){

        }

        public InventoryItem(int inventoryItemId, String inventoryItemName, String inventoryCategory, int inventoryQuantity, String inventoryUnit, double inventoryCostPerUnit, int inventoryReorderLevel, String inventorySupplier, LocalDate inventoryLastRestockedDate, LocalDate inventoryExpiryDate){
            this.inventoryItemId = inventoryItemId;
            this.inventoryItemName = inventoryItemName;
            this.inventoryCategory = inventoryCategory;
            this.inventoryQuantity = inventoryQuantity;
            this.inventoryUnit = inventoryUnit;
            this.inventoryCostPerUnit = inventoryCostPerUnit;
            this.inventoryReorderLevel = inventoryReorderLevel;
            this.inventorySupplier = inventorySupplier;
            this.inventoryLastRestockedDate = inventoryLastRestockedDate;
            this.inventoryExpiryDate = inventoryExpiryDate;
        }

        public int getInventoryItemId(){
            return inventoryItemId;
        }
        public void setInventoryItemId(int inventoryItemId){
            this.inventoryItemId = inventoryItemId;
        }

        public String getInventoryItemName(){
            return inventoryItemName;
        }
        public void setInventoryItemName(String inventoryItemName){
            this.inventoryItemName = inventoryItemName;
        }

        public String getInventoryCategory(){
            return inventoryCategory;
        }
        public void setInventoryCategory(String inventoryCategory){
            this.inventoryCategory = inventoryCategory;
        }

        public int getInventoryQuantity(){
            return inventoryQuantity;
        }
        public void setInventoryQuantity(int inventoryQuantity){
            this.inventoryQuantity = inventoryQuantity;
        }

        public String getInventoryUnit(){
            return inventoryUnit;
        }
        public void setInventoryUnit(String inventoryUnit){
            this.inventoryUnit = inventoryUnit;
        }

        public double getInventoryCostPerUnit(){
            return inventoryCostPerUnit;
        }
        public void setInventoryCostPerUnit(double inventoryCostPerUnit){
            this.inventoryCostPerUnit = inventoryCostPerUnit;
        }

        public int getInventoryReorderLevel(){
            return inventoryReorderLevel;
        }
        public void setInventoryReorderLevel(int inventoryReorderLevel){
            this.inventoryReorderLevel = inventoryReorderLevel;
        }

        public String getInventorySupplier(){
            return inventorySupplier;
        }
        public void setInventorySupplier(String inventorySupplier){
            this.inventorySupplier = inventorySupplier;
        }

        public LocalDate getInventoryLastRestockedDate(){
            return inventoryLastRestockedDate;
        }
        public void setInventoryLastRestockedDate(LocalDate inventoryLastRestockedDate){
            this.inventoryLastRestockedDate = inventoryLastRestockedDate;
        }

        public LocalDate getInventoryExpiryDate(){
            return inventoryExpiryDate;
        }
        public void setInventoryExpiryDate(LocalDate inventoryExpiryDate){
            this.inventoryExpiryDate = inventoryExpiryDate;
        }
    }

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

    public static class FoodWaste {

        private int foodWasteId;
        private String foodWasteItemName;
        private double foodWasteQuantity;
        private String foodWasteUnit;
        private String foodWasteReason;
        private double foodWasteEstimatedCost;
        private LocalDateTime foodWasteRecordedDate;
        private String foodWasteRecordedBy;
        private String foodWasteCategory;

        public FoodWaste(){

        }

        public FoodWaste(int foodWasteId, String foodWasteItemName, double foodWasteQuantity, String foodWasteUnit, String foodWasteReason, double foodWasteEstimatedCost, LocalDateTime foodWasteRecordedDate, String foodWasteRecordedBy, String foodWasteCategory){
            this.foodWasteId = foodWasteId;
            this.foodWasteItemName = foodWasteItemName;
            this.foodWasteQuantity = foodWasteQuantity;
            this.foodWasteUnit = foodWasteUnit;
            this.foodWasteReason = foodWasteReason;
            this.foodWasteEstimatedCost = foodWasteEstimatedCost;
            this.foodWasteRecordedDate = foodWasteRecordedDate;
            this.foodWasteRecordedBy = foodWasteRecordedBy;
            this.foodWasteCategory = foodWasteCategory;
        }

        public int getFoodWasteId(){
            return foodWasteId;
        }
        public void setFoodWasteId(int foodWasteId){
            this.foodWasteId = foodWasteId;
        }

        public String getFoodWasteItemName(){
            return foodWasteItemName;
        }
        public void setFoodWasteItemName(String foodWasteItemName){
            this.foodWasteItemName = foodWasteItemName;
        }

        public double getFoodWasteQuantity(){
            return foodWasteQuantity;
        }
        public void setFoodWasteQuantity(double foodWasteQuantity){
            this.foodWasteQuantity = foodWasteQuantity;
        }

        public String getFoodWasteUnit(){
            return foodWasteUnit;
        }
        public void setFoodWasteUnit(String foodWasteUnit){
            this.foodWasteUnit = foodWasteUnit;
        }

        public String getFoodWasteReason(){
            return foodWasteReason;
        }
        public void setFoodWasteReason(String foodWasteReason){
            this.foodWasteReason = foodWasteReason;
        }

        public double getFoodWasteEstimatedCost(){
            return foodWasteEstimatedCost;
        }
        public void setFoodWasteEstimatedCost(double foodWasteEstimatedCost){
            this.foodWasteEstimatedCost = foodWasteEstimatedCost;
        }

        public LocalDateTime getFoodWasteRecordedDate(){
            return foodWasteRecordedDate;
        }
        public void setFoodWasteRecordedDate(LocalDateTime foodWasteRecordedDate){
            this.foodWasteRecordedDate = foodWasteRecordedDate;
        }

        public String getFoodWasteRecordedBy(){
            return foodWasteRecordedBy;
        }
        public void setFoodWasteRecordedBy(String foodWasteRecordedBy){
            this.foodWasteRecordedBy = foodWasteRecordedBy;
        }

        public String getFoodWasteCategory(){
            return foodWasteCategory;
        }
        public void setFoodWasteCategory(String foodWasteCategory){
            this.foodWasteCategory = foodWasteCategory;
        }
    }

}
