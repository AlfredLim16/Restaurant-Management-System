package cashier;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.PaymentFailedException;
import user.ValidationException;

public class PaymentsPanel extends JPanel implements ActionListener {

    private JTable tablePayments;
    private JSeparator separator;
    private final JFrame parentFrame;
    private JScrollPane scrollPayments;
    private JLabel lblTitle, lblSubTitle;
    private JButton btnProcess, btnRefund;
    private DefaultTableModel modelPayments;
    private final PaymentService paymentService;
    private DefaultTableCellRenderer centerCollumn;
    private static final String[] PAY_METHODS = {"cash", "card", "GCash", "Maya"};

    public PaymentsPanel(PaymentService paymentService, JFrame parentFrame) {
        this.paymentService = paymentService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        Payments();
    }

    private void Payments() {
        lblTitle = new JLabel("Payments");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Process payments and view transaction history");
        lblSubTitle.setBounds(20, 35, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 70, 840, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        btnProcess = new JButton("Process");
        btnProcess.setBounds(20, 80, 90, 30);
        btnProcess.addActionListener(this);
        add(btnProcess);

        btnRefund = new JButton("Refund");
        btnRefund.setBounds(120, 80, 90, 30);
        btnRefund.addActionListener(this);
        add(btnRefund);
        
        modelPayments = new DefaultTableModel(new String[]{"ID", "Order ID", "Amount", "Tip", "Total", "Method", "Status", "TXN"}, 0){
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };

        tablePayments = new JTable(modelPayments);
        tablePayments.setRowHeight(28);
        tablePayments.setFillsViewportHeight(true);
        tablePayments.getTableHeader().setReorderingAllowed(false);
        tablePayments.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tablePayments.getTableHeader().setPreferredSize(new Dimension(tablePayments.getPreferredSize().width, 28));

        centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tablePayments.getColumnCount(); i++){
            tablePayments.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollPayments = new JScrollPane(tablePayments);
        scrollPayments.setBounds(20, 120, 840, 350);
        add(scrollPayments);

        refreshPayments();
    }

    public void refreshPayments(){
        modelPayments.setRowCount(0);
        ArrayList<Payment> payments = paymentService.getAllPayments();
        for(Payment payment : payments){
            double total = paymentService.getTotalWithTip(payment);
            modelPayments.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getLinkedOrderId(),
                String.format("%.2f", payment.getPaymentAmount()),
                String.format("%.2f", payment.getPaymentTipAmount()),
                String.format("%.2f", total),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getPaymentTransactionId()
            });
        }
    }

    private void processPayment(){
        String orderIdString = JOptionPane.showInputDialog(parentFrame, "Order ID:");
        if(orderIdString == null){
            return;
        }
        String tipString = JOptionPane.showInputDialog(parentFrame, "Tip amount:");
        if(tipString == null){
            return;
        }
        String method = (String) JOptionPane.showInputDialog(parentFrame, "Method:", "Payment", JOptionPane.PLAIN_MESSAGE, null, PAY_METHODS, PAY_METHODS[0]);
        if(method == null){
            return;
        }
        try{
            int orderId = Integer.parseInt(orderIdString);
            double tip = Double.parseDouble(tipString);
            paymentService.processPayment(orderId, tip, method);
            refreshPayments();
            JOptionPane.showMessageDialog(parentFrame, "Payment processed.");
        }catch(HeadlessException | NumberFormatException | PaymentFailedException | ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refundPayment(){
        int row = tablePayments.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select a payment.");
            return;
        }
        int payId = (int) modelPayments.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Refund payment #" + payId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        try{
            paymentService.refundPayment(payId);
            refreshPayments();
            JOptionPane.showMessageDialog(parentFrame, "Refunded.");
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnProcess){
            processPayment();
        }else if(e.getSource() == btnRefund){
            refundPayment();
        }
    }

}