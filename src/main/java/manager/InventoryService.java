package manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import user.AbstractAppService;
import user.InsufficientInventoryException;
import user.ValidationException;

public class InventoryService extends AbstractAppService {

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

    public void updateName(int inventoryId, String newName) throws ValidationException{
        InventoryItem existingItem = getInventoryItemOrThrow(inventoryId);
        ensureNotEmpty(newName, "Item name");
        existingItem.setInventoryItemName(newName.trim());
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
