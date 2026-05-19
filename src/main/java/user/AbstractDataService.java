package user;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractDataService<Model> {

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
