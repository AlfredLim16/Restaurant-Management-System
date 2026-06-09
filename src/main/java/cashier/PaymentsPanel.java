package cashier;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.PaymentFailedException;
import user.ValidationException;

public class PaymentsPanel extends JPanel implements ActionListener {

    private JSeparator separator;
    private JSeparator separator2;
    private JTable tablePending, tablePayments;
    private DefaultTableCellRenderer centerRenderer;
    private JScrollPane scrollPending, scrollPayments;
    private DefaultTableModel modelPending, modelPayments;
    private JButton btnProcess, btnRefund, btnHistory, btnToday;
    private JLabel lblDateShowing, lblTitle, lblSubTitle, lblPending, lblPendingHint, lblTransactions;

    private final JFrame parentFrame;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final String[] PAYMENT_METHODS;
    private LocalDate currentViewDate = LocalDate.now();

    public PaymentsPanel(PaymentService paymentService, OrderService orderService, JFrame parentFrame){
        this.PAYMENT_METHODS = new String[]{"Cash", "Card", "GCash", "Maya"};
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        Payments();
    }

    private void Payments(){
        lblTitle = new JLabel("Payments");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Process payments and view transaction history");
        lblSubTitle.setBounds(20, 35, 400, 20);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 60, 840, 1);
        separator.setForeground(Color.LIGHT_GRAY);
        add(separator);

        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        lblPending = new JLabel("Pending Orders");
        lblPending.setBounds(20, 70, 200, 25);
        lblPending.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblPending);

        lblPendingHint = new JLabel("Select an order and click Process to collect payment");
        lblPendingHint.setBounds(20, 92, 500, 18);
        lblPendingHint.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPendingHint.setForeground(Color.GRAY);
        add(lblPendingHint);

        btnProcess = new JButton("Process Payment");
        btnProcess.setBounds(710, 75, 150, 30);
        btnProcess.addActionListener(this);
        add(btnProcess);

        modelPending = new DefaultTableModel(new String[]{"Order ID", "Table", "Items", "Total", "Status", "Time"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };

        tablePending = new JTable(modelPending);
        tablePending.setRowHeight(26);
        tablePending.setFillsViewportHeight(true);
        tablePending.getTableHeader().setReorderingAllowed(false);
        tablePending.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 13));
        tablePending.getTableHeader().setPreferredSize(new Dimension(tablePending.getPreferredSize().width, 26));
        tablePending.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for(int i = 0; i < tablePending.getColumnCount(); i++){
            tablePending.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        scrollPending = new JScrollPane(tablePending);
        scrollPending.setBounds(20, 115, 840, 150);
        add(scrollPending);

        separator2 = new JSeparator();
        separator2.setBounds(20, 275, 840, 1);
        separator2.setForeground(Color.LIGHT_GRAY);
        add(separator2);

        lblTransactions = new JLabel("Transactions");
        lblTransactions.setBounds(20, 283, 150, 25);
        lblTransactions.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblTransactions);

        lblDateShowing = new JLabel("Today");
        lblDateShowing.setBounds(175, 287, 340, 18);
        lblDateShowing.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDateShowing.setForeground(new Color(0, 100, 180));
        add(lblDateShowing);

        btnToday = new JButton("Today");
        btnToday.setBounds(530, 284, 80, 28);
        btnToday.setFont(new Font("Arial", Font.PLAIN, 12));
        btnToday.addActionListener(this);
        add(btnToday);

        btnHistory = new JButton("Pick Date");
        btnHistory.setBounds(620, 284, 100, 28);
        btnHistory.setFont(new Font("Arial", Font.PLAIN, 12));
        btnHistory.addActionListener(this);
        add(btnHistory);

        btnRefund = new JButton("Refund");
        btnRefund.setBounds(730, 284, 130, 28);
        btnRefund.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRefund.addActionListener(this);
        add(btnRefund);

        modelPayments = new DefaultTableModel(new String[]{"ID", "Order", "Table", "Amount", "Tip", "Total", "Method", "Status", "Time"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };

        tablePayments = new JTable(modelPayments);
        tablePayments.setRowHeight(26);
        tablePayments.setFillsViewportHeight(true);
        tablePayments.getTableHeader().setReorderingAllowed(false);
        tablePayments.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 13));
        tablePayments.getTableHeader().setPreferredSize(new Dimension(tablePayments.getPreferredSize().width, 26));
        tablePayments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for(int i = 0; i < tablePayments.getColumnCount(); i++){
            tablePayments.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        scrollPayments = new JScrollPane(tablePayments);
        scrollPayments.setBounds(20, 318, 840, 155);
        add(scrollPayments);

        refreshPayments();
    }

    public void refreshPayments(){
        refreshPendingOrders();
        refreshTransactions(currentViewDate);
    }

    private void refreshPendingOrders(){
        modelPending.setRowCount(0);
        ArrayList<Order> eligible = orderService.getOrdersByStatus(OrderStatus.SERVED);
        ArrayList<Order> readyOrders = orderService.getOrdersByStatus(OrderStatus.READY);
        for(int i = 0; i < readyOrders.size(); i++){
            eligible.add(readyOrders.get(i));
        }
        for(int i = 0; i < eligible.size(); i++){
            Order order = eligible.get(i);
            if(hasCompletedPayment(order.getOrderId())){
                continue;
            }
            String items = ItemsSummary(order);
            String time;
            if(order.getOrderCreatedTime() != null){
                time = order.getOrderCreatedTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            }else{
                time = "-";
            }
            modelPending.addRow(new Object[]{
                order.getOrderId(),
                order.getOrderTableNumber(),
                items,
                String.format("%.2f", order.getOrderTotalAmount()),
                order.getOrderStatus(),
                time
            });
        }
    }

    private void refreshTransactions(LocalDate date){
        modelPayments.setRowCount(0);
        ArrayList<Payment> payments = paymentService.getPaymentsByDate(date);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        for(int i = 0; i < payments.size(); i++){
            Payment payment = payments.get(i);
            double total = paymentService.getTotalWithTip(payment);
            String tableNum = getTableNumberForOrder(payment.getLinkedOrderId());
            String time;
            if(payment.getPaymentTimestamp() != null){
                time = payment.getPaymentTimestamp().format(timeFmt);
            }else{
                time = "-";
            }
            modelPayments.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getLinkedOrderId(),
                tableNum,
                String.format("%.2f", payment.getPaymentAmount()),
                String.format("%.2f", payment.getPaymentTipAmount()),
                String.format("%.2f", total),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                time
            });
        }
        if(date.equals(LocalDate.now())){
            lblDateShowing.setText("Showing: Today  (" + payments.size() + " transaction/s)");
        }else{
            lblDateShowing.setText("Showing: " + date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + "  (" + payments.size() + " transaction/s)");
        }
    }

    private void processPayment(){
        int row = tablePending.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an order from the list above first.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) modelPending.getValueAt(row, 0);
        String table = (String) modelPending.getValueAt(row, 1);
        String items = (String) modelPending.getValueAt(row, 2);
        String amountStr = (String) modelPending.getValueAt(row, 3);

        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(340, 185));

        JLabel lOrderId = new JLabel("Order ID:");
        lOrderId.setBounds(10, 10, 100, 25);
        panel.add(lOrderId);

        JLabel vOrderId = new JLabel(String.valueOf(orderId));
        vOrderId.setBounds(120, 10, 210, 25);
        panel.add(vOrderId);

        JLabel lTable = new JLabel("Table:");
        lTable.setBounds(10, 40, 100, 25);
        panel.add(lTable);

        JLabel vTable = new JLabel(table);
        vTable.setBounds(120, 40, 210, 25);
        panel.add(vTable);

        JLabel lItems = new JLabel("Items:");
        lItems.setBounds(10, 70, 100, 25);
        panel.add(lItems);

        JLabel vItems = new JLabel(items);
        vItems.setBounds(120, 70, 210, 25);
        panel.add(vItems);

        JLabel lAmount = new JLabel("Amount:");
        lAmount.setBounds(10, 100, 100, 25);
        panel.add(lAmount);

        JLabel vAmount = new JLabel(amountStr);
        vAmount.setBounds(120, 100, 210, 25);
        panel.add(vAmount);

        JLabel lTip = new JLabel("Tip :");
        lTip.setBounds(10, 130, 100, 25);
        panel.add(lTip);

        JTextField tipField = new JTextField("0.00");
        tipField.setBounds(120, 130, 210, 25);
        panel.add(tipField);

        JLabel lMethod = new JLabel("Method:");
        lMethod.setBounds(10, 160, 100, 25);
        panel.add(lMethod);

        JComboBox methodBox = new JComboBox(PAYMENT_METHODS);
        methodBox.setBounds(120, 160, 210, 25);
        panel.add(methodBox);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Process Payment Order #" + orderId, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result != JOptionPane.OK_OPTION){
            return;
        }

        try{
            double tip = Double.parseDouble(tipField.getText().trim());
            String method = ((String) methodBox.getSelectedItem()).toLowerCase();
            paymentService.processPayment(orderId, tip, method);
            currentViewDate = LocalDate.now();
            refreshPayments();
            JOptionPane.showMessageDialog(parentFrame, "Payment processed successfully for Order #" + orderId + ".", "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid tip amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }catch(PaymentFailedException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Payment Error", JOptionPane.ERROR_MESSAGE);
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refundPayment(){
        int row = tablePayments.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select a transaction to refund.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int payId = (int) modelPayments.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Refund payment #" + payId + "?", "Confirm Refund", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        try{
            paymentService.refundPayment(payId);
            refreshPayments();
            JOptionPane.showMessageDialog(parentFrame, "Refund processed.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pickHistoryDate(){
        String input = JOptionPane.showInputDialog(parentFrame, "Enter date (YYYY-MM-DD):", "View Payment History", JOptionPane.PLAIN_MESSAGE);
        if(input == null || input.trim().isEmpty()){
            return;
        }
        try{
            currentViewDate = LocalDate.parse(input.trim());
            refreshTransactions(currentViewDate);
        }catch(DateTimeParseException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean hasCompletedPayment(int orderId){
        ArrayList<Payment> allPayments = paymentService.getAllPayments();
        for(int i = 0; i < allPayments.size(); i++){
            Payment p = allPayments.get(i);
            if(p.getLinkedOrderId() == orderId && PaymentStatus.COMPLETED.equals(p.getPaymentStatus())){
                return true;
            }
        }
        return false;
    }

    private String ItemsSummary(Order order){
        if(order.getOrderItems() == null || order.getOrderItems().isEmpty()){
            return "-";
        }
        String result = "";
        for(int i = 0; i < order.getOrderItems().size(); i++){
            OrderItem item = order.getOrderItems().get(i);
            if(result.length() > 0){
                result = result + ", ";
            }
            String name;
            if(item.getLinkedMenuItem() != null){
                name = item.getLinkedMenuItem().getMenuItemName();
            }else{
                name = "?";
            }
            result = result + item.getOrderItemQuantity() + "x " + name;
        }
        return result;
    }

    private String getTableNumberForOrder(int orderId){
        ArrayList<Order> allOrders = orderService.getAllOrders();
        for(int i = 0; i < allOrders.size(); i++){
            Order o = allOrders.get(i);
            if(o.getOrderId() == orderId){
                return o.getOrderTableNumber();
            }
        }
        return "-";
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnProcess){
            processPayment();
        }else if(e.getSource() == btnRefund){
            refundPayment();
        }else if(e.getSource() == btnHistory){
            pickHistoryDate();
        }else if(e.getSource() == btnToday){
            currentViewDate = LocalDate.now();
            refreshTransactions(currentViewDate);
        }
    }
}
