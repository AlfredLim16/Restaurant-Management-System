package manager;

import java.time.LocalDateTime;

public class FoodWaste {

    private int foodWasteId;
    private String foodWasteItemName;
    private double foodWasteQuantity;
    private String foodWasteUnit;
    private String foodWasteReason;
    private double foodWasteEstimatedCost;
    private LocalDateTime foodWasteRecordedDate;
    private String foodWasteRecordedBy;
    private String foodWasteCategory;

    public FoodWaste(){

    }

    public FoodWaste(int foodWasteId, String foodWasteItemName, double foodWasteQuantity, String foodWasteUnit, String foodWasteReason, double foodWasteEstimatedCost, LocalDateTime foodWasteRecordedDate, String foodWasteRecordedBy, String foodWasteCategory){
        this.foodWasteId = foodWasteId;
        this.foodWasteItemName = foodWasteItemName;
        this.foodWasteQuantity = foodWasteQuantity;
        this.foodWasteUnit = foodWasteUnit;
        this.foodWasteReason = foodWasteReason;
        this.foodWasteEstimatedCost = foodWasteEstimatedCost;
        this.foodWasteRecordedDate = foodWasteRecordedDate;
        this.foodWasteRecordedBy = foodWasteRecordedBy;
        this.foodWasteCategory = foodWasteCategory;
    }

    public int getFoodWasteId(){
        return foodWasteId;
    }
    public void setFoodWasteId(int foodWasteId){
        this.foodWasteId = foodWasteId;
    }

    public String getFoodWasteItemName(){
        return foodWasteItemName;
    }
    public void setFoodWasteItemName(String foodWasteItemName){
        this.foodWasteItemName = foodWasteItemName;
    }

    public double getFoodWasteQuantity(){
        return foodWasteQuantity;
    }
    public void setFoodWasteQuantity(double foodWasteQuantity){
        this.foodWasteQuantity = foodWasteQuantity;
    }

    public String getFoodWasteUnit(){
        return foodWasteUnit;
    }
    public void setFoodWasteUnit(String foodWasteUnit){
        this.foodWasteUnit = foodWasteUnit;
    }

    public String getFoodWasteReason(){
        return foodWasteReason;
    }
    public void setFoodWasteReason(String foodWasteReason){
        this.foodWasteReason = foodWasteReason;
    }

    public double getFoodWasteEstimatedCost(){
        return foodWasteEstimatedCost;
    }
    public void setFoodWasteEstimatedCost(double foodWasteEstimatedCost){
        this.foodWasteEstimatedCost = foodWasteEstimatedCost;
    }

    public LocalDateTime getFoodWasteRecordedDate(){
        return foodWasteRecordedDate;
    }
    public void setFoodWasteRecordedDate(LocalDateTime foodWasteRecordedDate){
        this.foodWasteRecordedDate = foodWasteRecordedDate;
    }

    public String getFoodWasteRecordedBy(){
        return foodWasteRecordedBy;
    }
    public void setFoodWasteRecordedBy(String foodWasteRecordedBy){
        this.foodWasteRecordedBy = foodWasteRecordedBy;
    }

    public String getFoodWasteCategory(){
        return foodWasteCategory;
    }
    public void setFoodWasteCategory(String foodWasteCategory){
        this.foodWasteCategory = foodWasteCategory;
    }
}
