package manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import user.AbstractDataService;

public class InMemoryFoodWaste extends AbstractDataService<FoodWaste> implements IFoodWaste {

    private final ArrayList<FoodWaste> dateRangeWasteRecords = new ArrayList<>();
    private final ArrayList<FoodWaste> matchingWasteRecords = new ArrayList<>();
    private final ArrayList<FoodWaste> categoryWasteRecords = new ArrayList<>();

    public InMemoryFoodWaste(){
        sampleFoodWasteData();
    }

    private void sampleFoodWasteData(){
        // Waste 1: ID 0 (auto), Item Chicken Breast, Qty 2.5, Unit kg, Reason Expired, Cost 75.00, Recorded yesterday, By admin, Category Meat
        create(new FoodWaste(0, "Chicken Breast", 2.5, "kg", "Expired", 75.00, LocalDateTime.now().minusDays(1), "admin", "Meat"));

        // Waste 2: ID 0 (auto), Item Nuggets, Qty 1.0, Unit kg, Reason Overcooked, Cost 70.00, Recorded today, By cashier, Category Main
        create(new FoodWaste(0, "Nuggets", 1.0, "kg", "Overcooked", 70.00, LocalDateTime.now(), "cashier", "Main"));
    }

    @Override
    protected int getModelId(FoodWaste foodWasteRecord){
        return foodWasteRecord.getFoodWasteId();
    }

    @Override
    protected void setModelId(FoodWaste foodWasteRecord, int wasteId){
        foodWasteRecord.setFoodWasteId(wasteId);
    }

    @Override
    public ArrayList<FoodWaste> findByCategory(String targetCategory){
        categoryWasteRecords.clear();
        for(FoodWaste currentRecord : storage.values()){
            if(currentRecord.getFoodWasteCategory().equals(targetCategory)){
                categoryWasteRecords.add(currentRecord);
            }
        }
        return categoryWasteRecords;
    }

    @Override
    public ArrayList<FoodWaste> findByDateRange(LocalDate startDate, LocalDate endDate){
        dateRangeWasteRecords.clear();
        for(FoodWaste currentRecord : storage.values()){
            LocalDate wasteDate = currentRecord.getFoodWasteRecordedDate().toLocalDate();
            if(!wasteDate.isBefore(startDate) && !wasteDate.isAfter(endDate)){
                dateRangeWasteRecords.add(currentRecord);
            }
        }
        return dateRangeWasteRecords;
    }

    @Override
    public ArrayList<FoodWaste> findByReason(String targetReason){
        matchingWasteRecords.clear();
        for(FoodWaste currentRecord : storage.values()){
            if(currentRecord.getFoodWasteReason().equals(targetReason)){
                matchingWasteRecords.add(currentRecord);
            }
        }
        return matchingWasteRecords;
    }
}
