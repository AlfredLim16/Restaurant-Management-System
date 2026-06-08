package manager;

import cashier.MenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

public class FoodWastePanel extends JPanel implements ActionListener {

    private static final String[] REASONS = {
        "Overcooked", "Expired", "Customer Return", "Spoiled", "Over-prepared"
    };

    private final FoodWasteService foodWasteService;
    private final MenuItemService menuItemService;
    private final JFrame parentFrame;

    private JTable tableWaste;
    private JScrollPane scrollWaste;
    private JLabel lblTitle, lblSubTitle;
    private JLabel lblWasteMost, lblWasteDaily;
    private DefaultTableModel modelWaste;
    private DefaultTableCellRenderer centerColumn;
    private JButton btnWasteAdd, btnWasteDelete;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FoodWastePanel(FoodWasteService foodWasteService, MenuItemService menuItemService, JFrame parentFrame) {
        this.foodWasteService = foodWasteService;
        this.menuItemService = menuItemService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        FoodWaste();
    }

    private void FoodWaste() {
        lblTitle = new JLabel("Food Waste");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Monitor and reduce food waste");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 1260, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        btnWasteAdd = new JButton("Add");
        btnWasteAdd.setBounds(20, 80, 80, 30);
        btnWasteAdd.addActionListener(this);
        add(btnWasteAdd);

        btnWasteDelete = new JButton("Delete");
        btnWasteDelete.setBounds(110, 80, 80, 30);
        btnWasteDelete.addActionListener(this);
        add(btnWasteDelete);

        lblWasteMost = new JLabel("Most Wasted: -");
        lblWasteMost.setBounds(220, 85, 250, 20);
        add(lblWasteMost);

        lblWasteDaily = new JLabel("Daily Cost: 0.00");
        lblWasteDaily.setBounds(480, 85, 200, 20);
        add(lblWasteDaily);

        modelWaste = new DefaultTableModel(new String[]{"ID", "Item", "Qty", "Unit", "Reason", "Category", "Cost", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableWaste = new JTable(modelWaste);
        tableWaste.setRowHeight(28);
        tableWaste.setFillsViewportHeight(true);
        tableWaste.getTableHeader().setReorderingAllowed(false);
        tableWaste.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableWaste.getTableHeader().setPreferredSize(new Dimension(tableWaste.getPreferredSize().width, 28));

        centerColumn = new DefaultTableCellRenderer();
        centerColumn.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tableWaste.getColumnCount(); i++) {
            tableWaste.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
        }

        scrollWaste = new JScrollPane(tableWaste);
        scrollWaste.setBounds(20, 120, 1260, 450);
        add(scrollWaste);

        refreshFoodWaste();
    }

    public void refreshFoodWaste() {
        modelWaste.setRowCount(0);
        ArrayList<FoodWaste> records = foodWasteService.getAllWasteRecords();
        for (FoodWaste foodwaste : records) {
            modelWaste.addRow(new Object[]{
                foodwaste.getFoodWasteId(),
                foodwaste.getFoodWasteItemName(),
                foodwaste.getFoodWasteQuantity(),
                foodwaste.getFoodWasteUnit(),
                foodwaste.getFoodWasteReason(),
                foodwaste.getFoodWasteCategory(),
                String.format("%.2f", foodwaste.getFoodWasteEstimatedCost()),
                foodwaste.getFoodWasteRecordedDate().toLocalDate().format(DATE_FMT)
            });
        }

        double daily = foodWasteService.calculateDailyWasteCost();
        lblWasteDaily.setText("Daily Cost: " + String.format("%.2f", daily));

        HashMap<String, Double> qtyMap = foodWasteService.getQuantityByItem();
        String most = "-";
        double max = 0;
        for (String key : qtyMap.keySet()) {
            double value = qtyMap.get(key);
            if (value > max) {
                max = value;
                most = key;
            }
        }
        lblWasteMost.setText("Most Wasted: " + most);
    }

    private void addWaste() {
        ArrayList<MenuItem> menuItems = menuItemService.getAllMenuItems();
        if (menuItems.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No menu items found. Add menu items first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] itemNames = new String[menuItems.size()];
        for (int i = 0; i < menuItems.size(); i++) {
            itemNames[i] = menuItems.get(i).getMenuItemName();
        }

        JComboBox<String> comboItem = new JComboBox<>(itemNames);
        JTextField txtQty = new JTextField();
        JTextField txtCost = new JTextField();
        JComboBox<String> comboReason = new JComboBox<>(REASONS);

        Object[] fields = {
            "Item:", comboItem,
            "Quantity:", txtQty,
            "Cost:", txtCost,
            "Reason:", comboReason
        };

        int result = JOptionPane.showConfirmDialog(parentFrame, fields, "Add Food Waste", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String itemName = (String) comboItem.getSelectedItem();
        String reason = (String) comboReason.getSelectedItem();

        int selectedIndex = comboItem.getSelectedIndex();
        String category = menuItems.get(selectedIndex).getMenuItemCategory();

        try {
            double qty = Double.parseDouble(txtQty.getText().trim());
            double cost = Double.parseDouble(txtCost.getText().trim());
            foodWasteService.recordWaste(itemName, qty, "units", reason, cost, "staff", category);
            refreshFoodWaste();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parentFrame, "Invalid number value.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteWaste() {
        int row = tableWaste.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Select a row.");
            return;
        }
        int id = (int) modelWaste.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            foodWasteService.deleteWaste(id);
            refreshFoodWaste();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnWasteAdd) {
            addWaste();
        } else if (e.getSource() == btnWasteDelete) {
            deleteWaste();
        }
    }
}
