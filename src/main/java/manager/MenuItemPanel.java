package manager;

import cashier.MenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.ValidationException;

public class MenuItemPanel extends JPanel implements ActionListener {

    private static final String[] CATEGORIES = {
        "Main", "Side", "Dessert", "Beverage", "Other"
    };

    private final MenuItemService menuItemService;
    private final JFrame parentFrame;

    private JSeparator separator;
    private JLabel lblTitle, lblSubTitle;
    private JTable tableMenuItem;
    private JScrollPane scrollMenuItem;
    private DefaultTableModel modelMenuItem;
    private DefaultTableCellRenderer centerColumn;
    private JButton btnAdd, btnUpdate, btnToggle, btnDelete, btnViewIngredients;

    public MenuItemPanel(MenuItemService menuItemService, JFrame parentFrame){
        this.menuItemService = menuItemService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        MenuItem();
    }

    private void MenuItem(){
        lblTitle = new JLabel("Menu Items");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage menu items available to customers");
        lblSubTitle.setBounds(20, 35, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 70, 1260, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(20, 80, 80, 30);
        btnAdd.addActionListener(this);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(110, 80, 90, 30);
        btnUpdate.addActionListener(this);
        add(btnUpdate);

        btnToggle = new JButton("Toggle Available");
        btnToggle.setBounds(210, 80, 140, 30);
        btnToggle.addActionListener(this);
        add(btnToggle);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(360, 80, 80, 30);
        btnDelete.addActionListener(this);
        add(btnDelete);

        btnViewIngredients = new JButton("View Ingredients");
        btnViewIngredients.setBounds(450, 80, 140, 30);
        btnViewIngredients.addActionListener(this);
        add(btnViewIngredients);

        modelMenuItem = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Available", "Ingredients"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };

        tableMenuItem = new JTable(modelMenuItem);
        tableMenuItem.setRowHeight(28);
        tableMenuItem.setFillsViewportHeight(true);
        tableMenuItem.getTableHeader().setReorderingAllowed(false);
        tableMenuItem.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableMenuItem.getTableHeader().setPreferredSize(new Dimension(tableMenuItem.getPreferredSize().width, 28));

        centerColumn = new DefaultTableCellRenderer();
        centerColumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tableMenuItem.getColumnCount(); i++){
            tableMenuItem.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
        }

        scrollMenuItem = new JScrollPane(tableMenuItem);
        scrollMenuItem.setBounds(20, 120, 1260, 450);
        add(scrollMenuItem);

        refreshMenuItems();
    }

    public void refreshMenuItems(){
        modelMenuItem.setRowCount(0);
        ArrayList<MenuItem> items = menuItemService.getAllMenuItems();
        for(int i = 0; i < items.size(); i++){
            MenuItem item = items.get(i);
            ArrayList<MenuItemIngredient> ingredients = menuItemService.getIngredients(item.getMenuItemId());
            modelMenuItem.addRow(new Object[]{
                item.getMenuItemId(),
                item.getMenuItemName(),
                item.getMenuItemCategory(),
                String.format("%.2f", item.getMenuItemPrice()),
                item.isAvailable() ? "Yes" : "No",
                ingredients.size() + " ingredient(s)"
            });
        }
    }

