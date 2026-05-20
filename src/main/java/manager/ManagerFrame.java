package manager;

import cashier.IMenuItem;
import cashier.IOrder;
import cashier.IPayment;
import cashier.InMemoryMenuItem;
import cashier.InMemoryOrder;
import cashier.InMemoryPayment;
import cashier.OrderService;
import cashier.PaymentService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;
import user.IPermission;
import user.User;

public class ManagerFrame extends JFrame implements ActionListener {

    private JLabel lblTitle, lblSystem;
    private OrderService orderService;
    private ReportService reportService;
    private PaymentService paymentService;
    private InventoryService inventoryService;
    private FoodWasteService foodWasteService;
    private DashboardPanel panelDashboard;
    private InventoryPanel panelInventory;
    private FoodWastePanel panelFoodWaste;
    private ReportsPanel panelReports;
    private JPanel panelAbout;
    private JButton btnDashboard, btnInventory, btnFoodWaste, btnReports, btnAbout, btnLogout;

    public ManagerFrame(User user, IPermission permission){
        setTitle("Byte Bite - Manager");
        setLayout(null);
        setSize(1040, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        Services();
        Navigation();
        About();

        panelDashboard = new DashboardPanel(reportService);
        panelDashboard.setBounds(20, 50, 975, 600);
        add(panelDashboard);

        panelInventory = new InventoryPanel(inventoryService, this);
        panelInventory.setBounds(20, 50, 975, 600);
        add(panelInventory);

        panelFoodWaste = new FoodWastePanel(foodWasteService, this);
        panelFoodWaste.setBounds(20, 50, 975, 600);
        add(panelFoodWaste);

        panelReports = new ReportsPanel(reportService, this);
        panelReports.setBounds(20, 50, 975, 600);
        add(panelReports);

        showPanel(panelDashboard);
        panelDashboard.refreshDashboard();
    }

    private void Services(){
        IOrder orderData = new InMemoryOrder();
        IPayment paymentData = new InMemoryPayment();
        IInventoryItem inventoryData = new InMemoryInventoryItem();
        IFoodWaste foodWasteData = new InMemoryFoodWaste();
        IMenuItem menuData = new InMemoryMenuItem();

        orderService = new OrderService(orderData, menuData);
        paymentService = new PaymentService(paymentData, orderData);
        inventoryService = new InventoryService(inventoryData);
        foodWasteService = new FoodWasteService(foodWasteData);
        reportService = new ReportService(orderData, paymentData, inventoryData, foodWasteData);
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

    private void About(){
        panelAbout = new JPanel(null);
        panelAbout.setBounds(20, 50, 975, 600);
        panelAbout.setBackground(Color.WHITE);
        add(panelAbout);

        lblTitle = new JLabel("About");
        lblTitle.setBounds(20, 20, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        panelAbout.add(lblTitle);

        lblSystem = new JLabel("Restaurant Management System");
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

        for(int i = 0; i < members.length; i++){
            JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
            imgLabel.setBounds(startX + (i * (imgW + gap)), startY, imgW, imgH);
            imgLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

            URL imgUrl = getClass().getResource(imagePaths[i]);
            if(imgUrl != null){
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

    private void showPanel(JPanel panel){
        panelDashboard.setVisible(false);
        panelInventory.setVisible(false);
        panelFoodWaste.setVisible(false);
        panelReports.setVisible(false);
        panelAbout.setVisible(false);
        panel.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnDashboard){
            showPanel(panelDashboard);
            panelDashboard.refreshDashboard();
        }else if(e.getSource() == btnInventory){
            showPanel(panelInventory);
            panelInventory.refreshInventory();
        }else if(e.getSource() == btnFoodWaste){
            showPanel(panelFoodWaste);
            panelFoodWaste.refreshFoodWaste();
        }else if(e.getSource() == btnReports){
            showPanel(panelReports);
        }else if(e.getSource() == btnAbout){
            showPanel(panelAbout);
        }else if(e.getSource() == btnLogout){
            System.exit(0);
        }
    }
}
