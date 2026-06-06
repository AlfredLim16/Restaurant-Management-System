package manager;

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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.ValidationException;

public class FoodWastePanel extends JPanel implements ActionListener {

    private static final String[] WASTE_ITEMS = {
        "Chicken", "Nuggets", "Fish Fillet", "McCafe", "CokeFloat", "Sundae", "McFlurry"
    };

    private static final String[] REASONS = {
        "Overcooked", "Expired", "Customer Return", "Spoiled", "Over-prepared"
    };

    private static final String[] CATEGORIES = {
        "Meat", "Snack", "Beverage", "Dessert", "Other"
    };

    private int[] wasteCounts;
    private JTable tableWaste;
    private JSeparator separator;
    private JScrollPane scrollWaste;
    private JLabel[] lblWasteCounts;
    private final JFrame parentFrame;
    private DefaultTableModel modelWaste;
    private JButton[] btnWasteAdd, btnWasteMinus;
    private DefaultTableCellRenderer centerColumn;
    private final FoodWasteService foodWasteService;
    private JLabel lblWasteTotal, lblWasteMost, lblWasteDaily;
    private JButton btnWasteSave, btnWasteReset, btnWasteDelete;
    private JLabel lblTitle, lblSubTitle, lblReason, lblCategory;
    private JComboBox<String> comboWasteReason, comboWasteCategory;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FoodWastePanel(FoodWasteService foodWasteService, JFrame parentFrame){
        this.foodWasteService = foodWasteService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        FoodWaste();
    }

