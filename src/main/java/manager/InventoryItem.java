package manager;

import java.time.LocalDate;

public class InventoryItem {

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
