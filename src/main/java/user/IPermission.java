package user;

public interface IPermission {

    boolean canAccessOrder(Role role);
    boolean canAccessInventory(Role role);
    boolean canAccessPayment(Role role);
    boolean canAccessReports(Role role);
    boolean canAccessFoodWaste(Role role);
    boolean canAccessSettings(Role role);
}
