package cashier;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order {

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
