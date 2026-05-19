package user;

public class InsufficientInventoryException extends BusinessException {

    public InsufficientInventoryException(String message){
        super(message);
    }

    public InsufficientInventoryException(String message, Throwable cause){
        super(message, cause);
    }
}
