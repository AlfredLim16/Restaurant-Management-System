package manager;

import cashier.MenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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
        buildUI();
    }

    private void buildUI(){
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

        modelMenuItem = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Price", "Available", "Ingredients"}, 0){
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
        IngredientDialog dialog = new IngredientDialog(parentFrame, allInventory, existing);
        dialog.setVisible(true);
        return dialog.getIngredientList();
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


    class IngredientDialog extends JDialog implements ActionListener {

        private ArrayList<InventoryItem> allInventory;
        private ArrayList<MenuItemIngredient> ingredientList;
        private ArrayList<MenuItemIngredient> originalList;

        private JLabel lblPick, lblQty, lblList;
        private JComboBox comboInv;
        private JSpinner spinQty;
        private JButton btnAddIng, btnRemoveIng, btnOk, btnCancel;
        private JTable ingredientTable;
        private JScrollPane scroll;
        private DefaultTableModel ingredientModel;
        private DefaultTableCellRenderer centerColumn;
        private JPanel panel;

        public IngredientDialog(JFrame parent, ArrayList<InventoryItem> allInventory, ArrayList<MenuItemIngredient> existing){
            super(parent, "Set Ingredients", true);
            this.allInventory = allInventory;
            this.originalList = existing;
            this.ingredientList = new ArrayList<MenuItemIngredient>();

            setLayout(null);
            setSize(540, 420);
            setLocationRelativeTo(parent);
            getContentPane().setBackground(Color.WHITE);

            ingredientModel = new DefaultTableModel(new String[]{"Inventory Item", "Qty Required"}, 0){
                @Override
                public boolean isCellEditable(int r, int c){
                    return false;
                }
            };

            if(existing != null){
                for(int i = 0; i < existing.size(); i++){
                    MenuItemIngredient ing = existing.get(i);
                    ingredientList.add(ing);
                    ingredientModel.addRow(new Object[]{ing.getInventoryItemName(), ing.getQuantityRequired()});
                }
            }

            String[] invNames = new String[allInventory.size()];
            for(int i = 0; i < allInventory.size(); i++){
                invNames[i] = allInventory.get(i).getInventoryItemName();
            }

            lblPick = new JLabel("Inventory Item:");
            lblPick.setBounds(10, 10, 120, 25);
            add(lblPick);

            comboInv = new JComboBox(invNames);
            comboInv.setBounds(130, 10, 200, 25);
            add(comboInv);

            lblQty = new JLabel("Qty:");
            lblQty.setBounds(340, 10, 40, 25);
            add(lblQty);

            spinQty = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 9999.0, 0.1));
            spinQty.setBounds(380, 10, 80, 25);
            add(spinQty);

            btnAddIng = new JButton("Add Ingredient");
            btnAddIng.setBounds(10, 45, 140, 28);
            btnAddIng.addActionListener(this);
            add(btnAddIng);

            btnRemoveIng = new JButton("Remove Selected");
            btnRemoveIng.setBounds(160, 45, 150, 28);
            btnRemoveIng.addActionListener(this);
            add(btnRemoveIng);

            lblList = new JLabel("Ingredients:");
            lblList.setBounds(10, 85, 120, 20);
            add(lblList);

            ingredientTable = new JTable(ingredientModel);
            ingredientTable.setRowHeight(25);
            ingredientTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 13));

            centerColumn = new DefaultTableCellRenderer();
            centerColumn.setHorizontalAlignment(SwingConstants.CENTER);
            for(int i = 0; i < ingredientTable.getColumnCount(); i++){
                ingredientTable.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
            }

            scroll = new JScrollPane(ingredientTable);
            scroll.setBounds(10, 108, 500, 200);
            add(scroll);

            btnOk = new JButton("OK");
            btnOk.setBounds(310, 320, 90, 28);
            btnOk.addActionListener(this);
            add(btnOk);

            btnCancel = new JButton("Cancel");
            btnCancel.setBounds(410, 320, 90, 28);
            btnCancel.addActionListener(this);
            add(btnCancel);
        }

        public ArrayList<MenuItemIngredient> getIngredientList(){
            return ingredientList;
        }

        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == btnAddIng){
                int idx = comboInv.getSelectedIndex();
                if(idx < 0){
                    return;
                }
                InventoryItem chosen = allInventory.get(idx);
                double qty = (double) spinQty.getValue();

                for(int i = 0; i < ingredientList.size(); i++){
                    if(ingredientList.get(i).getInventoryItemId() == chosen.getInventoryItemId()){
                        JOptionPane.showMessageDialog(this, "Already added.");
                        return;
                    }
                }

                MenuItemIngredient ing = new MenuItemIngredient();
                ing.setInventoryItemId(chosen.getInventoryItemId());
                ing.setInventoryItemName(chosen.getInventoryItemName());
                ing.setQuantityRequired(qty);
                ingredientList.add(ing);
                ingredientModel.addRow(new Object[]{chosen.getInventoryItemName(), qty});

            }else if(e.getSource() == btnRemoveIng){
                int row = ingredientTable.getSelectedRow();
                if(row == -1){
                    return;
                }
                ingredientList.remove(row);
                ingredientModel.removeRow(row);

            }else if(e.getSource() == btnOk){
                dispose();

            }else if(e.getSource() == btnCancel){
                ingredientList = originalList;
                dispose();
            }
        }
    }
}
