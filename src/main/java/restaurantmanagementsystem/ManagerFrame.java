
package restaurantsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import appservice.RestaurantAppService.*;
import dataservice.RestaurantInMemory;
import dataservice.RestaurantDataService;
import dataservice.RestaurantDataService.IPermission;
import java.net.URL;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import model.RestaurantModel.*;
import model.RestaurantModel.InventoryItem;

public class ManagerFrame extends JFrame implements ActionListener {

    private OrderService orderService;
    private PaymentService paymentService;
    private InventoryService inventoryService;
    private FoodWasteService foodWasteService;
    private ReportService reportService;

    private JButton btnDashboard, btnInventory, btnFoodWaste, btnReports, btnAbout, btnLogout;
    private JPanel panelDashboard, panelInventory, panelFoodWaste, panelReports, panelAbout;

    private JLabel lblDashRevenue, lblDashActive, lblDashCompleted, lblDashLow;

    private JTable tableInventory;
    private DefaultTableModel modelInventory;
    private JScrollPane scrollInventory;
    private JButton btnInvAdd, btnInvUpdate, btnInvRestock, btnInvDelete;

    private JLabel[] lblWasteCounts;
    private int[] wasteCounts;
    private JButton[] btnWasteAdd, btnWasteMinus;
    private JLabel lblWasteTotal, lblWasteMost, lblWasteDaily;
    private JTable tableWaste;
    private DefaultTableModel modelWaste;
    private JScrollPane scrollWaste;
    private JComboBox<String> comboWasteReason, comboWasteCategory;
    private JButton btnWasteSave, btnWasteReset, btnWasteDelete;

