package manager;

import cashier.IMenuItem;
import cashier.MenuItem;
import java.util.ArrayList;
import user.AbstractAppService;
import user.ValidationException;

public class MenuItemService extends AbstractAppService {

    private final IMenuItem _menuItem;
    private final IMenuItemIngredient _ingredient;
    private final IInventoryItem _inventoryItem;

    public MenuItemService(IMenuItem menuItem, IMenuItemIngredient ingredient, IInventoryItem inventoryItem){
        this._menuItem = menuItem;
        this._ingredient = ingredient;
        this._inventoryItem = inventoryItem;
    }

    public ArrayList<MenuItem> getAllMenuItems(){
        return new ArrayList<>(_menuItem.getAll());
    }

    public ArrayList<InventoryItem> getAllInventoryItems(){
        return new ArrayList<>(_inventoryItem.getAll());
    }

    public ArrayList<MenuItemIngredient> getIngredients(int menuItemId){
        return _ingredient.getByMenuItemId(menuItemId);
    }

    public MenuItem addMenuItem(String name, double price, String category, boolean isAvailable, ArrayList<MenuItemIngredient> ingredients) throws ValidationException{
        ensureNotEmpty(name, "Name");
        ensureNotEmpty(category, "Category");
        ensureNotNegative(price, "Price");

        MenuItem item = new MenuItem();
        item.setMenuItemName(name.trim());
        item.setMenuItemPrice(price);
        item.setMenuItemCategory(category.trim());
        item.setAvailable(isAvailable);

        _menuItem.create(item);

        if(ingredients != null && !ingredients.isEmpty()){
            for(MenuItemIngredient ing : ingredients){
                ing.setMenuItemId(item.getMenuItemId());
            }
            _ingredient.saveIngredients(item.getMenuItemId(), ingredients);
        }

        return item;
    }

    public void updateMenuItem(int id, String name, double price, String category, boolean isAvailable, ArrayList<MenuItemIngredient> ingredients) throws ValidationException{
        ensureNotEmpty(name, "Name");
        ensureNotEmpty(category, "Category");
        ensureNotNegative(price, "Price");

        MenuItem item = getMenuItemOrThrow(id);
        item.setMenuItemName(name.trim());
        item.setMenuItemPrice(price);
        item.setMenuItemCategory(category.trim());
        item.setAvailable(isAvailable);

        _menuItem.update(item);

        _ingredient.deleteByMenuItemId(id);
        if(ingredients != null && !ingredients.isEmpty()){
            for(MenuItemIngredient ing : ingredients){
                ing.setMenuItemId(id);
            }
            _ingredient.saveIngredients(id, ingredients);
        }
    }

    public void deleteMenuItem(int id) throws ValidationException{
        getMenuItemOrThrow(id);
        _ingredient.deleteByMenuItemId(id);
        _menuItem.delete(id);
    }

    public void toggleAvailability(int id) throws ValidationException{
        MenuItem item = getMenuItemOrThrow(id);
        item.setAvailable(!item.isAvailable());
        _menuItem.update(item);
    }

    private MenuItem getMenuItemOrThrow(int id) throws ValidationException{
        return getOrThrow(_menuItem.get(id), "Menu item not found");
    }
}
