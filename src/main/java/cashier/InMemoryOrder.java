package cashier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import user.AbstractDataService;

public class InMemoryOrder extends AbstractDataService<Order> implements IOrder {

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
