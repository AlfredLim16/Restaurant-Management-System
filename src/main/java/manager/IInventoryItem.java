package manager;

import java.util.ArrayList;

public interface IInventoryItem {

    void create(InventoryItem item);
    InventoryItem get(int inventoryId);
    ArrayList<InventoryItem> getAll();
    void update(InventoryItem item);
    void delete(int inventoryId);

    ArrayList<InventoryItem> findByCategory(String category);
    ArrayList<InventoryItem> findExpiringSoon(int daysThreshold);
    ArrayList<InventoryItem> findLowStock();
}
