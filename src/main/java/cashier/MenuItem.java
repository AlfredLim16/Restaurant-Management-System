package cashier;

public class MenuItem {

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
