package cashier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import user.AbstractAppService;
import user.PaymentFailedException;
import user.ValidationException;

public class PaymentService extends AbstractAppService {

    private final IPayment _payment;
    private final IOrder _order;

    public PaymentService(IPayment payment, IOrder order){
        this._payment = payment;
        this._order = order;
    }

    public double getTotalWithTip(Payment paymentRecord){
        return paymentRecord.getPaymentAmount() + paymentRecord.getPaymentTipAmount();
    }

    public long getTransactionCount(){
        long completedTransactionCount = 0;
        for(Payment currentPayment : _payment.getAll()){
            if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                completedTransactionCount++;
            }
        }
        return completedTransactionCount;
    }

    public HashMap<String, Long> getCountByMethod(){
        HashMap<String, Long> paymentCountByMethod = new HashMap<>();
        for(Payment currentPayment : _payment.getAll()){
            String paymentMethod = currentPayment.getPaymentMethod();
            Long currentCount = paymentCountByMethod.get(paymentMethod);
            if(currentCount == null){
                paymentCountByMethod.put(paymentMethod, 1L);
            }else{
                paymentCountByMethod.put(paymentMethod, currentCount + 1);
            }
        }
        return paymentCountByMethod;
    }

    public int getTransactionCountAsInt(){
        return (int) getTransactionCount();
    }

    public ArrayList<Payment> getAllPayments(){
        return new ArrayList<>(_payment.getAll());
    }

    public ArrayList<Payment> getPaymentsByStatus(String targetStatus){
        return new ArrayList<>(_payment.findByStatus(targetStatus));
    }

    public ArrayList<Payment> getPendingPayments(){
        return new ArrayList<>(_payment.findByStatus(PaymentStatus.PENDING));
    }

    public ArrayList<Payment> getPaymentsByDate(LocalDate date){
        return new ArrayList<>(_payment.findByDate(date));
    }

    public Payment processPayment(int orderId, double tipAmount, String paymentMethod) throws ValidationException, PaymentFailedException{
        Order existingOrder = getOrderOrThrow(orderId);
        if(!existingOrder.getOrderStatus().equals(OrderStatus.SERVED) && !existingOrder.getOrderStatus().equals(OrderStatus.READY)){
            throw new ValidationException("Order must be served before payment");
        }else if(hasCompletedPayment(orderId)){
            throw new ValidationException("Order already paid");
        }else if(!isValidPaymentMethod(paymentMethod)){
            throw new ValidationException("Invalid payment method: " + paymentMethod);
        }

        ensureNotNegative(tipAmount, "Tip amount");

        Payment newRecord = new Payment();
        newRecord.setLinkedOrderId(orderId);
        newRecord.setPaymentAmount(existingOrder.getOrderTotalAmount());
        newRecord.setPaymentTipAmount(tipAmount);
        newRecord.setPaymentMethod(paymentMethod);
        newRecord.setPaymentTimestamp(LocalDateTime.now());
        newRecord.setPaymentTransactionId(generateTransactionId());

        try{
            boolean paymentSuccessful = simulatePaymentProcessing(paymentMethod);
            if(paymentSuccessful){
                newRecord.setPaymentStatus(PaymentStatus.COMPLETED);
                existingOrder.setOrderStatus(OrderStatus.COMPLETED);
                _order.update(existingOrder);
            }else{
                newRecord.setPaymentStatus(PaymentStatus.FAILED);
                throw new PaymentFailedException("Payment processing failed");
            }
        }catch(PaymentFailedException paymentException){
            newRecord.setPaymentStatus(PaymentStatus.FAILED);
            _payment.create(newRecord);
            throw new PaymentFailedException("Payment processing error: " + paymentException.getMessage(), paymentException);
        }
        _payment.create(newRecord);
        return newRecord;
    }

    public void refundPayment(int paymentIdentifier) throws ValidationException{
        Payment existingPaymentRecord = getPaymentOrThrow(paymentIdentifier);

        if(!PaymentStatus.COMPLETED.equals(existingPaymentRecord.getPaymentStatus())){
            throw new ValidationException("Can only refund completed payments");
        }
        existingPaymentRecord.setPaymentStatus(PaymentStatus.REFUNDED);
        _payment.update(existingPaymentRecord);

        Order relatedOrder = _order.get(existingPaymentRecord.getLinkedOrderId());
        if(relatedOrder != null){
            relatedOrder.setOrderStatus(OrderStatus.SERVED);
            _order.update(relatedOrder);
        }
    }

    private boolean hasCompletedPayment(int orderIdentifier){
        for(Payment currentPayment : _payment.findByOrderId(orderIdentifier)){
            if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                return true;
            }
        }
        return false;
    }

    private boolean isValidPaymentMethod(String methodToValidate){
        if(methodToValidate == null){
            return false;
        }
        String lowerCaseMethod = methodToValidate.toLowerCase();
        return lowerCaseMethod.equals(PaymentMethod.CASH) || lowerCaseMethod.equals(PaymentMethod.CARD) || lowerCaseMethod.equals(PaymentMethod.GCASH) || lowerCaseMethod.equals(PaymentMethod.MAYA);
    }

    private boolean simulatePaymentProcessing(String paymentMethod){
        return Math.random() < 0.95;
    }

    private String generateTransactionId(){
        String date = LocalDate.now().toString().replace("-", "");
        int random = (int) (Math.random() * 10000);
        return "RMS-TXN-" + date + "-" + random;
    }

    public double calculateDailyRevenue(LocalDate targetDate){
        double dailyRevenueTotal = 0.0;
        for(Payment currentPayment : _payment.findByDate(targetDate)){
            if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                dailyRevenueTotal += currentPayment.getPaymentAmount();
            }
        }
        return dailyRevenueTotal;
    }

    public double calculateDailyTips(LocalDate targetDate){
        double dailyTipsTotal = 0.0;
        for(Payment currentPayment : _payment.findByDate(targetDate)){
            if(PaymentStatus.COMPLETED.equals(currentPayment.getPaymentStatus())){
                dailyTipsTotal += currentPayment.getPaymentTipAmount();
            }
        }
        return dailyTipsTotal;
    }

    public double calculateAverageTransaction(){
        ArrayList<Payment> completedPaymentList = new ArrayList<>(_payment.findByStatus(PaymentStatus.COMPLETED));
        if(completedPaymentList.isEmpty()){
            return 0.0;
        }
        double accumulatedTotal = 0.0;
        for(Payment currentPayment : completedPaymentList){
            accumulatedTotal += currentPayment.getPaymentAmount();
        }
        return accumulatedTotal / completedPaymentList.size();
    }

    public double calculateTotalRevenue(){
        double accumulatedRevenue = 0.0;
        for(Payment completedPayment : _payment.findByStatus(PaymentStatus.COMPLETED)){
            accumulatedRevenue += completedPayment.getPaymentAmount();
        }
        return accumulatedRevenue;
    }

    public double calculateTotalTips(){
        double accumulatedTips = 0.0;
        for(Payment completedPayment : _payment.findByStatus(PaymentStatus.COMPLETED)){
            accumulatedTips += completedPayment.getPaymentTipAmount();
        }
        return accumulatedTips;
    }

    public HashMap<String, ArrayList<Payment>> groupByPaymentMethod(ArrayList<Payment> paymentList){
        HashMap<String, ArrayList<Payment>> paymentsGroupedByMethod = new HashMap<>();
        for(Payment currentPayment : paymentList){
            putIntoGroupedMap(paymentsGroupedByMethod, currentPayment.getPaymentMethod(), currentPayment);
        }
        return paymentsGroupedByMethod;
    }

    public HashMap<String, ArrayList<Payment>> groupByStatus(ArrayList<Payment> paymentList){
        HashMap<String, ArrayList<Payment>> paymentsGroupedByStatus = new HashMap<>();
        for(Payment currentPayment : paymentList){
            putIntoGroupedMap(paymentsGroupedByStatus, currentPayment.getPaymentStatus(), currentPayment);
        }
        return paymentsGroupedByStatus;
    }

    private Order getOrderOrThrow(int orderId) throws ValidationException{
        return getOrThrow(_order.get(orderId), "Order not found");
    }

    private Payment getPaymentOrThrow(int paymentId) throws ValidationException{
        return getOrThrow(_payment.get(paymentId), "Payment not found");
    }
}
