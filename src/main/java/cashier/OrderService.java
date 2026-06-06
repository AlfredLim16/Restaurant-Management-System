package cashier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import manager.IInventoryItem;
import manager.IMenuItemIngredient;
import manager.InventoryItem;
import manager.MenuItemIngredient;
import user.AbstractAppService;
import user.InsufficientInventoryException;
import user.ValidationException;

public class OrderService extends AbstractAppService {

    private final IOrder _order;
    private final IMenuItem _menuItem;
    private final IMenuItemIngredient _ingredient;
    private final IInventoryItem _inventoryItem;

    public OrderService(IOrder order, IMenuItem menuItem, IMenuItemIngredient ingredient, IInventoryItem inventoryItem){
        this._order = order;
        this._menuItem = menuItem;
        this._ingredient = ingredient;
        this._inventoryItem = inventoryItem;
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

    public Order createOrder(String tableNumber, ArrayList<OrderItem> orderItems) throws ValidationException, InsufficientInventoryException{
        ensureNotEmpty(tableNumber, "Table number");
        ensureNotNullNotEmpty(orderItems, "Order items");

        for(OrderItem orderItem : orderItems){
            MenuItem requestedMenuItem = orderItem.getLinkedMenuItem();
            ensureMenuItemAvailable(requestedMenuItem, requestedMenuItem.getMenuItemId());
            ensurePositive(orderItem.getOrderItemQuantity(), "Item quantity");
        }

        for(OrderItem orderItem : orderItems){
            int menuItemId = orderItem.getLinkedMenuItem().getMenuItemId();
            int orderedQty = orderItem.getOrderItemQuantity();
            ArrayList<MenuItemIngredient> ingredients = _ingredient.getByMenuItemId(menuItemId);
            for(MenuItemIngredient ingredient : ingredients){
                InventoryItem invItem = _inventoryItem.get(ingredient.getInventoryItemId());
                if(invItem == null){
                    continue;
                }
                int needed = (int) Math.ceil(ingredient.getQuantityRequired() * orderedQty);
                if(invItem.getInventoryQuantity() < needed){
                    throw new InsufficientInventoryException(
                        "Not enough inventory for '" + invItem.getInventoryItemName() +
                        "'. Available: " + invItem.getInventoryQuantity() +
                        ", Required: " + needed
                    );
                }
            }
        }

        for(OrderItem orderItem : orderItems){
            int menuItemId = orderItem.getLinkedMenuItem().getMenuItemId();
            int orderedQty = orderItem.getOrderItemQuantity();
            ArrayList<MenuItemIngredient> ingredients = _ingredient.getByMenuItemId(menuItemId);
            for(MenuItemIngredient ingredient : ingredients){
                InventoryItem invItem = _inventoryItem.get(ingredient.getInventoryItemId());
                if(invItem == null){
                    continue;
                }
                int needed = (int) Math.ceil(ingredient.getQuantityRequired() * orderedQty);
                invItem.setInventoryQuantity(invItem.getInventoryQuantity() - needed);
                _inventoryItem.update(invItem);
            }
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
