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

    private JLabel lblTitle, lblSubTitle;

    private JPanel cardRevenue, cardActive, cardCompleted, cardLow;

    private JLabel lblCardRevenueTitle, lblCardRevenueValue;
    private JLabel lblCardActiveTitle, lblCardActiveValue;
    private JLabel lblCardCompletedTitle, lblCardCompletedValue;
    private JLabel lblCardLowTitle, lblCardLowValue;

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

        cardRevenue = new JPanel(null);
        cardRevenue.setBackground(Color.WHITE);
        cardRevenue.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        cardRevenue.setBounds(20, 80, 300, 100);
        add(cardRevenue);

        lblCardRevenueTitle = new JLabel("Total Revenue", SwingConstants.CENTER);
        lblCardRevenueTitle.setBounds(10, 10, 280, 20);
        lblCardRevenueTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCardRevenueTitle.setForeground(new Color(85, 85, 85));
        cardRevenue.add(lblCardRevenueTitle);

        lblCardRevenueValue = new JLabel("0", SwingConstants.CENTER);
        lblCardRevenueValue.setBounds(10, 35, 280, 40);
        lblCardRevenueValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblCardRevenueValue.setForeground(new Color(0, 143, 74));
        cardRevenue.add(lblCardRevenueValue);

        cardActive = new JPanel(null);
        cardActive.setBackground(Color.WHITE);
        cardActive.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        cardActive.setBounds(340, 80, 300, 100);
        add(cardActive);

        lblCardActiveTitle = new JLabel("Active Orders", SwingConstants.CENTER);
        lblCardActiveTitle.setBounds(10, 10, 280, 20);
        lblCardActiveTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCardActiveTitle.setForeground(new Color(85, 85, 85));
        cardActive.add(lblCardActiveTitle);

        lblCardActiveValue = new JLabel("0", SwingConstants.CENTER);
        lblCardActiveValue.setBounds(10, 35, 280, 40);
        lblCardActiveValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblCardActiveValue.setForeground(new Color(30, 120, 200));
        cardActive.add(lblCardActiveValue);

        cardCompleted = new JPanel(null);
        cardCompleted.setBackground(Color.WHITE);
        cardCompleted.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        cardCompleted.setBounds(660, 80, 300, 100);
        add(cardCompleted);

        lblCardCompletedTitle = new JLabel("Completed Orders", SwingConstants.CENTER);
        lblCardCompletedTitle.setBounds(10, 10, 280, 20);
        lblCardCompletedTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCardCompletedTitle.setForeground(new Color(85, 85, 85));
        cardCompleted.add(lblCardCompletedTitle);

        lblCardCompletedValue = new JLabel("0", SwingConstants.CENTER);
        lblCardCompletedValue.setBounds(10, 35, 280, 40);
        lblCardCompletedValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblCardCompletedValue.setForeground(new Color(85, 85, 85));
        cardCompleted.add(lblCardCompletedValue);

        cardLow = new JPanel(null);
        cardLow.setBackground(Color.WHITE);
        cardLow.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        cardLow.setBounds(980, 80, 300, 100);
        add(cardLow);

        lblCardLowTitle = new JLabel("Low Stock Alerts", SwingConstants.CENTER);
        lblCardLowTitle.setBounds(10, 10, 280, 20);
        lblCardLowTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCardLowTitle.setForeground(new Color(85, 85, 85));
        cardLow.add(lblCardLowTitle);

        lblCardLowValue = new JLabel("0", SwingConstants.CENTER);
        lblCardLowValue.setBounds(10, 35, 280, 40);
        lblCardLowValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblCardLowValue.setForeground(new Color(200, 50, 50));
        cardLow.add(lblCardLowValue);
    }

    public void refreshDashboard() {
        HashMap<String, Object> summary = reportService.dashboardSummary();

        double revenue = 0.0;
        long active = 0L;
        long completed = 0L;
        int low = 0;

        if(summary.get("totalRevenue") != null){
            revenue = (double) summary.get("totalRevenue");
        }
        if(summary.get("activeOrders") != null){
            active = (long) summary.get("activeOrders");
        }
        if(summary.get("totalOrders") != null){
            completed = (long) summary.get("totalOrders");
        }
        if(summary.get("lowStockAlerts") != null){
            low = (int) summary.get("lowStockAlerts");
        }

        lblCardRevenueValue.setText(String.format("%,.2f", revenue));
        lblCardActiveValue.setText(String.valueOf(active));
        lblCardCompletedValue.setText(String.valueOf(completed));
        lblCardLowValue.setText(String.valueOf(low));
    }
}
