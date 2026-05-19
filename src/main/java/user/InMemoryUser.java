package user;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryUser implements IUser {

    private final HashMap<String, User> users;

    public InMemoryUser(){
        users = new HashMap<>();

        // Username: manager, Passowrd: manager123
        // Username: cashier, Passowrd: cashier123
        users.put("manager", new User("manager", "manager123", Role.MANAGER));
        users.put("cashier", new User("cashier", "cashier123", Role.CASHIER));
    }

    @Override
    public void addUser(User user){
        if(user == null || user.getUserName() == null){
            throw new IllegalArgumentException("User or username cannot be null");
        }
        users.put(user.getUserName(), user);
    }

    @Override
    public User findByUsername(String username){
        return users.get(username);
    }

    @Override
    public void updateUser(User user){
        if(user == null || user.getUserName() == null){
            throw new IllegalArgumentException("User or username cannot be null");
        }
        if(!users.containsKey(user.getUserName())){
            throw new IllegalArgumentException("User not found: " + user.getUserName());
        }
        users.put(user.getUserName(), user);
    }

    @Override
    public void deleteUser(String username){
        users.remove(username);
    }

    @Override
    public boolean userExists(String username){
        return users.containsKey(username);
    }

    @Override
    public ArrayList<User> getAllUsers(){
        return new ArrayList<>(users.values());
    }
}
