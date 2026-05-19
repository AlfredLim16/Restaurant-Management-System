package user;

import java.util.ArrayList;

public interface IUser {

    void addUser(User user);
    User findByUsername(String username);
    void updateUser(User user);
    void deleteUser(String username);
    boolean userExists(String username);

    ArrayList<User> getAllUsers();
}
