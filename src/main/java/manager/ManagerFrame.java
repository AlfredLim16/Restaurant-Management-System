package manager;

import cashier.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;
import user.DbUser;
import user.IPermission;
import user.IUser;
import user.LoginFrame;
import user.User;

public class ManagerFrame extends JFrame implements ActionListener {

    private OrderService orderService;
    private ReportService reportService;
    private PaymentService paymentService;
    private InventoryService inventoryService;
    private FoodWasteService foodWasteService;
    private MenuItemService menuItemService;
    private UserManagementService userManagementService;

    private DashboardPanel panelDashboard;
    private InventoryPanel panelInventory;
    private FoodWastePanel panelFoodWaste;
    private ReportsPanel panelReports;
    private MenuItemPanel panelMenuItem;
    private UserManagementPanel panelUsers;

    private JLabel lblTitle, lblSystem, memberImage0, memberImage1, memberImage2, memberImage3, memberImage4;
    private JLabel memberName0, memberName1, memberName2, memberName3, memberName4;
    private JPanel panelAbout;
    private JButton btnDashboard, btnInventory, btnFoodWaste, btnReports, btnMenuItem, btnUsers, btnAbout, btnLogout;

    public ManagerFrame(User user, IPermission permission){
        setTitle("Byte Bite - Manager");
        setLayout(null);
        setSize(1360, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        Services();
        Navigation();
        About();

        panelDashboard = new DashboardPanel(reportService);
        panelDashboard.setBounds(20, 50, 1280, 600);
        add(panelDashboard);

        panelInventory = new InventoryPanel(inventoryService, foodWasteService, this);
        panelInventory.setBounds(20, 50, 1280, 600);
        add(panelInventory);

        panelFoodWaste = new FoodWastePanel(foodWasteService, this);
        panelFoodWaste.setBounds(20, 50, 1280, 600);
        add(panelFoodWaste);

        panelReports = new ReportsPanel(reportService, this);
        panelReports.setBounds(20, 50, 1280, 600);
        add(panelReports);

        panelMenuItem = new MenuItemPanel(menuItemService, this);
        panelMenuItem.setBounds(20, 50, 1280, 600);
        add(panelMenuItem);

        panelUsers = new UserManagementPanel(userManagementService, this);
        panelUsers.setBounds(20, 50, 1280, 600);
        add(panelUsers);

        showPanel(panelDashboard);
        panelDashboard.refreshDashboard();
    }

    private void Services(){
        IOrder orderData = new DbOrder();
        IPayment paymentData = new DbPayment();
        IInventoryItem inventoryData = new DbInventoryItem();
        IFoodWaste foodWasteData = new DbFoodWaste();
        IMenuItem menuData = new DbMenuItem();
        IMenuItemIngredient ingredientData = new DbMenuItemIngredient();
        IUser userData = new DbUser();

        orderService = new OrderService(orderData, menuData, ingredientData, inventoryData);
        paymentService = new PaymentService(paymentData, orderData);
        inventoryService = new InventoryService(inventoryData);
        foodWasteService = new FoodWasteService(foodWasteData, inventoryData);
        reportService = new ReportService(orderData, paymentData, inventoryData, foodWasteData);
        menuItemService = new MenuItemService(menuData, ingredientData, inventoryData);
        userManagementService = new UserManagementService(userData);
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
        btnDashboard = new JButton("Dashboard", loadIcon("dashboard.png", 18));
        btnDashboard.setOpaque(true);
        btnDashboard.setIconTextGap(12);
        btnDashboard.setBounds(40, 10, 145, 32);
        btnDashboard.setFocusPainted(false);
        //btnDashboard.setBorderPainted(false);
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setBackground(Color.WHITE);
        btnDashboard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDashboard.setHorizontalAlignment(SwingConstants.LEFT);
        btnDashboard.addActionListener(this);
        add(btnDashboard);

        btnInventory = new JButton("Inventory", loadIcon("inventory.png", 18));
        btnInventory.setOpaque(true);
        btnInventory.setIconTextGap(12);
        btnInventory.setBounds(199, 10, 145, 32);
        btnInventory.setFocusPainted(false);
        //btnInventory.setBorderPainted(false);
        btnInventory.setContentAreaFilled(false);
        btnInventory.setBackground(Color.WHITE);
        btnInventory.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInventory.setHorizontalAlignment(SwingConstants.LEFT);
        btnInventory.addActionListener(this);
        add(btnInventory);

        btnMenuItem = new JButton("Menu Items", loadIcon("order.png", 18));
        btnMenuItem.setOpaque(true);
        btnMenuItem.setIconTextGap(12);
        btnMenuItem.setBounds(358, 10, 145, 32);
        btnMenuItem.setFocusPainted(false);
        //btnMenuItem.setBorderPainted(false);
        btnMenuItem.setContentAreaFilled(false);
        btnMenuItem.setBackground(Color.WHITE);
        btnMenuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenuItem.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuItem.addActionListener(this);
        add(btnMenuItem);

        btnFoodWaste = new JButton("Food Waste", loadIcon("foodWaste.png", 18));
        btnFoodWaste.setOpaque(true);
        btnFoodWaste.setIconTextGap(12);
        btnFoodWaste.setBounds(517, 10, 145, 32);
        btnFoodWaste.setFocusPainted(false);
        //btnFoodWaste.setBorderPainted(false);
        btnFoodWaste.setContentAreaFilled(false);
        btnFoodWaste.setBackground(Color.WHITE);
        btnFoodWaste.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFoodWaste.setHorizontalAlignment(SwingConstants.LEFT);
        btnFoodWaste.addActionListener(this);
        add(btnFoodWaste);

        btnReports = new JButton("Reports", loadIcon("report.png", 18));
        btnReports.setOpaque(true);
        btnReports.setIconTextGap(12);
        btnReports.setBounds(676, 10, 145, 32);
        btnReports.setFocusPainted(false);
        //btnReports.setBorderPainted(false);
        btnReports.setContentAreaFilled(false);
        btnReports.setBackground(Color.WHITE);
        btnReports.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReports.setHorizontalAlignment(SwingConstants.LEFT);
        btnReports.addActionListener(this);
        add(btnReports);

        btnUsers = new JButton("Users", loadIcon("settings.png", 18));
        btnUsers.setOpaque(true);
        btnUsers.setIconTextGap(12);
        btnUsers.setBounds(835, 10, 145, 32);
        btnUsers.setFocusPainted(false);
        //btnUsers.setBorderPainted(false);
        btnUsers.setContentAreaFilled(false);
        btnUsers.setBackground(Color.WHITE);
        btnUsers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUsers.setHorizontalAlignment(SwingConstants.LEFT);
        btnUsers.addActionListener(this);
        add(btnUsers);

        btnAbout = new JButton("About", loadIcon("about.png", 18));
        btnAbout.setOpaque(true);
        btnAbout.setIconTextGap(12);
        btnAbout.setBounds(994, 10, 145, 32);
        btnAbout.setFocusPainted(false);
        //btnAbout.setBorderPainted(false);
        btnAbout.setContentAreaFilled(false);
        btnAbout.setBackground(Color.WHITE);
        btnAbout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbout.setHorizontalAlignment(SwingConstants.LEFT);
        btnAbout.addActionListener(this);
        add(btnAbout);

        btnLogout = new JButton("Logout", loadIcon("log-out.png", 18));
        btnLogout.setOpaque(true);
        btnLogout.setIconTextGap(12);
        btnLogout.setBounds(1153, 10, 145, 32);
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
        panelAbout.setBounds(20, 50, 1280, 600);
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
            "Buthaina Al-harmali",
            "Alfred Lim"
        };

        String[] imagePaths = {
            "/images/tom-justine-de-jesus.jpg",
            "/images/allysa-rose-tolarba.jpg",
            "/images/nolan-claveria.jpg",
            "/images/aina-bulawin.jpg",
            "/images/alfred-lim.jpg"
        };

        int imgW = 220;
        int imgH = 220;

        memberImage0 = new JLabel("", SwingConstants.CENTER);
        memberImage0.setBounds(20, 110, imgW, imgH);
        memberImage0.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        URL imgUrl0 = getClass().getResource(imagePaths[0]);
        if(imgUrl0 != null){
            Image scaled0 = new ImageIcon(imgUrl0).getImage().getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
            memberImage0.setIcon(new ImageIcon(scaled0));
        }
        panelAbout.add(memberImage0);
        memberName0 = new JLabel(members[0], SwingConstants.CENTER);
        memberName0.setBounds(20, 338, imgW, 25);
        panelAbout.add(memberName0);

        memberImage1 = new JLabel("", SwingConstants.CENTER);
        memberImage1.setBounds(280, 110, imgW, imgH);
        memberImage1.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        URL imgUrl1 = getClass().getResource(imagePaths[1]);
        if(imgUrl1 != null){
            Image scaled1 = new ImageIcon(imgUrl1).getImage().getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
            memberImage1.setIcon(new ImageIcon(scaled1));
        }
        panelAbout.add(memberImage1);
        memberName1 = new JLabel(members[1], SwingConstants.CENTER);
        memberName1.setBounds(280, 338, imgW, 25);
        panelAbout.add(memberName1);

        memberImage2 = new JLabel("", SwingConstants.CENTER);
        memberImage2.setBounds(540, 110, imgW, imgH);
        memberImage2.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        URL imgUrl2 = getClass().getResource(imagePaths[2]);
        if(imgUrl2 != null){
            Image scaled2 = new ImageIcon(imgUrl2).getImage().getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
            memberImage2.setIcon(new ImageIcon(scaled2));
        }
        panelAbout.add(memberImage2);
        memberName2 = new JLabel(members[2], SwingConstants.CENTER);
        memberName2.setBounds(540, 338, imgW, 25);
        panelAbout.add(memberName2);

        memberImage3 = new JLabel("", SwingConstants.CENTER);
        memberImage3.setBounds(800, 110, imgW, imgH);
        memberImage3.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        URL imgUrl3 = getClass().getResource(imagePaths[3]);
        if(imgUrl3 != null){
            Image scaled3 = new ImageIcon(imgUrl3).getImage().getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
            memberImage3.setIcon(new ImageIcon(scaled3));
        }
        panelAbout.add(memberImage3);
        memberName3 = new JLabel(members[3], SwingConstants.CENTER);
        memberName3.setBounds(800, 338, imgW, 25);
        panelAbout.add(memberName3);

        memberImage4 = new JLabel("", SwingConstants.CENTER);
        memberImage4.setBounds(1060, 110, imgW, imgH);
        memberImage4.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        URL imgUrl4 = getClass().getResource(imagePaths[4]);
        if(imgUrl4 != null){
            Image scaled4 = new ImageIcon(imgUrl4).getImage().getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
            memberImage4.setIcon(new ImageIcon(scaled4));
        }
        panelAbout.add(memberImage4);
        memberName4 = new JLabel(members[4], SwingConstants.CENTER);
        memberName4.setBounds(1060, 338, imgW, 25);
        panelAbout.add(memberName4);
    }

    private void showPanel(JPanel panel){
        panelDashboard.setVisible(false);
        panelInventory.setVisible(false);
        panelFoodWaste.setVisible(false);
        panelReports.setVisible(false);
        panelMenuItem.setVisible(false);
        panelUsers.setVisible(false);
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
        }else if(e.getSource() == btnMenuItem){
            showPanel(panelMenuItem);
            panelMenuItem.refreshMenuItems();
        }else if(e.getSource() == btnUsers){
            showPanel(panelUsers);
            panelUsers.refreshUsers();
        }else if(e.getSource() == btnAbout){
            showPanel(panelAbout);
        }else if(e.getSource() == btnLogout){
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}
