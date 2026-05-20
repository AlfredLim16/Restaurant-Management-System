package cashier;

import java.util.ArrayList;

public interface IMenuItem {

    void create(MenuItem item);
    MenuItem get(int itemId);
    ArrayList<MenuItem> getAll();
    void update(MenuItem item);
    void delete(int itemId);

    ArrayList<MenuItem> findAvailable();
    ArrayList<MenuItem> findByCategory(String category);
}
