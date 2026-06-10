package cashier;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.ValidationException;

public class OrdersPanel extends JPanel implements ActionListener {

    private JSeparator separator;
    private JTable tableOrders;
    private final JFrame parentFrame;
    private JScrollPane scrollOrders;
    private DefaultTableModel modelOrders;
    private JLabel lblTitle, lblSubTitle;
    private final OrderService orderService;
    private DefaultTableCellRenderer centerCollumn;
    private JButton btnNewOrder, btnUpdateStatus, btnViewDetails;

    public OrdersPanel(OrderService orderService, JFrame parentFrame){
        this.orderService = orderService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        Orders();
    }

    private void Orders(){
        lblTitle = new JLabel("Orders");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage and track customer orders");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 70, 840, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        btnNewOrder = new JButton("New Order");
        btnNewOrder.setBounds(20, 80, 100, 30);
        btnNewOrder.addActionListener(this);
        add(btnNewOrder);

        btnUpdateStatus = new JButton("Update Status");
        btnUpdateStatus.setBounds(130, 80, 150, 30);
        btnUpdateStatus.addActionListener(this);
        add(btnUpdateStatus);

        btnViewDetails = new JButton("View Details");
        btnViewDetails.setBounds(290, 80, 110, 30);
        btnViewDetails.addActionListener(this);
        add(btnViewDetails);

        modelOrders = new DefaultTableModel(new String[]{"ID", "Table", "Status", "Total", "Date", "Items"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };

        tableOrders = new JTable(modelOrders);
        tableOrders.setRowHeight(28);
        tableOrders.setFillsViewportHeight(true);
        tableOrders.getTableHeader().setReorderingAllowed(false);
        tableOrders.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableOrders.getTableHeader().setPreferredSize(new Dimension(tableOrders.getPreferredSize().width, 28));

        centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tableOrders.getColumnCount(); i++){
            tableOrders.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }

        scrollOrders = new JScrollPane(tableOrders);
        scrollOrders.setBounds(20, 120, 840, 350);
        add(scrollOrders);

        refreshOrders();
    }

