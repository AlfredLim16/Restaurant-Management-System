package manager;

import java.util.ArrayList;
import user.AbstractAppService;
import user.IUser;
import user.Role;
import user.User;
import user.ValidationException;

public class UserManagementService extends AbstractAppService {

    private final IUser _user;

    public UserManagementService(IUser userData){
        this._user = userData;
    }

    public ArrayList<User> getAllUsers(){
        return new ArrayList<>(_user.getAllUsers());
    }

    public void addUser(String username, String password, String role) throws ValidationException{
        ensureNotEmpty(username, "Username");
        ensureNotEmpty(password, "Password");
        ensureNotEmpty(role, "Role");

        if(_user.userExists(username)){
            throw new ValidationException("Username '" + username + "' already exists");
        }

        User newUser = new User();
        newUser.setUserName(username);
        newUser.setUserPassword(password);
        newUser.setUserRole(Role.valueOf(role.toUpperCase()));
        _user.addUser(newUser);
    }

    public void updateUser(String username, String newPassword, String newRole) throws ValidationException{
        ensureNotEmpty(newPassword, "Password");
        ensureNotEmpty(newRole, "Role");

        User existing = _user.findByUsername(username);
        if(existing == null){
            throw new ValidationException("User '" + username + "' not found");
        }

        existing.setUserPassword(newPassword);
        existing.setUserRole(Role.valueOf(newRole.toUpperCase()));
        _user.updateUser(existing);
    }

    public void deleteUser(String username) throws ValidationException{
        if(!_user.userExists(username)){
            throw new ValidationException("User '" + username + "' not found");
        }
        _user.deleteUser(username);
    }
}
