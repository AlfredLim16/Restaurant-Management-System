package manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.ValidationException;

public class InventoryPanel extends JPanel implements ActionListener {

    private static final String[] CATEGORIES = {
        "Meat", "Seafood", "Vegetable", "Fruit", "Dairy", "Grain", "Spice", "Beverage", "Condiment", "Other"
    };

    private static final String[] UNITS = {
        "kg", "g", "litter", "pcs", "pack"
    };

    private final InventoryService inventoryService;
    private final JFrame parentFrame;

    private JTable tableInventory;
    private JScrollPane scrollInventory;
    private JLabel lblTitle, lblSubTitle;
    private DefaultTableModel modelInventory;
    private DefaultTableCellRenderer centerColumn;
    private JButton btnInvAdd, btnInvUpdate, btnInvRestock, btnInvDelete;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public InventoryPanel(InventoryService inventoryService, JFrame parentFrame){
        this.inventoryService = inventoryService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        Inventory();
    }

    private void Inventory(){
        lblTitle = new JLabel("Inventory");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage stock levels, reorders, and suppliers");
        lblSubTitle.setBounds(20, 35, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 1260, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        btnInvAdd = new JButton("Add");
        btnInvAdd.setBounds(20, 80, 80, 30);
        btnInvAdd.addActionListener(this);
        add(btnInvAdd);

        btnInvUpdate = new JButton("Update Quantity");
        btnInvUpdate.setBounds(110, 80, 130, 30);
        btnInvUpdate.addActionListener(this);
        add(btnInvUpdate);

        btnInvRestock = new JButton("Restock");
        btnInvRestock.setBounds(250, 80, 90, 30);
        btnInvRestock.addActionListener(this);
        add(btnInvRestock);

        btnInvDelete = new JButton("Delete");
        btnInvDelete.setBounds(350, 80, 90, 30);
        btnInvDelete.addActionListener(this);
        add(btnInvDelete);

        modelInventory = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Qty", "Unit", "Cost", "Reorder", "Expiry", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };

        tableInventory = new JTable(modelInventory);
        tableInventory.setRowHeight(28);
        tableInventory.setFillsViewportHeight(true);
        tableInventory.getTableHeader().setReorderingAllowed(false);
        tableInventory.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableInventory.getTableHeader().setPreferredSize(new Dimension(tableInventory.getPreferredSize().width, 28));

        centerColumn = new DefaultTableCellRenderer();
        centerColumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tableInventory.getColumnCount(); i++){
            tableInventory.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
        }

        scrollInventory = new JScrollPane(tableInventory);
        scrollInventory.setBounds(20, 120, 1260, 450);
        add(scrollInventory);

        refreshInventory();
    }

    public void refreshInventory(){
        modelInventory.setRowCount(0);
        ArrayList<InventoryItem> items = inventoryService.getAllInventoryItems();
        for(InventoryItem item : items){
            boolean low = inventoryService.isLowStock(item.getInventoryItemId());
            boolean exp = inventoryService.isExpiringSoon(item.getInventoryItemId(), 7);
            String status;
            if(low && exp){
                status = "Low & Expiring";
            }else if(low){
                status = "Low Stock";
            }else if(exp){
                status = "Expiring Soon";
            }else{
                status = "Good";
            }

            modelInventory.addRow(new Object[]{
                item.getInventoryItemId(),
                item.getInventoryItemName(),
                item.getInventoryCategory(),
                item.getInventoryQuantity(),
                item.getInventoryUnit(),
                String.format("%.2f", item.getInventoryCostPerUnit()),
                item.getInventoryReorderLevel(),
                item.getInventoryExpiryDate() != null ? item.getInventoryExpiryDate().format(DATE_FMT) : "N/A",
                status
            });
        }
    }

    private String pickFromCombo(String title, String[] options, String current){
        JComboBox<String> combo = new JComboBox<>(options);
        if(current != null){
            combo.setSelectedItem(current);
        }
        int result = JOptionPane.showConfirmDialog(parentFrame, combo, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result != JOptionPane.OK_OPTION){
            return null;
        }
        return (String) combo.getSelectedItem();
    }

    private void addInventory(){
        JTextField txtName = new JTextField();
        JComboBox<String> comboCategory = new JComboBox<>(CATEGORIES);
        JTextField txtQty = new JTextField();
        JComboBox<String> comboUnit = new JComboBox<>(UNITS);
        JTextField txtCost = new JTextField();
        JTextField txtReorder = new JTextField();
        JTextField txtSupplier = new JTextField();
        JTextField txtExpiry = new JTextField();

        Object[] fields = {
            "Name:", txtName,
            "Category:", comboCategory,
            "Quantity:", txtQty,
            "Unit:", comboUnit,
            "Cost per unit:", txtCost,
            "Reorder level:", txtReorder,
            "Supplier:", txtSupplier,
            "Expiry (yyyy-MM-dd, blank = none):", txtExpiry
        };

        int result = JOptionPane.showConfirmDialog(parentFrame, fields, "Add Inventory Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result != JOptionPane.OK_OPTION){
            return;
        }

        String name = txtName.getText().trim();
        if(name.isEmpty()){
            JOptionPane.showMessageDialog(parentFrame, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String category = (String) comboCategory.getSelectedItem();
        String unit = (String) comboUnit.getSelectedItem();
        String expiry = txtExpiry.getText().trim();

        try{
            int qty = Integer.parseInt(txtQty.getText().trim());
            double cost = Double.parseDouble(txtCost.getText().trim());
            int reorder = Integer.parseInt(txtReorder.getText().trim());
            String supplier = txtSupplier.getText().trim();
            LocalDate expDate = null;
            if(!expiry.isEmpty()){
                expDate = LocalDate.parse(expiry, DATE_FMT);
            }
            inventoryService.addInventoryItem(name, category, qty, unit, cost, reorder, supplier, expDate);
            refreshInventory();
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid number value.", "Error", JOptionPane.ERROR_MESSAGE);
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInventoryQty(){
        int row = tableInventory.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an item.");
            return;
        }
        int id = (int) modelInventory.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(parentFrame, "New quantity:");
        if(input == null){
            return;
        }
        try{
            inventoryService.updateQuantity(id, Integer.parseInt(input.trim()));
            refreshInventory();
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restockInventory(){
        int row = tableInventory.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an item.");
            return;
        }
        int id = (int) modelInventory.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(parentFrame, "Quantity to add:");
        if(input == null){
            return;
        }
        try{
            inventoryService.restockInventory(id, Integer.parseInt(input.trim()));
            refreshInventory();
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInventory(){
        int row = tableInventory.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an item.");
            return;
        }
        int id = (int) modelInventory.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Delete this item?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        try{
            inventoryService.deleteInventoryItem(id);
            refreshInventory();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnInvAdd){
            addInventory();
        }else if(e.getSource() == btnInvUpdate){
            updateInventoryQty();
        }else if(e.getSource() == btnInvRestock){
            restockInventory();
        }else if(e.getSource() == btnInvDelete){
            deleteInventory();
        }
    }
}