    private void FoodWaste(){
        lblTitle = new JLabel("Food Waste");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Monitor and reduce food waste");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 70, 1100, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        wasteCounts = new int[WASTE_ITEMS.length];
        lblWasteCounts = new JLabel[WASTE_ITEMS.length];
        btnWasteAdd = new JButton[WASTE_ITEMS.length];
        btnWasteMinus = new JButton[WASTE_ITEMS.length];

        int y = 120;
        for(int i = 0; i < WASTE_ITEMS.length; i++){
            JLabel lblItem = new JLabel(WASTE_ITEMS[i]);
            lblItem.setBounds(20, y, 100, 25);
            add(lblItem);

            btnWasteAdd[i] = new JButton("+");
            btnWasteAdd[i].setBounds(130, y, 45, 25);
            btnWasteAdd[i].setActionCommand("waste_add:" + i);
            btnWasteAdd[i].addActionListener(this);
            add(btnWasteAdd[i]);

            lblWasteCounts[i] = new JLabel("0", SwingConstants.CENTER);
            lblWasteCounts[i].setBounds(180, y, 40, 25);
            add(lblWasteCounts[i]);

            btnWasteMinus[i] = new JButton("-");
            btnWasteMinus[i].setBounds(225, y, 45, 25);
            btnWasteMinus[i].setActionCommand("waste_minus:" + i);
            btnWasteMinus[i].addActionListener(this);
            add(btnWasteMinus[i]);

            y += 40;
        }

        lblWasteTotal = new JLabel("Total Waste: 0");
        lblWasteTotal.setBounds(20, y + 10, 150, 25);
        add(lblWasteTotal);

        lblWasteMost = new JLabel("Most Wasted: -");
        lblWasteMost.setBounds(320, 80, 250, 25);
        add(lblWasteMost);

        lblWasteDaily = new JLabel("Daily Cost: 0.00");
        lblWasteDaily.setBounds(580, 80, 200, 25);
        add(lblWasteDaily);

        modelWaste = new DefaultTableModel(new String[]{"ID", "Item", "Qty", "Reason", "Category", "Cost", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c){
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
        for(int i = 0; i < tableWaste.getColumnCount(); i++){
            tableWaste.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
        }

        scrollWaste = new JScrollPane(tableWaste);
        scrollWaste.setBounds(320, 120, 800, 300);
        add(scrollWaste);

        lblReason = new JLabel("Reason:");
        lblReason.setBounds(320, 450, 60, 25);
        add(lblReason);

        comboWasteReason = new JComboBox<>(REASONS);
        comboWasteReason.setBounds(380, 450, 150, 25);
        add(comboWasteReason);

        lblCategory = new JLabel("Category:");
        lblCategory.setBounds(540, 450, 70, 25);
        add(lblCategory);

        comboWasteCategory = new JComboBox<>(CATEGORIES);
        comboWasteCategory.setBounds(610, 450, 150, 25);
        add(comboWasteCategory);

        btnWasteSave = new JButton("Save");
        btnWasteSave.setBounds(320, 500, 80, 30);
        btnWasteSave.addActionListener(this);
        add(btnWasteSave);

        btnWasteReset = new JButton("Reset");
        btnWasteReset.setBounds(410, 500, 80, 30);
        btnWasteReset.addActionListener(this);
        add(btnWasteReset);

        btnWasteDelete = new JButton("Delete");
        btnWasteDelete.setBounds(500, 500, 80, 30);
        btnWasteDelete.addActionListener(this);
        add(btnWasteDelete);

        refreshFoodWaste();
    }

    public void refreshFoodWaste(){
        modelWaste.setRowCount(0);
        ArrayList<FoodWaste> records = foodWasteService.getAllWasteRecords();
        for(FoodWaste foodwaste : records){
            modelWaste.addRow(new Object[]{
                foodwaste.getFoodWasteId(),
                foodwaste.getFoodWasteItemName(),
                foodwaste.getFoodWasteQuantity(),
                foodwaste.getFoodWasteReason(),
                foodwaste.getFoodWasteCategory(),
                foodwaste.getFoodWasteEstimatedCost(),
                foodwaste.getFoodWasteRecordedDate().toLocalDate().format(DATE_FMT)
            });
        }

        double daily = foodWasteService.calculateDailyWasteCost();
        lblWasteDaily.setText("Daily Cost: " + String.format("%.2f", daily));

        HashMap<String, Double> qtyMap = foodWasteService.getQuantityByItem();
        String most = "-";
        double max = 0;
        for(String key : qtyMap.keySet()){
            double value = qtyMap.get(key);
            if(value > max){
                max = value;
                most = key;
            }
        }
        lblWasteMost.setText("Most Wasted: " + most);
    }

    private void updateWasteTotal(){
        int total = 0;
        for(int counts : wasteCounts){
            total += counts;
        }
        lblWasteTotal.setText("Total Waste: " + total);
    }

    private void saveWaste(){
        String reason = (String) comboWasteReason.getSelectedItem();
        String category = (String) comboWasteCategory.getSelectedItem();
        String costStr = JOptionPane.showInputDialog(parentFrame, "Cost per unit:");
        if(costStr == null){
            return;
        }

        double costPerUnit;
        try{
            costPerUnit = Double.parseDouble(costStr);
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(parentFrame, "Invalid cost.");
            return;
        }

        boolean saved = false;
        for(int items = 0; items < WASTE_ITEMS.length; items++){
            if(wasteCounts[items] <= 0){
                continue;
            }
            try{
                double totalCost = wasteCounts[items] * costPerUnit;
                foodWasteService.recordWaste(WASTE_ITEMS[items], wasteCounts[items], "units", reason, totalCost, "staff", category);
                saved = true;
            }catch(ValidationException ex){
                JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if(!saved){
            JOptionPane.showMessageDialog(parentFrame, "No quantities entered.");
            return;
        }

        refreshFoodWaste();
        resetWaste();
        JOptionPane.showMessageDialog(parentFrame, "Saved.");
    }

    private void resetWaste(){
        for(int items = 0; items < WASTE_ITEMS.length; items++){
            wasteCounts[items] = 0;
            lblWasteCounts[items].setText("0");
        }
        updateWasteTotal();
    }

    private void deleteWaste(){
        int row = tableWaste.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(parentFrame, "Select a row.");
            return;
        }
        int id = (int) modelWaste.getValueAt(row, 0);
        try{
            foodWasteService.deleteWaste(id);
            refreshFoodWaste();
        }catch(ValidationException ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnWasteSave){
            saveWaste();
        }else if(e.getSource() == btnWasteReset){
            resetWaste();
        }else if(e.getSource() == btnWasteDelete){
            deleteWaste();
        }else if(e.getActionCommand() != null && e.getActionCommand().startsWith("waste_add:")){
            int index = Integer.parseInt(e.getActionCommand().substring(10));
            wasteCounts[index]++;
            lblWasteCounts[index].setText(String.valueOf(wasteCounts[index]));
            updateWasteTotal();
        }else if(e.getActionCommand() != null && e.getActionCommand().startsWith("waste_minus:")){
            int idx = Integer.parseInt(e.getActionCommand().substring(12));
            if(wasteCounts[idx] > 0){
                wasteCounts[idx]--;
                lblWasteCounts[idx].setText(String.valueOf(wasteCounts[idx]));
                updateWasteTotal();
            }
        }
    }

}
