package manager;

import java.time.LocalDate;
import java.util.ArrayList;

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
