package manager;

import java.time.LocalDate;
import java.util.ArrayList;
import user.AbstractDataService;

public class InMemoryInventoryItem extends AbstractDataService<InventoryItem> implements IInventoryItem {

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
