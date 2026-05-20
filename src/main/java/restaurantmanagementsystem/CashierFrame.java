package restaurantsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import appservice.RestaurantAppService.*;
import dataservice.RestaurantInMemory;
import dataservice.RestaurantDataService;
import dataservice.RestaurantDataService.IPermission;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import model.RestaurantModel.*;

public class CashierFrame extends JFrame implements ActionListener {

    private OrderService orderService;
    private PaymentService paymentService;

    private JButton btnOrders, btnPayments, btnLogout;
    private JPanel panelOrders, panelPayments;

    private JTable tableOrders;
    private DefaultTableModel modelOrders;
    private JScrollPane scrollOrders;
    private JButton btnNewOrder, btnUpdateStatus, btnViewDetails;

    private JTable tablePayments;
    private DefaultTableModel modelPayments;
    private JScrollPane scrollPayments;
    private JButton btnProcess, btnRefund;

    private static final String[] PAY_METHODS = {"cash", "card", "GCash", "Maya"};

    public CashierFrame(User user, IPermission perm){
        setTitle("Restaurant System - Cashier");
        setLayout(null);
        setSize(935, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        Services();
        Navigation();
        Orders();
        Payments();

        showPanel(panelOrders);
    }

    private void Services(){
        RestaurantDataService.IOrder orderData = new RestaurantInMemory.InMemoryOrder();
        RestaurantDataService.IPayment paymentData = new RestaurantInMemory.InMemoryPayment();
        RestaurantDataService.IMenuItem menuData = new RestaurantInMemory.InMemoryMenuItem();
        orderService = new OrderService(orderData, menuData);
        paymentService = new PaymentService(paymentData, orderData);
    }

    private ImageIcon loadIcon(String filename, int size){
        URL url = getClass().getResource("/icons/" + filename);
        if(url == null){
            return null;
        }
        Image img = new ImageIcon(url).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void Navigation(){
        int x = 40, y = 10, w = 150, h = 35, gap = 15;

        btnOrders = new JButton("Orders", loadIcon("order.png", 18));
        btnOrders.setOpaque(true);
        btnOrders.setIconTextGap(12);
        btnOrders.setBounds(x, y, w, h);
        btnOrders.setFocusPainted(false);
        //btnOrders.setBorderPainted(false);
        btnOrders.setContentAreaFilled(false);
        btnOrders.setBackground(Color.WHITE);
        btnOrders.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOrders.setHorizontalAlignment(SwingConstants.LEFT);
        btnOrders.addActionListener(this);
        add(btnOrders);
        x += w + gap;

        btnPayments = new JButton("Payments", loadIcon("payment.png", 18));
        btnPayments.setOpaque(true);
        btnPayments.setIconTextGap(12);
        btnPayments.setBounds(x, y, w, h);
        btnPayments.setFocusPainted(false);
        //btnPayments.setBorderPainted(false);
        btnPayments.setContentAreaFilled(false);
        btnPayments.setBackground(Color.WHITE);
        btnPayments.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPayments.setHorizontalAlignment(SwingConstants.LEFT);
        btnPayments.addActionListener(this);
        add(btnPayments);
        x += w + gap;

        btnLogout = new JButton("Logout", loadIcon("log-out.png", 18));
        btnLogout.setOpaque(true);
        btnLogout.setIconTextGap(12);
        btnLogout.setBounds(x, y, w, h);
        btnLogout.setFocusPainted(false);
        //btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.addActionListener(this);
        add(btnLogout);
    }

    private void showPanel(JPanel p){
        panelOrders.setVisible(false);
        panelPayments.setVisible(false);
        p.setVisible(true);
    }

    private void Orders(){
        panelOrders = new JPanel(null);
        panelOrders.setBounds(20, 50, 860, 500);
        panelOrders.setBackground(Color.WHITE);
        add(panelOrders);

        JLabel lblTitle = new JLabel("Orders");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelOrders.add(lblTitle);

        JLabel lblSubTitle = new JLabel("Manage and track customer orders");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        panelOrders.add(lblSubTitle);

        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 840, 1);
        separator.setForeground(Color.BLACK);
        panelOrders.add(separator);

        modelOrders = new NonEditableModel(new String[]{"ID", "Table", "Status", "Total", "Date", "Items"}, 0);
        tableOrders = new JTable(modelOrders);
        tableOrders.setRowHeight(28);
        tableOrders.setFillsViewportHeight(true);
        tableOrders.getTableHeader().setReorderingAllowed(false);
        tableOrders.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableOrders.getTableHeader().setPreferredSize(new Dimension(tableOrders.getPreferredSize().width, 28));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(CENTER);
        for(int i = 0; i < tableOrders.getColumnCount(); i++){
            tableOrders.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        scrollOrders = new JScrollPane(tableOrders);
        scrollOrders.setBounds(20, 80, 840, 350);
        panelOrders.add(scrollOrders);

        btnNewOrder = new JButton("New Order");
        btnNewOrder.setBounds(20, 450, 100, 30);
        btnNewOrder.addActionListener(this);
        panelOrders.add(btnNewOrder);

        btnUpdateStatus = new JButton("Update Status");
        btnUpdateStatus.setBounds(130, 450, 150, 30);
        btnUpdateStatus.addActionListener(this);
        panelOrders.add(btnUpdateStatus);

        btnViewDetails = new JButton("View Details");
        btnViewDetails.setBounds(290, 450, 110, 30);
        btnViewDetails.addActionListener(this);
        panelOrders.add(btnViewDetails);

        refreshOrders();
    }

    private void refreshOrders(){
        modelOrders.setRowCount(0);
        ArrayList<Order> orders = orderService.getAllOrders();
        for(Order o : orders){
            int itemCount = 0;
            if(o.getOrderItems() != null){
                for(OrderItem oi : o.getOrderItems()){
                    itemCount += oi.getOrderItemQuantity();
                }
            }
            modelOrders.addRow(new Object[]{
                o.getOrderId(),
                o.getOrderTableNumber(),
                o.getOrderStatus(),
                String.format("%.2f", o.getOrderTotalAmount()),
                o.getOrderCreatedTime() != null ? o.getOrderCreatedTime().toLocalDate().toString() : "-",
                itemCount
            });
        }
    }

    private void Payments(){
        panelPayments = new JPanel(null);
        panelPayments.setBounds(20, 50, 860, 500);
        panelPayments.setBackground(Color.WHITE);
        add(panelPayments);

        JLabel lblTitle = new JLabel("Payments");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelPayments.add(lblTitle);

        JLabel lblSubTitle = new JLabel("Process payments and view transaction history");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        panelPayments.add(lblSubTitle);

        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 840, 1);
        separator.setForeground(Color.BLACK);
        panelPayments.add(separator);

        modelPayments = new NonEditableModel(new String[]{"ID", "Order ID", "Amount", "Tip", "Total", "Method", "Status", "TXN"}, 0);
        tablePayments = new JTable(modelPayments);
        tablePayments.setRowHeight(28);
        tablePayments.setFillsViewportHeight(true);
        tablePayments.getTableHeader().setReorderingAllowed(false);
        tablePayments.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tablePayments.getTableHeader().setPreferredSize(new Dimension(tablePayments.getPreferredSize().width, 28));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(CENTER);
        for(int i = 0; i < tablePayments.getColumnCount(); i++){
            tablePayments.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        scrollPayments = new JScrollPane(tablePayments);
        scrollPayments.setBounds(20, 80, 840, 350);
        panelPayments.add(scrollPayments);

        btnProcess = new JButton("Process");
        btnProcess.setBounds(20, 450, 90, 30);
        btnProcess.addActionListener(this);
        panelPayments.add(btnProcess);

        btnRefund = new JButton("Refund");
        btnRefund.setBounds(120, 450, 90, 30);
        btnRefund.addActionListener(this);
        panelPayments.add(btnRefund);

        refreshPayments();
    }

    private void refreshPayments(){
        modelPayments.setRowCount(0);
        ArrayList<Payment> payments = paymentService.getAllPayments();
        for(Payment p : payments){
            double total = paymentService.getTotalWithTip(p);
            modelPayments.addRow(new Object[]{
                p.getPaymentId(),
                p.getLinkedOrderId(),
                String.format("%.2f", p.getPaymentAmount()),
                String.format("%.2f", p.getPaymentTipAmount()),
                String.format("%.2f", total),
                p.getPaymentMethod(),
                p.getPaymentStatus(),
                p.getPaymentTransactionId()
            });
        }
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnOrders){
            showPanel(panelOrders);
            refreshOrders();
        }else if(e.getSource() == btnPayments){
            showPanel(panelPayments);
            refreshPayments();
        }else if(e.getSource() == btnLogout){
            System.exit(0);
        }else if(e.getSource() == btnNewOrder){
            new NewOrderDialog(this).setVisible(true);
        }else if(e.getSource() == btnUpdateStatus){
            updateOrderStatus();
        }else if(e.getSource() == btnViewDetails){
            viewOrderDetails();
        }else if(e.getSource() == btnProcess){
            processPayment();
        }else if(e.getSource() == btnRefund){
            refundPayment();
        }
    }

    private String[] getNextStatus(String current){
        if(current.equals("Preparing")){
            return new String[]{"Ready", "Cancelled"};
        }
        if(current.equals("Ready")){
            return new String[]{"Served", "Cancelled"};
        }
        if(current.equals("Served")){
            return new String[]{"Completed"};
        }
        return new String[]{};
    }

    private void updateOrderStatus(){
        int row = tableOrders.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select an order.");
            return;
        }
        int orderId = (int) modelOrders.getValueAt(row, 0);
        String current = (String) modelOrders.getValueAt(row, 2);
        String[] next = getNextStatus(current);
        if(next.length == 0){
            JOptionPane.showMessageDialog(this, "No transitions available.");
            return;
        }
        String chosen = (String) JOptionPane.showInputDialog(this, "Select new status:", "Update", JOptionPane.PLAIN_MESSAGE, null, next, next[0]);
        if(chosen == null){
            return;
        }
        try{
            orderService.updateOrderStatus(orderId, chosen);
            refreshOrders();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewOrderDetails(){
        int row = tableOrders.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select an order.");
            return;
        }
        int orderId = (int) modelOrders.getValueAt(row, 0);
        Order order = null;
        for(Order o : orderService.getAllOrders()){
            if(o.getOrderId() == orderId){
                order = o;
                break;
            }
        }
        if(order != null){
            new OrderDetailsDialog(this, order).setVisible(true);
        }
    }

    private void processPayment(){
        String orderIdStr = JOptionPane.showInputDialog(this, "Order ID:");
        if(orderIdStr == null){
            return;
        }
        String tipStr = JOptionPane.showInputDialog(this, "Tip amount:");
        if(tipStr == null){
            return;
        }
        String method = (String) JOptionPane.showInputDialog(this, "Method:", "Payment", JOptionPane.PLAIN_MESSAGE, null, PAY_METHODS, PAY_METHODS[0]);
        if(method == null){
            return;
        }
        try{
            int orderId = Integer.parseInt(orderIdStr);
            double tip = Double.parseDouble(tipStr);
            paymentService.processPayment(orderId, tip, method);
            refreshPayments();
            JOptionPane.showMessageDialog(this, "Payment processed.");
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refundPayment(){
        int row = tablePayments.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select a payment.");
            return;
        }
        int payId = (int) modelPayments.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Refund payment #" + payId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        try{
            paymentService.refundPayment(payId);
            refreshPayments();
            JOptionPane.showMessageDialog(this, "Refunded.");
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class NewOrderDialog extends JDialog implements ActionListener {

        private JTextField txtTable;
        private JComboBox<String> comboMenu;
        private JSpinner spinQty;
        private JTable tableItems;
        private DefaultTableModel modelItems;
        private JLabel lblTotal;
        private JButton btnAdd, btnPlace, btnCancel;
        private ArrayList<OrderItem> selectedItems;
        private ArrayList<model.RestaurantModel.MenuItem> availableMenu;
        private double currentTotal;

        public NewOrderDialog(JFrame parent){
            super(parent, "New Order", true);
            setLayout(null);
            setSize(600, 500);
            setLocationRelativeTo(parent);

            selectedItems = new ArrayList<OrderItem>();
            availableMenu = orderService.getAvailableMenuItems();
            currentTotal = 0.0;

            JLabel lblTable = new JLabel("Table:");
            lblTable.setBounds(20, 20, 80, 25);
            add(lblTable);

            txtTable = new JTextField();
            txtTable.setBounds(100, 20, 150, 25);
            add(txtTable);

            JLabel lblItem = new JLabel("Item:");
            lblItem.setBounds(20, 55, 80, 25);
            add(lblItem);

            String[] names = new String[availableMenu.size()];
            for(int i = 0; i < availableMenu.size(); i++){
                model.RestaurantModel.MenuItem m = availableMenu.get(i);
                names[i] = m.getMenuItemName() + " - " + String.format("%.2f", m.getMenuItemPrice());
            }
            comboMenu = new JComboBox<String>(names);
            comboMenu.setBounds(100, 55, 250, 25);
            add(comboMenu);

            JLabel lblQty = new JLabel("Qty:");
            lblQty.setBounds(360, 55, 50, 25);
            add(lblQty);

            spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
            spinQty.setBounds(410, 55, 60, 25);
            add(spinQty);

            btnAdd = new JButton("Add");
            btnAdd.setBounds(480, 55, 80, 25);
            btnAdd.addActionListener(this);
            add(btnAdd);

            modelItems = new NonEditableModel(new String[]{"Item", "Qty", "Price", "Subtotal"}, 0);
            tableItems = new JTable(modelItems);
            tableItems.setRowHeight(25);
            JScrollPane sp = new JScrollPane(tableItems);
            sp.setBounds(20, 95, 540, 250);
            add(sp);

            lblTotal = new JLabel("Total: 0.00");
            lblTotal.setBounds(20, 360, 200, 30);
            lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
            add(lblTotal);

            btnPlace = new JButton("Place Order");
            btnPlace.setBounds(300, 400, 120, 30);
            btnPlace.addActionListener(this);
            add(btnPlace);

            btnCancel = new JButton("Cancel");
            btnCancel.setBounds(430, 400, 100, 30);
            btnCancel.addActionListener(this);
            add(btnCancel);
        }

        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == btnAdd){
                int idx = comboMenu.getSelectedIndex();
                if(idx < 0){
                    return;
                }
                int qty = (int) spinQty.getValue();
                model.RestaurantModel.MenuItem chosen = availableMenu.get(idx);
                double price = chosen.getMenuItemPrice();

                boolean found = false;
                for(OrderItem oi : selectedItems){
                    if(oi.getLinkedMenuItem().getMenuItemId() == chosen.getMenuItemId()){
                        oi.setOrderItemQuantity(oi.getOrderItemQuantity() + qty);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    selectedItems.add(new OrderItem("OI-" + (selectedItems.size() + 1), chosen, qty));
                }
                refreshItems();
            }else if(e.getSource() == btnPlace){
                String table = txtTable.getText().trim();
                if(table.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Table number required.");
                    return;
                }
                if(selectedItems.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Add at least one item.");
                    return;
                }
                try{
                    orderService.createOrder(table, selectedItems);
                    refreshOrders();
                    dispose();
                    JOptionPane.showMessageDialog(CashierFrame.this, "Order placed.");
                }catch(ValidationException ex){
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }else if(e.getSource() == btnCancel){
                dispose();
            }
        }

        private void refreshItems(){
            modelItems.setRowCount(0);
            currentTotal = 0.0;
            for(OrderItem oi : selectedItems){
                double price = oi.getLinkedMenuItem().getMenuItemPrice();
                double sub = price * oi.getOrderItemQuantity();
                currentTotal += sub;
                modelItems.addRow(new Object[]{
                    oi.getLinkedMenuItem().getMenuItemName(),
                    oi.getOrderItemQuantity(),
                    String.format("%.2f", price),
                    String.format("%.2f", sub)
                });
            }
            lblTotal.setText("Total: " + String.format("%.2f", currentTotal));
        }
    }

    class OrderDetailsDialog extends JDialog implements ActionListener {

        private JButton btnClose;

        public OrderDetailsDialog(JFrame parent, Order order){
            super(parent, "Order Details", true);
            setLayout(null);
            setSize(400, 400);
            setLocationRelativeTo(parent);
            getContentPane().setBackground(Color.WHITE);

            JLabel lblInfo = new JLabel("Order #" + order.getOrderId() + " Table " + order.getOrderTableNumber());
            lblInfo.setBounds(20, 20, 350, 25);
            add(lblInfo);

            DefaultTableModel model = new NonEditableModel(new String[]{"Qty", "Item", "Price", "Subtotal"}, 0);
            JTable table = new JTable(model);
            table.setRowHeight(28);
            table.setFillsViewportHeight(true);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
            table.getTableHeader().setPreferredSize(new Dimension(table.getPreferredSize().width, 28));
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(CENTER);
            for(int i = 0; i < table.getColumnCount(); i++){
                table.getColumnModel().getColumn(i).setCellRenderer(center);
            }
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(20, 55, 350, 220);
            add(scrollPane);

            if(order.getOrderItems() != null){
                for(OrderItem oi : order.getOrderItems()){
                    double price = oi.getLinkedMenuItem() != null ? oi.getLinkedMenuItem().getMenuItemPrice() : 0;
                    model.addRow(new Object[]{
                        oi.getOrderItemQuantity(),
                        oi.getLinkedMenuItem() != null ? oi.getLinkedMenuItem().getMenuItemName() : "-",
                        String.format("%.2f", price),
                        String.format("%.2f", price * oi.getOrderItemQuantity())
                    });
                }
            }

            btnClose = new JButton("Close");
            btnClose.setBounds(280, 300, 90, 30);
            btnClose.setBackground(Color.RED);
            btnClose.setForeground(Color.WHITE);
            btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnClose.addActionListener(this);
            btnClose.addActionListener(this);
            add(btnClose);
        }

        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == btnClose){
                dispose();
            }
        }
    }

    private class NonEditableModel extends DefaultTableModel {

        public NonEditableModel(String[] cols, int rows){
            super(cols, rows);
        }
        @Override
        public boolean isCellEditable(int r, int c){
            return false;
        }
    }

}
