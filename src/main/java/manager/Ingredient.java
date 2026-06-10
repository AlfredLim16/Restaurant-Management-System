package manager;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Ingredient extends JDialog implements ActionListener {

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

    public Ingredient(JFrame parent, ArrayList<InventoryItem> allInventory, ArrayList<MenuItemIngredient> existing){
        super(parent, "Set Ingredients", true);
        this.allInventory = allInventory;
        this.originalList = existing;
        this.ingredientList = new ArrayList<MenuItemIngredient>();

        setLayout(null);
        setSize(540, 420);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        ingredientModel = new DefaultTableModel(new String[]{"Inventory Item", "Qty Required"}, 0) {
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
