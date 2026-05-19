package user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractAppService {

    // returns the model if it is not null
    protected <Model> Model getOrThrow(Model model, String message) throws ValidationException{
        if(model == null){
            throw new ValidationException(message);
        }
        return model;
    }

    // if the string is null or contains only whitespace
    protected void ensureNotEmpty(String valueToCheck, String fieldName) throws ValidationException{
        if(valueToCheck == null || valueToCheck.trim().isEmpty()){
            throw new ValidationException(fieldName + " is required");
        }
    }

    // ? - it's accepts any kind of arraylist type like String, Integer and etc
    protected void ensureNotNullNotEmpty(ArrayList<?> listToCheck, String fieldName) throws ValidationException{
        if(listToCheck == null || listToCheck.isEmpty()){
            throw new ValidationException(fieldName + " must contain at least one item");
        }
    }

    // if the int value is 0 or negative
    protected void ensurePositive(int valueToCheck, String fieldName) throws ValidationException{
        if(valueToCheck <= 0){
            throw new ValidationException(fieldName + " must be greater than 0");
        }
    }

    // if the double value is 0 or negative
    protected void ensurePositive(double valueToCheck, String fieldName) throws ValidationException{
        if(valueToCheck <= 0){
            throw new ValidationException(fieldName + " must be greater than 0");
        }
    }

    // if the int value is negative
    protected void ensureNotNegative(int valueToCheck, String fieldName) throws ValidationException{
        if(valueToCheck < 0){
            throw new ValidationException(fieldName + " cannot be negative");
        }
    }

    // if the double value is negative
    protected void ensureNotNegative(double valueToCheck, String fieldName) throws ValidationException{
        if(valueToCheck < 0){
            throw new ValidationException(fieldName + " cannot be negative");
        }
    }

    // if either date is null, or if startDate comes after endDate
    protected void ensureDateRange(LocalDate startDate, LocalDate endDate) throws ValidationException{
        if(startDate == null || endDate == null){
            throw new ValidationException("Start date and end date are required");
        }
        if(startDate.isAfter(endDate)){
            throw new ValidationException("Start date cannot be after end date");
        }
    }

    // add value in a arraylist inside of hashmap, if no keys exist arraylist will created first
    protected <Key, Value> void putIntoGroupedMap(HashMap<Key, ArrayList<Value>> targetMap, Key key, Value value){
        ArrayList<Value> existingList = targetMap.get(key);
        if(existingList == null){
            existingList = new ArrayList<>();
            targetMap.put(key, existingList);
        }
        existingList.add(value);
    }
}