    private JButton btnRepSales, btnRepOrders, btnRepInventory, btnRepWaste;
    private JTable tableReport;
    private DefaultTableModel modelReport;
    private JScrollPane scrollReport;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] WASTE_ITEMS = {"Chicken", "Nuggets", "Fish Fillet", "McCafe", "CokeFloat", "Sundae", "McFlurry"};
    private static final String[] REASONS = {"Overcooked", "Expired", "Customer Return", "Spoiled", "Over-prepared"};
    private static final String[] CATEGORIES = {"Meat", "Snack", "Beverage", "Dessert", "Other"};

    public ManagerFrame(User user, IPermission perm){
        setTitle("Restaurant System - Manager");
        setLayout(null);
        setSize(1040, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        Services();
        Navigation();
        Dashboard();
        Inventory();
        FoodWaste();
        Reports();
        About();

        showPanel(panelDashboard);
    }

    private void Services(){
        RestaurantDataService.IOrder orderData = new RestaurantInMemory.InMemoryOrder();
        RestaurantDataService.IPayment paymentData = new RestaurantInMemory.InMemoryPayment();
        RestaurantDataService.IInventoryItem inventoryData = new RestaurantInMemory.InMemoryInventoryItem();
        RestaurantDataService.IFoodWaste foodWasteData = new RestaurantInMemory.InMemoryFoodWaste();
        RestaurantDataService.IMenuItem menuData = new RestaurantInMemory.InMemoryMenuItem();

        orderService = new OrderService(orderData, menuData);
        paymentService = new PaymentService(paymentData, orderData);
        inventoryService = new InventoryService(inventoryData);
        foodWasteService = new FoodWasteService(foodWasteData);
        reportService = new ReportService(orderData, paymentData, inventoryData, foodWasteData);
    }
    
    private ImageIcon loadIcon(String filename, int size) {
        URL url = getClass().getResource("/icons/" + filename);
        if(url == null) return null;
        Image img = new ImageIcon(url).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void Navigation(){
        int x = 40, y = 10, w = 145, h = 32, gap = 14;

        btnDashboard = new JButton("Dashboard", loadIcon("dashboard.png", 18));
        btnDashboard.setOpaque(true);
        btnDashboard.setIconTextGap(12);
        btnDashboard.setBounds(x, y, w, h);
        btnDashboard.setFocusPainted(false);
        //btnDashboard.setBorderPainted(false);
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setBackground(Color.WHITE);
        btnDashboard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDashboard.setHorizontalAlignment(SwingConstants.LEFT);
        btnDashboard.addActionListener(this);
        add(btnDashboard);
        x += w + gap;

        btnInventory = new JButton("Inventory", loadIcon("inventory.png", 18));
        btnInventory.setOpaque(true);
        btnInventory.setIconTextGap(12);
        btnInventory.setBounds(x, y, w, h);
        btnInventory.setFocusPainted(false);
        //btnInventory.setBorderPainted(false);
        btnInventory.setContentAreaFilled(false);
        btnInventory.setBackground(Color.WHITE);
        btnInventory.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInventory.setHorizontalAlignment(SwingConstants.LEFT);
        btnInventory.addActionListener(this);
        add(btnInventory);
        x += w + gap;

        btnFoodWaste = new JButton("Food Waste", loadIcon("foodWaste.png", 18));
        btnFoodWaste.setOpaque(true);
        btnFoodWaste.setIconTextGap(12);
        btnFoodWaste.setBounds(x, y, w, h);
        btnFoodWaste.setFocusPainted(false);
        //btnFoodWaste.setBorderPainted(false);
        btnFoodWaste.setContentAreaFilled(false);
        btnFoodWaste.setBackground(Color.WHITE);
        btnFoodWaste.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFoodWaste.setHorizontalAlignment(SwingConstants.LEFT);
        btnFoodWaste.addActionListener(this);
        add(btnFoodWaste);
        x += w + gap;

        btnReports = new JButton("Reports", loadIcon("report.png", 18));
        btnReports.setOpaque(true);
        btnReports.setIconTextGap(12);
        btnReports.setBounds(x, y, w, h);
        btnReports.setFocusPainted(false);
        //btnReports.setBorderPainted(false);
        btnReports.setContentAreaFilled(false);
        btnReports.setBackground(Color.WHITE);
        btnReports.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReports.setHorizontalAlignment(SwingConstants.LEFT);
        btnReports.addActionListener(this);
        add(btnReports);
        x += w + gap;

        btnAbout = new JButton("About", loadIcon("about.png", 18));
        btnAbout.setOpaque(true);
        btnAbout.setIconTextGap(12);
        btnAbout.setBounds(x, y, w, h);
        btnAbout.setFocusPainted(false);
        //btnAbout.setBorderPainted(false);
        btnAbout.setContentAreaFilled(false);
        btnAbout.setBackground(Color.WHITE);
        btnAbout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbout.setHorizontalAlignment(SwingConstants.LEFT);
        btnAbout.addActionListener(this);
        add(btnAbout);
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

    private void showPanel(JPanel panel){
        panelDashboard.setVisible(false);
        panelInventory.setVisible(false);
        panelFoodWaste.setVisible(false);
        panelReports.setVisible(false);
        panelAbout.setVisible(false);
        panel.setVisible(true);
    }

    private JLabel dashboardCard(JPanel parent, String title, int x, int y, Color color){
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.setBounds(x, y, 220, 100);
        parent.add(card);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setBounds(10, 10, 200, 20);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(85, 85, 85));
        card.add(lblTitle);

        JLabel lblValue = new JLabel("0", SwingConstants.CENTER);
        lblValue.setBounds(10, 35, 200, 40);
        lblValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblValue.setForeground(color);
        card.add(lblValue);

        return lblValue;
    }

    private void Dashboard(){
        panelDashboard = new JPanel(null);
        panelDashboard.setBounds(20, 50, 975, 600);
        panelDashboard.setBackground(Color.WHITE);
        add(panelDashboard);

        JLabel lblTitle = new JLabel("Dashboard");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelDashboard.add(lblTitle);
        
        JLabel lblSubTitle = new JLabel("Overview of the Restaurant");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        panelDashboard.add(lblSubTitle);
        
        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 940, 1);
        separator.setForeground(Color.BLACK);
        panelDashboard.add(separator);

        lblDashRevenue = dashboardCard(panelDashboard, "Total Revenue", 20, 80, new Color(0, 143, 74));
        lblDashActive = dashboardCard(panelDashboard, "Active Orders", 260, 80, new Color(30, 120, 200));
        lblDashCompleted = dashboardCard(panelDashboard, "Completed Orders", 500, 80, new Color(85, 85, 85));
        lblDashLow = dashboardCard(panelDashboard, "Low Stock Alerts", 740, 80, new Color(200, 50, 50));

        refreshDashboard();
    }

    private void refreshDashboard(){
        HashMap<String, Object> summary = reportService.dashboardSummary();
        double revenue = (double) summary.getOrDefault("totalRevenue", 0.0);
        long active = (long) summary.getOrDefault("activeOrders", 0L);
        long completed = (long) summary.getOrDefault("totalOrders", 0L);
        int low = (int) summary.getOrDefault("lowStockAlerts", 0);

        lblDashRevenue.setText(String.format("%,.2f", revenue));
        lblDashActive.setText(String.valueOf(active));
        lblDashCompleted.setText(String.valueOf(completed));
        lblDashLow.setText(String.valueOf(low));
    }

    private void Inventory(){
        panelInventory = new JPanel(null);
        panelInventory.setBounds(20, 50, 975, 600);
        panelInventory.setBackground(Color.WHITE);
        add(panelInventory);

        JLabel lblTitle = new JLabel("Inventory");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelInventory.add(lblTitle);
        
        JLabel lblSubTitle = new JLabel("Manage stock levels, reorders, and suppliers");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        panelInventory.add(lblSubTitle);
        
        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 940, 1);
        separator.setForeground(Color.BLACK);
        panelInventory.add(separator);

        modelInventory = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Qty", "Unit", "Cost", "Reorder", "Expiry", "Status"}, 0) {
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };
        tableInventory = new JTable(modelInventory);
        tableInventory.setRowHeight(28);
        tableInventory.setFillsViewportHeight(true);
        tableInventory.getTableHeader().setReorderingAllowed(false);
        tableInventory.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableInventory.getTableHeader().setPreferredSize(new Dimension(tableInventory.getPreferredSize().width, 28));
        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(CENTER);
        for(int i = 0; i < tableInventory.getColumnCount(); i++){
            tableInventory.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }
        scrollInventory = new JScrollPane(tableInventory);
        scrollInventory.setBounds(20, 80, 940, 400);
        panelInventory.add(scrollInventory);

        btnInvAdd = new JButton("Add");
        btnInvAdd.setBounds(20, 500, 80, 30);
        btnInvAdd.addActionListener(this);
        panelInventory.add(btnInvAdd);

        btnInvUpdate = new JButton("Update Quantity");
        btnInvUpdate.setBounds(110, 500, 130, 30);
        btnInvUpdate.addActionListener(this);
        panelInventory.add(btnInvUpdate);

        btnInvRestock = new JButton("Restock");
        btnInvRestock.setBounds(250, 500, 90, 30);
        btnInvRestock.addActionListener(this);
        panelInventory.add(btnInvRestock);

        btnInvDelete = new JButton("Delete");
        btnInvDelete.setBounds(350, 500, 90, 30);
        btnInvDelete.addActionListener(this);
        panelInventory.add(btnInvDelete);

        refreshInventory();
    }

    private void refreshInventory(){
        modelInventory.setRowCount(0);
        ArrayList <InventoryItem > items = inventoryService.getAllInventoryItems();
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

    private void FoodWaste(){
        panelFoodWaste = new JPanel(null);
        panelFoodWaste.setBounds(20, 50, 975, 600);
        panelFoodWaste.setBackground(Color.WHITE);
        add(panelFoodWaste);

        JLabel lblTitle = new JLabel("Food Waste");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelFoodWaste.add(lblTitle);
        
        JLabel lblSubTitle = new JLabel("Monitor and reduce food waste");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        panelFoodWaste.add(lblSubTitle);
        
        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 940, 1);
        separator.setForeground(Color.BLACK);
        panelFoodWaste.add(separator);

        wasteCounts = new int[WASTE_ITEMS.length];
        lblWasteCounts = new JLabel[WASTE_ITEMS.length];
        btnWasteAdd = new JButton[WASTE_ITEMS.length];
        btnWasteMinus = new JButton[WASTE_ITEMS.length];

        int y = 120;
        for(int i = 0; i < WASTE_ITEMS.length; i++){
            JLabel lblItem = new JLabel(WASTE_ITEMS[i]);
            lblItem.setBounds(20, y, 100, 25);
            panelFoodWaste.add(lblItem);

            btnWasteAdd[i] = new JButton("+");
            btnWasteAdd[i].setBounds(130, y, 45, 25);
            btnWasteAdd[i].setActionCommand("waste_add:" + i);
            btnWasteAdd[i].addActionListener(this);
            panelFoodWaste.add(btnWasteAdd[i]);

            lblWasteCounts[i] = new JLabel("0", SwingConstants.CENTER);
            lblWasteCounts[i].setBounds(180, y, 40, 25);
            panelFoodWaste.add(lblWasteCounts[i]);

            btnWasteMinus[i] = new JButton("-");
            btnWasteMinus[i].setBounds(225, y, 45, 25);
            btnWasteMinus[i].setActionCommand("waste_minus:" + i);
            btnWasteMinus[i].addActionListener(this);
            panelFoodWaste.add(btnWasteMinus[i]);

            y += 40;
        }

        lblWasteTotal = new JLabel("Total Waste: 0");
        lblWasteTotal.setBounds(20, y + 10, 150, 25);
        panelFoodWaste.add(lblWasteTotal);

        lblWasteMost = new JLabel("Most Wasted: -");
        lblWasteMost.setBounds(320, 80, 250, 25);
        panelFoodWaste.add(lblWasteMost);

        lblWasteDaily = new JLabel("Daily Cost: 0.00");
        lblWasteDaily.setBounds(580, 80, 200, 25);
        panelFoodWaste.add(lblWasteDaily);

        modelWaste = new DefaultTableModel(new String[]{"ID", "Item", "Qty", "Reason", "Category", "Cost", "Date"}, 0) {
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
        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(CENTER);
        for(int i = 0; i < tableWaste.getColumnCount(); i++){
            tableWaste.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }
        scrollWaste = new JScrollPane(tableWaste);
        scrollWaste.setBounds(320, 120, 640, 300);
        panelFoodWaste.add(scrollWaste);

        JLabel lblReason = new JLabel("Reason:");
        lblReason.setBounds(320, 450, 60, 25);
        panelFoodWaste.add(lblReason);

        comboWasteReason = new JComboBox<String>(REASONS);
        comboWasteReason.setBounds(380, 450, 150, 25);
        panelFoodWaste.add(comboWasteReason);

        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setBounds(540, 450, 70, 25);
        panelFoodWaste.add(lblCategory);

        comboWasteCategory = new JComboBox<String>(CATEGORIES);
        comboWasteCategory.setBounds(610, 450, 150, 25);
        panelFoodWaste.add(comboWasteCategory);

        btnWasteSave = new JButton("Save");
        btnWasteSave.setBounds(320, 500, 80, 30);
        btnWasteSave.addActionListener(this);
        panelFoodWaste.add(btnWasteSave);

        btnWasteReset = new JButton("Reset");
        btnWasteReset.setBounds(410, 500, 80, 30);
        btnWasteReset.addActionListener(this);
        panelFoodWaste.add(btnWasteReset);

        btnWasteDelete = new JButton("Delete");
        btnWasteDelete.setBounds(500, 500, 80, 30);
        btnWasteDelete.addActionListener(this);
        panelFoodWaste.add(btnWasteDelete);

        refreshFoodWaste();
    }

    private void refreshFoodWaste(){
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

    private void Reports(){
        panelReports = new JPanel(null);
        panelReports.setBounds(20, 50, 975, 600);
        panelReports.setBackground(Color.WHITE);
        add(panelReports);

        JLabel lblTitle = new JLabel("Reports");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panelReports.add(lblTitle);
        
        JLabel lblSubTitle = new JLabel("Analytics and performance insights");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        panelReports.add(lblSubTitle);
        
        JSeparator separator = new JSeparator();
        separator.setBounds(20, 70, 940, 1);
        separator.setForeground(Color.BLACK);
        panelReports.add(separator);

        btnRepSales = new JButton("Daily Sales");
        btnRepSales.setBounds(20, 80, 110, 30);
        btnRepSales.addActionListener(this);
        panelReports.add(btnRepSales);

        btnRepOrders = new JButton("Orders");
        btnRepOrders.setBounds(140, 80, 80, 30);
        btnRepOrders.addActionListener(this);
        panelReports.add(btnRepOrders);

        btnRepInventory = new JButton("Inventory");
        btnRepInventory.setBounds(230, 80, 90, 30);
        btnRepInventory.addActionListener(this);
        panelReports.add(btnRepInventory);

        btnRepWaste = new JButton("Food Waste");
        btnRepWaste.setBounds(330, 80, 110, 30);
        btnRepWaste.addActionListener(this);
        panelReports.add(btnRepWaste);

        modelReport = new DefaultTableModel(new String[]{"Metric", "Value"}, 0) {
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };
        tableReport = new JTable(modelReport);
        tableReport.setRowHeight(25);
        tableReport.setFillsViewportHeight(true);
        tableReport.getTableHeader().setReorderingAllowed(false);
        tableReport.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableReport.getTableHeader().setPreferredSize(new Dimension(tableReport.getPreferredSize().width, 28));
        DefaultTableCellRenderer centerCollumn = new DefaultTableCellRenderer();
        centerCollumn.setHorizontalAlignment(CENTER);
        for(int i = 0; i < tableReport.getColumnCount(); i++){
            tableReport.getColumnModel().getColumn(i).setCellRenderer(centerCollumn);
        }
        scrollReport = new JScrollPane(tableReport);
        scrollReport.setBounds(20, 120, 940, 450);
        panelReports.add(scrollReport);
    }

    private void About() {
        panelAbout = new JPanel(null);
        panelAbout.setBounds(20, 50, 975, 600);
        panelAbout.setBackground(Color.WHITE);
        add(panelAbout);

        JLabel lblTitle = new JLabel("About");
        lblTitle.setBounds(20, 20, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        panelAbout.add(lblTitle);

        JLabel lblSystem = new JLabel("Restaurant Management System");
        lblSystem.setBounds(20, 70, 400, 25);
        lblSystem.setFont(new Font("Arial", Font.BOLD, 16));
        panelAbout.add(lblSystem);

        String[] members = {
            "Tom Justine De Jesus",
            "Allysa Rose Tolarba",
            "Nolan Claveria",
            "Aina Bulawin",
            "Alfred Lim"
        };

        String[] imagePaths = {
            "/images/tom-justine-de-jesus.jpg",
            "/images/allysa-rose-tolarba.jpg",
            "/images/nolan-claveria.jpg",
            "/images/aina-bulawin.jpg",
            "/images/alfred-lim.jpg"
        };

        int startX = 20;
        int startY = 110;
        int imgW = 160;
        int imgH = 160;
        int gap = 30;

        for(int i = 0; i < members.length; i++) {
            JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
            imgLabel.setBounds(startX + (i * (imgW + gap)), startY, imgW, imgH);
            imgLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

            URL imgUrl = getClass().getResource(imagePaths[i]);
            if(imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image scaled = icon.getImage().getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            }
            panelAbout.add(imgLabel);

            JLabel lblName = new JLabel(members[i], SwingConstants.CENTER);
            lblName.setBounds(startX + (i * (imgW + gap)), startY + imgH + 8, imgW, 25);
            panelAbout.add(lblName);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnDashboard){
            showPanel(panelDashboard);
            refreshDashboard();
        }else if(e.getSource() == btnInventory){
            showPanel(panelInventory);
            refreshInventory();
        }else if(e.getSource() == btnFoodWaste){
            showPanel(panelFoodWaste);
            refreshFoodWaste();
        }else if(e.getSource() == btnReports){
            showPanel(panelReports);
        }else if(e.getSource() == btnAbout){
            showPanel(panelAbout);
        }else if(e.getSource() == btnLogout){
            System.exit(0);
        }
        
        if(e.getSource() == btnInvAdd){
            addInventory();
        }else if(e.getSource() == btnInvUpdate){
            updateInventoryQty();
        }else if(e.getSource() == btnInvRestock){
            restockInventory();
        }else if(e.getSource() == btnInvDelete){
            deleteInventory();
        }
        
        if(e.getSource() == btnWasteSave){
            saveWaste();
        }else if(e.getSource() == btnWasteReset){
            resetWaste();
        }else if(e.getSource() == btnWasteDelete){
            deleteWaste();
        }else if(e.getSource() == btnRepSales){
            runSalesReport();
        }else if(e.getSource() == btnRepOrders){
            runOrdersReport();
        }else if(e.getSource() == btnRepInventory){
            runInventoryReport();
        }else if(e.getSource() == btnRepWaste){
            runWasteReport();
        }else{
            String cmd = e.getActionCommand();
            if(cmd != null && cmd.startsWith("waste_add:")){
                int idx = Integer.parseInt(cmd.substring(10));
                wasteCounts[idx]++;
                lblWasteCounts[idx].setText(String.valueOf(wasteCounts[idx]));
                updateWasteTotal();
            }else if(cmd != null && cmd.startsWith("waste_minus:")){
                int idx = Integer.parseInt(cmd.substring(12));
                if(wasteCounts[idx] > 0){
                    wasteCounts[idx]--;
                    lblWasteCounts[idx].setText(String.valueOf(wasteCounts[idx]));
                    updateWasteTotal();
                }
            }
        }
    }

    private void updateWasteTotal(){
        int total = 0;
        for(int c : wasteCounts){
            total += c;
        }
        lblWasteTotal.setText("Total Waste: " + total);
    }

    private void addInventory(){
        String name = JOptionPane.showInputDialog(this, "Item name:");
        if(name == null || name.trim().isEmpty()){
            return;
        }
        String category = JOptionPane.showInputDialog(this, "Category:");
        if(category == null){
            return;
        }
        String qtyStr = JOptionPane.showInputDialog(this, "Quantity:");
        if(qtyStr == null){
            return;
        }
        String unit = JOptionPane.showInputDialog(this, "Unit:");
        if(unit == null){
            return;
        }
        String costStr = JOptionPane.showInputDialog(this, "Cost per unit:");
        if(costStr == null){
            return;
        }
        String reorderStr = JOptionPane.showInputDialog(this, "Reorder level:");
        if(reorderStr == null){
            return;
        }
        String supplier = JOptionPane.showInputDialog(this, "Supplier:");
        if(supplier == null){
            return;
        }
        String expiry = JOptionPane.showInputDialog(this, "Expiry (yyyy-MM-dd), blank for none:");

        try{
            int qty = Integer.parseInt(qtyStr);
            double cost = Double.parseDouble(costStr);
            int reorder = Integer.parseInt(reorderStr);
            LocalDate expDate = null;
            if(expiry != null && !expiry.trim().isEmpty()){
                expDate = LocalDate.parse(expiry, DATE_FMT);
            }
            inventoryService.addInventoryItem(name, category, qty, unit, cost, reorder, supplier, expDate);
            refreshInventory();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInventoryQty(){
        int row = tableInventory.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select an item.");
            return;
        }
        int id = (int) modelInventory.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "New quantity:");
        if(input == null){
            return;
        }
        try{
            inventoryService.updateQuantity(id, Integer.parseInt(input));
            refreshInventory();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restockInventory(){
        int row = tableInventory.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select an item.");
            return;
        }
        int id = (int) modelInventory.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Quantity to add:");
        if(input == null){
            return;
        }
        try{
            inventoryService.restockInventory(id, Integer.parseInt(input));
            refreshInventory();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInventory(){
        int row = tableInventory.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select an item.");
            return;
        }
        int id = (int) modelInventory.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this item?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        try{
            inventoryService.deleteInventoryItem(id);
            refreshInventory();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveWaste(){
        String reason = (String) comboWasteReason.getSelectedItem();
        String category = (String) comboWasteCategory.getSelectedItem();
        String costStr = JOptionPane.showInputDialog(this, "Cost per unit:");
        if(costStr == null){
            return;
        }

        double costPerUnit;
        try{
            costPerUnit = Double.parseDouble(costStr);
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Invalid cost.");
            return;
        }

        boolean saved = false;
        for(int i = 0; i < WASTE_ITEMS.length; i++){
            if(wasteCounts[i] <= 0){
                continue;
            }
            try{
                double totalCost = wasteCounts[i] * costPerUnit;
                foodWasteService.recordWaste(WASTE_ITEMS[i], wasteCounts[i], "units", reason, totalCost, "staff", category);
                saved = true;
            }catch(ValidationException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if(!saved){
            JOptionPane.showMessageDialog(this, "No quantities entered.");
            return;
        }

        refreshFoodWaste();
        resetWaste();
        JOptionPane.showMessageDialog(this, "Saved.");
    }

    private void resetWaste(){
        for(int i = 0; i < WASTE_ITEMS.length; i++){
            wasteCounts[i] = 0;
            lblWasteCounts[i].setText("0");
        }
        updateWasteTotal();
    }

    private void deleteWaste(){
        int row = tableWaste.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select a row.");
            return;
        }
        int id = (int) modelWaste.getValueAt(row, 0);
        try{
            foodWasteService.deleteWaste(id);
            refreshFoodWaste();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runSalesReport(){
        String dateStr = JOptionPane.showInputDialog(this, "Date (yyyy-MM-dd):");
        if(dateStr == null){
            return;
        }
        try{
            LocalDate date = LocalDate.parse(dateStr, DATE_FMT);
            HashMap<String, Object> report = reportService.dailySalesReport(date);
            modelReport.setColumnIdentifiers(new String[]{"Metric", "Value"});
            modelReport.setRowCount(0);
            modelReport.addRow(new Object[]{"Date", date});
            modelReport.addRow(new Object[]{"Revenue", report.get("totalRevenue")});
            modelReport.addRow(new Object[]{"Tips", report.get("totalTips")});
            modelReport.addRow(new Object[]{"Transactions", report.get("transactionCount")});
            modelReport.addRow(new Object[]{"Average", report.get("averageTransaction")});
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runOrdersReport(){
        HashMap<String, Object> report = reportService.orderReport();
        modelReport.setColumnIdentifiers(new String[]{"Metric", "Value"});
        modelReport.setRowCount(0);
        modelReport.addRow(new Object[]{"Total Orders", report.get("totalOrders")});
        modelReport.addRow(new Object[]{"Completed", report.get("completedOrders")});
        modelReport.addRow(new Object[]{"Revenue", report.get("totalRevenue")});
        modelReport.addRow(new Object[]{"Average Order", report.get("averageOrderValue")});
    }

    private void runInventoryReport(){
        HashMap<String, Object> report = reportService.inventoryReport();
        modelReport.setColumnIdentifiers(new String[]{"Metric", "Value"});
        modelReport.setRowCount(0);
        modelReport.addRow(new Object[]{"Total Items", report.get("totalItems")});
        modelReport.addRow(new Object[]{"Low Stock", report.get("lowStockCount")});
        modelReport.addRow(new Object[]{"Expiring Soon", report.get("expiringSoonCount")});
        modelReport.addRow(new Object[]{"Total Value", report.get("totalInventoryValue")});
    }

    private void runWasteReport(){
        String start = JOptionPane.showInputDialog(this, "Start date (yyyy-MM-dd):");
        if(start == null){
            return;
        }
        String end = JOptionPane.showInputDialog(this, "End date (yyyy-MM-dd):");
        if(end == null){
            return;
        }
        try{
            LocalDate strt = LocalDate.parse(start, DATE_FMT);
            LocalDate en = LocalDate.parse(end, DATE_FMT);
            HashMap<String, Object> report = reportService.wasteReport(strt, en);
            modelReport.setColumnIdentifiers(new String[]{"Metric", "Value"});
            modelReport.setRowCount(0);
            modelReport.addRow(new Object[]{"Records", report.get("totalWasteRecords")});
            modelReport.addRow(new Object[]{"Total Cost", report.get("totalWasteCost")});
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
