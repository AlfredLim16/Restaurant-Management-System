package manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import user.AbstractAppService;
import user.InsufficientInventoryException;
import user.ValidationException;

public class FoodWasteService extends AbstractAppService {

    private final IFoodWaste _foodWaste;
    private final IInventoryItem _inventoryItem;

    public FoodWasteService(IFoodWaste foodWaste, IInventoryItem inventoryItem){
        this._foodWaste = foodWaste;
        this._inventoryItem = inventoryItem;
    }

    public FoodWaste recordWaste(int inventoryItemId, double quantity, String reason, String recordedBy) throws ValidationException, InsufficientInventoryException{
        InventoryItem item = _inventoryItem.get(inventoryItemId);
        if(item == null){
            throw new ValidationException("Inventory item not found");
        }
        ensurePositive(quantity, "Quantity");
        ensureNotEmpty(reason, "Reason");
        ensureNotEmpty(recordedBy, "Recorded by");

        if(item.getInventoryQuantity() < (int) quantity){
            throw new InsufficientInventoryException("Not enough stock for " + item.getInventoryItemName() + ". Available: " + item.getInventoryQuantity() + ", Requested: " + (int) quantity);
        }

        item.setInventoryQuantity(item.getInventoryQuantity() - (int) quantity);
        _inventoryItem.update(item);

        double estimatedCost = quantity * item.getInventoryCostPerUnit();

        FoodWaste newRecord = new FoodWaste();
        newRecord.setFoodWasteItemName(item.getInventoryItemName());
        newRecord.setFoodWasteQuantity(quantity);
        newRecord.setFoodWasteUnit(item.getInventoryUnit());
        newRecord.setFoodWasteReason(reason);
        newRecord.setFoodWasteEstimatedCost(estimatedCost);
        newRecord.setFoodWasteRecordedDate(LocalDateTime.now());
        newRecord.setFoodWasteRecordedBy(recordedBy);
        newRecord.setFoodWasteCategory(item.getInventoryCategory());

        _foodWaste.create(newRecord);
        return newRecord;
    }

    public void deleteWaste(int wasteId) throws ValidationException{
        getWasteOrThrow(wasteId);
        _foodWaste.delete(wasteId);
    }

    // estimated cost of waste records within the date range
    public double calculateTotalWasteCost(LocalDate startDate, LocalDate endDate) throws ValidationException{
        ensureDateRange(startDate, endDate);
        double accumulatedWasteCost = 0.0;
        for(FoodWaste currentRecord : _foodWaste.findByDateRange(startDate, endDate)){
            accumulatedWasteCost += currentRecord.getFoodWasteEstimatedCost();
        }
        return accumulatedWasteCost;
    }

    // estimated cost of waste recorded today
    public double calculateDailyWasteCost(){
        LocalDate today = LocalDate.now();
        double dailyWasteCost = 0.0;
        for(FoodWaste currentWasteRecord : _foodWaste.getAll()){
            if(currentWasteRecord.getFoodWasteRecordedDate().toLocalDate().equals(today)){
                dailyWasteCost += currentWasteRecord.getFoodWasteEstimatedCost();
            }
        }
        return dailyWasteCost;
    }

    // estimated cost of waste for the given year and month
    public double calculateMonthlyWasteCost(int year, int month){
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        double monthlyCost = 0.0;
        for(FoodWaste currentRecord : _foodWaste.findByDateRange(startOfMonth, endOfMonth)){
            monthlyCost += currentRecord.getFoodWasteEstimatedCost();
        }
        return monthlyCost;
    }

    public ArrayList<FoodWaste> getAllWasteRecords(){
        return new ArrayList<>(_foodWaste.getAll());
    }

    public ArrayList<InventoryItem> getAllInventoryItems(){
        return new ArrayList<>(_inventoryItem.getAll());
    }

    public ArrayList<FoodWaste> getWasteByDateRange(LocalDate startDate, LocalDate endDate) throws ValidationException{
        ensureDateRange(startDate, endDate);
        return new ArrayList<>(_foodWaste.findByDateRange(startDate, endDate));
    }

    public ArrayList<FoodWaste> getWasteByReason(String targetReason){
        return new ArrayList<>(_foodWaste.findByReason(targetReason));
    }

    public ArrayList<FoodWaste> getWasteByCategory(String targetCategory){
        return new ArrayList<>(_foodWaste.findByCategory(targetCategory));
    }

    // return hashmap of waste reason count
    public HashMap<String, Long> getWasteCountByReason(){
        HashMap<String, Long> wasteCountByReason = new HashMap<>();
        for(FoodWaste currentRecord : _foodWaste.getAll()){
            String wasteReason = currentRecord.getFoodWasteReason();
            Long currentCount = wasteCountByReason.get(wasteReason);
            if(currentCount == null){
                wasteCountByReason.put(wasteReason, 1L);
            }else{
                wasteCountByReason.put(wasteReason, currentCount + 1);
            }
        }
        return wasteCountByReason;
    }

    // return hashmap of category to total estimated cost
    public HashMap<String, Double> getWasteCostByCategory(){
        HashMap<String, Double> wasteCostByCategory = new HashMap<>();
        for(FoodWaste currentRecord : _foodWaste.getAll()){
            String wasteCategory = currentRecord.getFoodWasteCategory();
            Double accumulatedCost = wasteCostByCategory.get(wasteCategory);
            if(accumulatedCost == null){
                wasteCostByCategory.put(wasteCategory, currentRecord.getFoodWasteEstimatedCost());
            }else{
                wasteCostByCategory.put(wasteCategory, accumulatedCost + currentRecord.getFoodWasteEstimatedCost());
            }
        }
        return wasteCostByCategory;
    }

    // return hashmap of an item name to total wasted quantity
    public HashMap<String, Double> getQuantityByItem(){
        HashMap<String, Double> quantityByItemName = new HashMap<>();
        for(FoodWaste currentRecord : _foodWaste.getAll()){
            String wastedItemName = currentRecord.getFoodWasteItemName();
            Double accumulatedQuantity = quantityByItemName.get(wastedItemName);
            if(accumulatedQuantity == null){
                quantityByItemName.put(wastedItemName, currentRecord.getFoodWasteQuantity());
            }else{
                quantityByItemName.put(wastedItemName, accumulatedQuantity + currentRecord.getFoodWasteQuantity());
            }
        }
        return quantityByItemName;
    }

    // group waste records into a hashmap, key by reason
    public HashMap<String, ArrayList<FoodWaste>> groupByReason(ArrayList<FoodWaste> wasteRecordList){
        HashMap<String, ArrayList<FoodWaste>> wasteRecordsGroupedByReason = new HashMap<>();
        for(FoodWaste currentRecord : wasteRecordList){
            putIntoGroupedMap(wasteRecordsGroupedByReason, currentRecord.getFoodWasteReason(), currentRecord);
        }
        return wasteRecordsGroupedByReason;
    }

    // group waste records into a hashmap, key by category
    public HashMap<String, ArrayList<FoodWaste>> groupByCategory(ArrayList<FoodWaste> wasteRecordList){
        HashMap<String, ArrayList<FoodWaste>> wasteRecordsGroupedByCategory = new HashMap<>();
        for(FoodWaste currentRecord : wasteRecordList){
            putIntoGroupedMap(wasteRecordsGroupedByCategory, currentRecord.getFoodWasteCategory(), currentRecord);
        }
        return wasteRecordsGroupedByCategory;
    }

    private FoodWaste getWasteOrThrow(int wasteIdentifier) throws ValidationException{
        return getOrThrow(_foodWaste.get(wasteIdentifier), "Waste record not found");
    }
}
