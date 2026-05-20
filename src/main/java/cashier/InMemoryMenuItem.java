package cashier;

import java.util.ArrayList;
import user.AbstractDataService;

public class InMemoryMenuItem extends AbstractDataService<MenuItem> implements IMenuItem {

    private final ArrayList<MenuItem> matchingMenuItems = new ArrayList<>();
    private final ArrayList<MenuItem> availableMenuItems = new ArrayList<>();

    public InMemoryMenuItem(){
        sampleMenuData();
    }

    private void sampleMenuData(){
        // ID 0 (auto), Name: Chicken, Price: 70.00, Category: Main, Available: true
        create(new MenuItem(0, "Chicken", 70.00, "Main", true));

        // ID 0 (auto), Name: Nuggets, Price: 70.00, Category: Main, Available: true
        create(new MenuItem(0, "Nuggets", 70.00, "Main", true));

        // ID 0 (auto), Name: Fish Fillet, Price: 50.00, Category: Main, Available: true
        create(new MenuItem(0, "Fish Fillet", 50.00, "Main", true));

        // ID 0 (auto), Name: McCafe, Price: 20.00, Category: Drink, Available: true
        create(new MenuItem(0, "McCafe", 20.00, "Drink", true));

        // ID 0 (auto), Name: Coke Float, Price: 20.00, Category: Drink, Available: true
        create(new MenuItem(0, "Coke Float", 20.00, "Drink", true));

        // ID 0 (auto), Name: Sundae, Price: 20.00, Category: Drink, Available: true
        create(new MenuItem(0, "Sundae", 20.00, "Drink", true));

        // ID 0 (auto), Name: McFlurry, Price: 25.00, Category: Drink, Available: true
        create(new MenuItem(0, "McFlurry", 25.00, "Drink", true));

        // ID 0 (auto), Name: Beef Burger, Price: 85.00, Category: Main, Available: false
        create(new MenuItem(0, "Beef Burger", 85.00, "Main", false));

        // ID 0 (auto), Name: Fries, Price: 40.00, Category: Side, Available: false
        create(new MenuItem(0, "Fries", 40.00, "Side", false));

        // ID 0 (auto), Name: Milk Shake, Price: 30.00, Category: Drink, Available: false
        create(new MenuItem(0, "Milk Shake", 30.00, "Drink", false));
    }

    @Override
    protected int getModelId(MenuItem menuItem){
        return menuItem.getMenuItemId();
    }

    @Override
    protected void setModelId(MenuItem menuItem, int itemId){
        menuItem.setMenuItemId(itemId);
    }

    @Override
    public ArrayList<MenuItem> findAvailable(){
        availableMenuItems.clear();
        for(MenuItem currentItem : storage.values()){
            if(currentItem.isAvailable()){
                availableMenuItems.add(currentItem);
            }
        }
        return availableMenuItems;
    }

    @Override
    public ArrayList<MenuItem> findByCategory(String targetCategory){
        matchingMenuItems.clear();
        for(MenuItem currentItem : storage.values()){
            if(currentItem.getMenuItemCategory().equals(targetCategory)){
                matchingMenuItems.add(currentItem);
            }
        }
        return matchingMenuItems;
    }
}
