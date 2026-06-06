package manager;

public class MenuItemIngredient {

    private int menuItemId;
    private int inventoryItemId;
    private String inventoryItemName;
    private double quantityRequired;

    public MenuItemIngredient(){
    }

    public MenuItemIngredient(int menuItemId, int inventoryItemId, String inventoryItemName, double quantityRequired){
        this.menuItemId = menuItemId;
        this.inventoryItemId = inventoryItemId;
        this.inventoryItemName = inventoryItemName;
        this.quantityRequired = quantityRequired;
    }

    public int getMenuItemId(){
        return menuItemId;
    }
    public void setMenuItemId(int menuItemId){
        this.menuItemId = menuItemId;
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

    public double getQuantityRequired(){
        return quantityRequired;
    }
    public void setQuantityRequired(double quantityRequired){
        this.quantityRequired = quantityRequired;
    }
}
