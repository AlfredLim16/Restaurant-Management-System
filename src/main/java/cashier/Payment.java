package cashier;

import java.time.LocalDateTime;

public class Payment {

    private int paymentId;
    private int linkedOrderId;
    private double paymentAmount;
    private double paymentTipAmount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentTimestamp;
    private String paymentTransactionId;

    public Payment(){

    }

    public Payment(int paymentId, int linkedOrderId, double paymentAmount, double paymentTipAmount, String paymentMethod, String paymentStatus, LocalDateTime paymentTimestamp, String paymentTransactionId){
        this.paymentId = paymentId;
        this.linkedOrderId = linkedOrderId;
        this.paymentAmount = paymentAmount;
        this.paymentTipAmount = paymentTipAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentTimestamp = paymentTimestamp;
        this.paymentTransactionId = paymentTransactionId;
    }

    public int getPaymentId(){
        return paymentId;
    }
    public void setPaymentId(int paymentId){
        this.paymentId = paymentId;
    }

    public int getLinkedOrderId(){
        return linkedOrderId;
    }
    public void setLinkedOrderId(int linkedOrderId){
        this.linkedOrderId = linkedOrderId;
    }

    public double getPaymentAmount(){
        return paymentAmount;
    }
    public void setPaymentAmount(double paymentAmount){
        this.paymentAmount = paymentAmount;
    }

    public double getPaymentTipAmount(){
        return paymentTipAmount;
    }
    public void setPaymentTipAmount(double paymentTipAmount){
        this.paymentTipAmount = paymentTipAmount;
    }

    public String getPaymentMethod(){
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus(){
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus){
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentTimestamp(){
        return paymentTimestamp;
    }
    public void setPaymentTimestamp(LocalDateTime paymentTimestamp){
        this.paymentTimestamp = paymentTimestamp;
    }

    public String getPaymentTransactionId(){
        return paymentTransactionId;
    }
    public void setPaymentTransactionId(String paymentTransactionId){
        this.paymentTransactionId = paymentTransactionId;
    }
}
