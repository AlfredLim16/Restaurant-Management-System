package user;

public class AuthenticationService extends AbstractAppService implements IAuthentication {

    private final IUser _user;

    public AuthenticationService(IUser userData){
        this._user = userData;
    }

    // returns the matching User if username and password are correct; otherwise returns null
    @Override
    public User login(String username, String password){
        User user = _user.findByUsername(username);
        if(user != null && user.getUserPassword().equals(password)){
            return user;
        }
        return null;
    }
}