    private ArrayList<MenuItemIngredient> openIngredientDialog(ArrayList<MenuItemIngredient> existing){
        ArrayList<InventoryItem> allInventory = menuItemService.getAllInventoryItems();
        if(allInventory.isEmpty()){
            JOptionPane.showMessageDialog(parentFrame, "No inventory items found. Add inventory first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return existing;
        }
        Ingredient ingredient = new Ingredient(parentFrame, allInventory, existing);
        ingredient.setVisible(true);
        return ingredient.getIngredientList();
    }

    private void addMenuItem(){
        JTextField txtName = new JTextField();
        JComboBox comboCategory = new JComboBox(CATEGORIES);
        JTextField txtPrice = new JTextField();
        JCheckBox chkAvailable = new JCheckBox("Available", true);

        Object[] fields = {
            "Name:", txtName,
            "Category:", comboCategory,
            "Price:", txtPrice,
            "", chkAvailable
        };

        int result = JOptionPane.showConfirmDialog(parentFrame, fields, "Add Menu Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result != JOptionPane.OK_OPTION){
            return;
        }

        String name = txtName.getText().trim();
        if(name.isEmpty()){
            JOptionPane.showMessageDialog(parentFrame, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price;
        try{
            price = Double.parseDouble(txtPrice.getText().trim());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid price.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String category = (String) comboCategory.getSelectedItem();
        ArrayList<MenuItemIngredient> ingredients = openIngredientDialog(new ArrayList<MenuItemIngredient>());

        try{
            menuItemService.addMenuItem(name, price, category, chkAvailable.isSelected(), ingredients);
            refreshMenuItems();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMenuItem(){
        int row = tableMenuItem.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an item.");
            return;
        }
        int id = (int) modelMenuItem.getValueAt(row, 0);
        String currentName = (String) modelMenuItem.getValueAt(row, 1);
        String currentCategory = (String) modelMenuItem.getValueAt(row, 2);
        String currentPrice = (String) modelMenuItem.getValueAt(row, 3);
        boolean currentAvailable = "Yes".equals(modelMenuItem.getValueAt(row, 4));

        JTextField txtName = new JTextField(currentName);
        JComboBox comboCategory = new JComboBox(CATEGORIES);
        comboCategory.setSelectedItem(currentCategory);
        JTextField txtPrice = new JTextField(currentPrice);
        JCheckBox chkAvailable = new JCheckBox("Available", currentAvailable);

        Object[] fields = {
            "Name:", txtName,
            "Category:", comboCategory,
            "Price:", txtPrice,
            "", chkAvailable
        };

        int result = JOptionPane.showConfirmDialog(parentFrame, fields, "Update Menu Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result != JOptionPane.OK_OPTION){
            return;
        }

        String name = txtName.getText().trim();
        if(name.isEmpty()){
            JOptionPane.showMessageDialog(parentFrame, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price;
        try{
            price = Double.parseDouble(txtPrice.getText().trim());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid price.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String category = (String) comboCategory.getSelectedItem();
        ArrayList<MenuItemIngredient> existingIngredients = menuItemService.getIngredients(id);
        ArrayList<MenuItemIngredient> ingredients = openIngredientDialog(existingIngredients);

        try{
            menuItemService.updateMenuItem(id, name, price, category, chkAvailable.isSelected(), ingredients);
            refreshMenuItems();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleAvailability(){
        int row = tableMenuItem.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an item.");
            return;
        }
        int id = (int) modelMenuItem.getValueAt(row, 0);
        try{
            menuItemService.toggleAvailability(id);
            refreshMenuItems();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMenuItem(){
        int row = tableMenuItem.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an item.");
            return;
        }
        int id = (int) modelMenuItem.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Delete this menu item?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        try{
            menuItemService.deleteMenuItem(id);
            refreshMenuItems();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewIngredients(){
        int row = tableMenuItem.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select an item.");
            return;
        }
        int id = (int) modelMenuItem.getValueAt(row, 0);
        String name = (String) modelMenuItem.getValueAt(row, 1);
        ArrayList<MenuItemIngredient> ingredients = menuItemService.getIngredients(id);

        if(ingredients.isEmpty()){
            JOptionPane.showMessageDialog(parentFrame, name + " has no linked ingredients.");
            return;
        }

        String text = "";
        for(int i = 0; i < ingredients.size(); i++){
            MenuItemIngredient ing = ingredients.get(i);
            text = text + "- " + ing.getInventoryItemName() + " x" + ing.getQuantityRequired() + "\n";
        }
        JOptionPane.showMessageDialog(parentFrame, text, name + " Ingredients", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnAdd){
            addMenuItem();
        }else if(e.getSource() == btnUpdate){
            updateMenuItem();
        }else if(e.getSource() == btnToggle){
            toggleAvailability();
        }else if(e.getSource() == btnDelete){
            deleteMenuItem();
        }else if(e.getSource() == btnViewIngredients){
            viewIngredients();
        }
    }
}
