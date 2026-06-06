package manager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel implements ActionListener {

    private JTable tableReport;
    private JSeparator separator;
    private final JFrame parentFrame;
    private JScrollPane scrollReport;
    private JLabel lblTitle, lblSubTitle;
    private DefaultTableModel modelReport;
    private final ReportService reportService;
    private DefaultTableCellRenderer centerColumn;
    private JButton btnRepSales, btnRepOrders, btnRepInventory, btnRepWaste;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportsPanel(ReportService reportService, JFrame parentFrame){
        this.reportService = reportService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        Reports();
    }

    private void Reports(){
        lblTitle = new JLabel("Reports");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Analytics and performance insights");
        lblSubTitle.setBounds(20, 35, 300, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 70, 1100, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        btnRepSales = new JButton("Daily Sales");
        btnRepSales.setBounds(20, 80, 110, 30);
        btnRepSales.addActionListener(this);
        add(btnRepSales);

        btnRepOrders = new JButton("Orders");
        btnRepOrders.setBounds(140, 80, 80, 30);
        btnRepOrders.addActionListener(this);
        add(btnRepOrders);

        btnRepInventory = new JButton("Inventory");
        btnRepInventory.setBounds(230, 80, 90, 30);
        btnRepInventory.addActionListener(this);
        add(btnRepInventory);

        btnRepWaste = new JButton("Food Waste");
        btnRepWaste.setBounds(330, 80, 110, 30);
        btnRepWaste.addActionListener(this);
        add(btnRepWaste);

        modelReport = new DefaultTableModel(new String[]{"Metric", "Value"}, 0) {
            @Override
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

        centerColumn = new DefaultTableCellRenderer();
        centerColumn.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tableReport.getColumnCount(); i++){
            tableReport.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
        }

        scrollReport = new JScrollPane(tableReport);
        scrollReport.setBounds(20, 120, 1100, 450);
        add(scrollReport);
    }

    private void runSalesReport(){
        String dateStr = JOptionPane.showInputDialog(parentFrame, "Date (yyyy-MM-dd):");
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
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        String startString = JOptionPane.showInputDialog(parentFrame, "Start date (yyyy-MM-dd):");
        if(startString == null){
            return;
        }
        String endString = JOptionPane.showInputDialog(parentFrame, "End date (yyyy-MM-dd):");
        if(endString == null){
            return;
        }
        try{
            LocalDate start = LocalDate.parse(startString, DATE_FMT);
            LocalDate end = LocalDate.parse(endString, DATE_FMT);
            HashMap<String, Object> report = reportService.wasteReport(start, end);
            modelReport.setColumnIdentifiers(new String[]{"Metric", "Value"});
            modelReport.setRowCount(0);
            modelReport.addRow(new Object[]{"Records", report.get("totalWasteRecords")});
            modelReport.addRow(new Object[]{"Total Cost", report.get("totalWasteCost")});
        }catch(Exception ex){
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnRepSales){
            runSalesReport();
        }else if(e.getSource() == btnRepOrders){
            runOrdersReport();
        }else if(e.getSource() == btnRepInventory){
            runInventoryReport();
        }else if(e.getSource() == btnRepWaste){
            runWasteReport();
        }
    }
}
