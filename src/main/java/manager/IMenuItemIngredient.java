package manager;

import java.util.ArrayList;

public interface IMenuItemIngredient {

    void saveIngredients(int menuItemId, ArrayList<MenuItemIngredient> ingredients);
    ArrayList<MenuItemIngredient> getByMenuItemId(int menuItemId);
    void deleteByMenuItemId(int menuItemId);
}