    public void refreshOrders(){
        modelOrders.setRowCount(0);
        ArrayList<Order> orders = orderService.getAllOrders();
        for(int i = 0; i < orders.size(); i++){
            Order order = orders.get(i);
            int itemCount = 0;
            if(order.getOrderItems() != null){
                for(int j = 0; j < order.getOrderItems().size(); j++){
                    itemCount += order.getOrderItems().get(j).getOrderItemQuantity();
                }
            }
            String dateText;
            if(order.getOrderCreatedTime() != null){
                dateText = order.getOrderCreatedTime().toLocalDate().toString();
            }else{
                dateText = "-";
            }
            modelOrders.addRow(new Object[]{
                order.getOrderId(),
                order.getOrderTableNumber(),
                order.getOrderStatus(),
                String.format("%.2f", order.getOrderTotalAmount()),
                dateText,
                itemCount
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnNewOrder){
            new NewOrderDialog(parentFrame).setVisible(true);
        }else if(e.getSource() == btnUpdateStatus){
            updateOrderStatus();
        }else if(e.getSource() == btnViewDetails){
            viewOrderDetails();
        }
    }

    private String[] getNextStatus(String current){
        if(current.equals("Preparing")){
            return new String[]{"Ready", "Cancelled"};
        }
        if(current.equals("Ready")){
            return new String[]{"Served", "Cancelled"};
        }
        return new String[]{};
    }

    private void updateOrderStatus(){
        int row = tableOrders.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an order.");
            return;
        }
        int orderId = (int) modelOrders.getValueAt(row, 0);
        String current = (String) modelOrders.getValueAt(row, 2);
        String[] next = getNextStatus(current);
        if(next.length == 0){
            JOptionPane.showMessageDialog(parentFrame, "No transitions available.");
            return;
        }
        String chosen = (String) JOptionPane.showInputDialog(parentFrame, "Select new status:", "Update", JOptionPane.PLAIN_MESSAGE, null, next, next[0]);
        if(chosen == null){
            return;
        }
        try{
            orderService.updateOrderStatus(orderId, chosen);
            refreshOrders();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewOrderDetails(){
        int row = tableOrders.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an order.");
            return;
        }
        int orderId = (int) modelOrders.getValueAt(row, 0);
        Order order = null;
        ArrayList<Order> allOrders = orderService.getAllOrders();
        for(int i = 0; i < allOrders.size(); i++){
            if(allOrders.get(i).getOrderId() == orderId){
                order = allOrders.get(i);
                break;
            }
        }
        if(order != null){
            new OrderDetailsDialog(parentFrame, order).setVisible(true);
        }
    }

    class NewOrderDialog extends JDialog implements ActionListener {

        private JSpinner spinQty;
        private JSpinner spinTable;
        private JTable tableItems;
        private double currentTotal;
        private JScrollPane scrollPane;
        private JComboBox comboMenu;
        private DefaultTableModel modelItems;
        private ArrayList<MenuItem> availableMenu;
        private ArrayList<OrderItem> selectedItems;
        private JButton btnAdd, btnPlace, btnCancel;
        private DefaultTableCellRenderer centerColumn;
        private JLabel lblTable, lblItem, lblTotal, lblQuantity;

        public NewOrderDialog(JFrame parent){
            super(parent, "New Order", true);
            setLayout(null);
            setSize(600, 500);
            setLocationRelativeTo(parent);
            getContentPane().setBackground(Color.WHITE);

            selectedItems = new ArrayList<OrderItem>();
            availableMenu = orderService.getAvailableMenuItems();
            currentTotal = 0.0;

            lblTable = new JLabel("Table:");
            lblTable.setBounds(20, 20, 80, 25);
            add(lblTable);

            spinTable = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
            spinTable.setBounds(100, 20, 80, 25);
            add(spinTable);

            lblItem = new JLabel("Item:");
            lblItem.setBounds(20, 55, 80, 25);
            add(lblItem);

            String[] names = new String[availableMenu.size()];
            for(int i = 0; i < availableMenu.size(); i++){
                MenuItem menu = availableMenu.get(i);
                names[i] = menu.getMenuItemName() + " - " + String.format("%.2f", menu.getMenuItemPrice());
            }
            comboMenu = new JComboBox(names);
            comboMenu.setBounds(100, 55, 250, 25);
            add(comboMenu);

            lblQuantity = new JLabel("Qty:");
            lblQuantity.setBounds(360, 55, 50, 25);
            add(lblQuantity);

            spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
            spinQty.setBounds(410, 55, 60, 25);
            add(spinQty);

            btnAdd = new JButton("Add");
            btnAdd.setBounds(480, 55, 80, 25);
            btnAdd.addActionListener(this);
            add(btnAdd);

            modelItems = new DefaultTableModel(new String[]{"Item", "Qty", "Price", "Subtotal"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c){
                    return false;
                }
            };

            tableItems = new JTable(modelItems);
            tableItems.setRowHeight(25);
            tableItems.setFillsViewportHeight(true);
            tableItems.getTableHeader().setReorderingAllowed(false);
            tableItems.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
            tableItems.getTableHeader().setPreferredSize(new Dimension(tableItems.getPreferredSize().width, 28));

            centerColumn = new DefaultTableCellRenderer();
            centerColumn.setHorizontalAlignment(SwingConstants.CENTER);
            for(int i = 0; i < tableItems.getColumnCount(); i++){
                tableItems.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
            }

            scrollPane = new JScrollPane(tableItems);
            scrollPane.setBounds(20, 95, 540, 250);
            add(scrollPane);

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
                int menuIndex = comboMenu.getSelectedIndex();
                if(menuIndex < 0){
                    return;
                }
                int quantity = (int) spinQty.getValue();
                MenuItem chosen = availableMenu.get(menuIndex);

                boolean found = false;
                for(int i = 0; i < selectedItems.size(); i++){
                    OrderItem orderItem = selectedItems.get(i);
                    if(orderItem.getLinkedMenuItem().getMenuItemId() == chosen.getMenuItemId()){
                        orderItem.setOrderItemQuantity(orderItem.getOrderItemQuantity() + quantity);
                        found = true;
                        break;
                    }
                }
                if(!found){
                    selectedItems.add(new OrderItem("OI-" + (selectedItems.size() + 1), chosen, quantity));
                }
                refreshItems();
            }else if(e.getSource() == btnPlace){
                String table = String.valueOf(spinTable.getValue());
                if(selectedItems.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Add at least one item.");
                    return;
                }
                try{
                    orderService.createOrder(table, selectedItems);
                    refreshOrders();
                    dispose();
                    JOptionPane.showMessageDialog(parentFrame, "Order placed.");
                }catch(ValidationException ex){
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }catch(user.InsufficientInventoryException ex){
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Insufficient Inventory", JOptionPane.ERROR_MESSAGE);
                }
            }else if(e.getSource() == btnCancel){
                dispose();
            }
        }

        private void refreshItems(){
            modelItems.setRowCount(0);
            currentTotal = 0.0;
            for(int i = 0; i < selectedItems.size(); i++){
                OrderItem oi = selectedItems.get(i);
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

        private JLabel lblInfo;
        private JButton btnClose;
        private JTable detailsTable;
        private JScrollPane scrollPane;
        private DefaultTableModel detailsModel;
        private DefaultTableCellRenderer centerCollumn;

        public OrderDetailsDialog(JFrame parent, Order order){
            super(parent, "Order Details", true);
            setLayout(null);
            setSize(400, 400);
            setLocationRelativeTo(parent);
            getContentPane().setBackground(Color.WHITE);

            lblInfo = new JLabel("Order #" + order.getOrderId() + " Table " + order.getOrderTableNumber());
            lblInfo.setBounds(20, 20, 350, 25);
            add(lblInfo);

            detailsModel = new DefaultTableModel(new String[]{"Qty", "Item", "Price", "Subtotal"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c){
                    return false;
                }
            };

            detailsTable = new JTable(detailsModel);
            detailsTable.setRowHeight(28);
            detailsTable.setFillsViewportHeight(true);
            detailsTable.getTableHeader().setReorderingAllowed(false);
            detailsTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
            detailsTable.getTableHeader().setPreferredSize(new Dimension(detailsTable.getPreferredSize().width, 28));

            centerCollumn = new DefaultTableCellRenderer();
            centerCollumn.setHorizontalAlignment(SwingConstants.CENTER);
            for(int i = 0; i < detailsTable.getColumnCount(); i++){
                detailsTable.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
            }

            scrollPane = new JScrollPane(detailsTable);
            scrollPane.setBounds(20, 55, 350, 220);
            add(scrollPane);

            if(order.getOrderItems() != null){
                for(int i = 0; i < order.getOrderItems().size(); i++){
                    OrderItem orderItem = order.getOrderItems().get(i);
                    double price = 0;
                    String itemName = "-";
                    if(orderItem.getLinkedMenuItem() != null){
                        price = orderItem.getLinkedMenuItem().getMenuItemPrice();
                        itemName = orderItem.getLinkedMenuItem().getMenuItemName();
                    }
                    detailsModel.addRow(new Object[]{
                        orderItem.getOrderItemQuantity(),
                        itemName,
                        String.format("%.2f", price),
                        String.format("%.2f", price * orderItem.getOrderItemQuantity())
                    });
                }
            }

            btnClose = new JButton("Close");
            btnClose.setBounds(280, 300, 90, 30);
            btnClose.setBackground(Color.RED);
            btnClose.setForeground(Color.WHITE);
            btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
}
