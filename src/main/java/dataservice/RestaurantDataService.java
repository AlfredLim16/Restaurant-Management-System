
package dataservice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import model.RestaurantModel.*;

/**
 *
 * @author Allysa
 */
public class RestaurantDataService {

    public static abstract class AbstractDataService<Model> {

        protected HashMap<Integer, Model> storage = new HashMap<>();
        protected int nextModelId = 1;

        protected abstract int getModelId(Model model);
        protected abstract void setModelId(Model model, int id);

        public void create(Model newModel){
            if(getModelId(newModel) == 0){
                setModelId(newModel, nextModelId++);
            }
            storage.put(getModelId(newModel), newModel);
        }

        public Model get(int modelId){
            return storage.get(modelId);
        }

        public ArrayList<Model> getAll(){
            return new ArrayList<>(storage.values());
        }

        public void update(Model existingModel){
            int id = getModelId(existingModel);
            if(storage.containsKey(id)){
                storage.put(id, existingModel);
            }
        }

        public void delete(int modelId){
            storage.remove(modelId);
        }

        public boolean exists(int modelId){
            return storage.containsKey(modelId);
        }
    }

    public interface IAuthentication {

        User login(String username, String password);
    }

    public interface IPermission {

        boolean canAccessOrder(Role role);
        boolean canAccessInventory(Role role);
        boolean canAccessPayment(Role role);
        boolean canAccessReports(Role role);
        boolean canAccessFoodWaste(Role role);
        boolean canAccessSettings(Role role);
    }

    public interface IUser {

        void addUser(User user);
        User findByUsername(String username);
        void updateUser(User user);
        void deleteUser(String username);
        boolean userExists(String username);

        ArrayList<User> getAllUsers();
    }

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

    public interface IMenuItem {

        void create(MenuItem item);
        MenuItem get(int itemId);
        ArrayList<MenuItem> getAll();
        void update(MenuItem item);
        void delete(int itemId);

        ArrayList<MenuItem> findAvailable();
        ArrayList<MenuItem> findByCategory(String category);
    }

    public interface IOrder {

        void create(Order order);
        Order get(int orderId);
        ArrayList<Order> getAll();
        void update(Order order);
        void delete(int orderId);

        ArrayList<Order> findByStatus(String status);
        ArrayList<Order> findByTable(String tableNumber);
    }

    public interface IPayment {

        void create(Payment payment);
        Payment get(int paymentId);
        ArrayList<Payment> getAll();
        void update(Payment payment);
        void delete(int paymentId);

        ArrayList<Payment> findByDate(LocalDate date);
        ArrayList<Payment> findByOrderId(int orderId);
        ArrayList<Payment> findByPaymentMethod(String method);
        ArrayList<Payment> findByStatus(String status);
    }

    public interface IFoodWaste {

        void create(FoodWaste waste);
        FoodWaste get(int wasteId);
        ArrayList<FoodWaste> getAll();
        void update(FoodWaste waste);
        void delete(int wasteId);

        ArrayList<FoodWaste> findByCategory(String category);
        ArrayList<FoodWaste> findByDateRange(LocalDate startDate, LocalDate endDate);
        ArrayList<FoodWaste> findByReason(String reason);
    }

}
