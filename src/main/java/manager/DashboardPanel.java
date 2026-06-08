package manager;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class DashboardPanel extends JPanel {

    private JSeparator separator;
    private final ReportService reportService;
    private JLabel lblTitle, lblSubTitle, lblCardTitle, lblCardValue, lblDashRevenue, lblDashActive, lblDashCompleted, lblDashLow;

    public DashboardPanel(ReportService reportService) {
        this.reportService = reportService;
        setLayout(null);
        setBackground(Color.WHITE);
        Dashboard();
    }

    private void Dashboard() {
        lblTitle = new JLabel("Dashboard");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Overview of the Restaurant");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 70, 1260, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        lblDashRevenue = dashboardCard("Total Revenue", 20, 80, new Color(0, 143, 74));
        lblDashActive = dashboardCard("Active Orders", 340, 80, new Color(30, 120, 200));
        lblDashCompleted = dashboardCard("Completed Orders", 660, 80, new Color(85, 85, 85));
        lblDashLow = dashboardCard("Low Stock Alerts", 980, 80, new Color(200, 50, 50));
    }

    private JLabel dashboardCard(String title, int x, int y, Color color) {
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.setBounds(x, y, 300, 100);
        add(card);

        lblCardTitle = new JLabel(title, SwingConstants.CENTER);
        lblCardTitle.setBounds(10, 10, 250, 20);
        lblCardTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCardTitle.setForeground(new Color(85, 85, 85));
        card.add(lblCardTitle);

        lblCardValue = new JLabel("0", SwingConstants.CENTER);
        lblCardValue.setBounds(10, 35, 250, 40);
        lblCardValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblCardValue.setForeground(color);
        card.add(lblCardValue);

        return lblCardValue;
    }

    public void refreshDashboard() {
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
}