package user;

public class PermissionService extends AbstractAppService implements IPermission {

    private final IUser userStorage;

    public PermissionService(IUser user){
        this.userStorage = user;
    }

    @Override
    public boolean canAccessOrder(Role role){
        return role == Role.CASHIER;
    }

    @Override
    public boolean canAccessInventory(Role role){
        return role == Role.MANAGER;
    }

    @Override
    public boolean canAccessPayment(Role role){
        return role == Role.CASHIER;
    }

    @Override
    public boolean canAccessReports(Role role){
        return role == Role.MANAGER;
    }

    @Override
    public boolean canAccessFoodWaste(Role role){
        return role == Role.MANAGER;
    }

    @Override
    public boolean canAccessSettings(Role role){
        return role == Role.MANAGER;
    }
}
