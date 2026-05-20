package cashier;

public class OrderItem {

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
