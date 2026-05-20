package cashier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import user.AbstractDataService;

public class InMemoryPayment extends AbstractDataService<Payment> implements IPayment {

    private final ArrayList<Payment> orderPayments = new ArrayList<>();
    private final ArrayList<Payment> matchingPayments = new ArrayList<>();
    private final ArrayList<Payment> datePayments = new ArrayList<>();
    private final ArrayList<Payment> methodPayments = new ArrayList<>();

    public InMemoryPayment(){
        samplePaymentData();
    }

    private void samplePaymentData(){
        /*Payment 1: ID 0 (auto), Order ID 1, Amount 140.00, Tip 10.00, Method cash, Status Completed, Timestamp 90 minutes ago, TXN RMS-TXN-20260515-1234
            Successful payment. AppService automatically sets the linked order to Completed. */
        create(new Payment(0, 1, 140.00, 10.00, "cash", "Completed", LocalDateTime.now().minusMinutes(90), "RMS-TXN-20260515-1234"));

        /*Payment 2: ID 0 (auto), Order ID 2, Amount 50.00, Tip 5.00, Method GCash, Status Failed, Timestamp 45 minutes ago, TXN RMS-TXN-20260515-5678
            Failed payment. AppService leaves the linked order as Served so it can be paid again. */
        create(new Payment(0, 2, 50.00, 5.00, "GCash", "Failed", LocalDateTime.now().minusMinutes(45), "RMS-TXN-20260515-5678"));
    }

    @Override
    protected int getModelId(Payment paymentRecord){
        return paymentRecord.getPaymentId();
    }

    @Override
    protected void setModelId(Payment paymentRecord, int paymentId){
        paymentRecord.setPaymentId(paymentId);
    }

    @Override
    public ArrayList<Payment> findByDate(LocalDate targetDate){
        datePayments.clear();
        for(Payment currentPayment : storage.values()){
            LocalDate paymentDate = currentPayment.getPaymentTimestamp().toLocalDate();
            if(paymentDate.equals(targetDate)){
                datePayments.add(currentPayment);
            }
        }
        return datePayments;
    }

    @Override
    public ArrayList<Payment> findByOrderId(int targetOrderId){
        orderPayments.clear();
        for(Payment currentPayment : storage.values()){
            if(currentPayment.getLinkedOrderId() == targetOrderId){
                orderPayments.add(currentPayment);
            }
        }
        return orderPayments;
    }

    @Override
    public ArrayList<Payment> findByPaymentMethod(String targetPaymentMethod){
        methodPayments.clear();
        for(Payment currentPayment : storage.values()){
            if(currentPayment.getPaymentMethod().equals(targetPaymentMethod)){
                methodPayments.add(currentPayment);
            }
        }
        return methodPayments;
    }

    @Override
    public ArrayList<Payment> findByStatus(String targetStatus){
        matchingPayments.clear();
        for(Payment currentPayment : storage.values()){
            if(currentPayment.getPaymentStatus().equals(targetStatus)){
                matchingPayments.add(currentPayment);
            }
        }
        return matchingPayments;
    }
}
