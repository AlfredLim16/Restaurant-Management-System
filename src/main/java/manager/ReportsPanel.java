package manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel implements ActionListener {

    private JSeparator separator;
    private JTable tableReport;
    private final JFrame parentFrame;
    private JScrollPane scrollReport;
    private JLabel lblTitle, lblSubTitle;
    private DefaultTableModel modelReport;
    private final ReportService reportService;
    private DefaultTableCellRenderer centerColumn;
    private JButton btnRepSales, btnRepOrders, btnRepInventory, btnRepWaste;

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
        separator.setBounds(20, 70, 1260, 1);
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
        scrollReport.setBounds(20, 120, 1260, 450);
        add(scrollReport);
    }

    private LocalDate pickDate(String title, ArrayList<LocalDate> availableDates){
        if(availableDates.isEmpty()){
            JOptionPane.showMessageDialog(parentFrame, "No dates available.", title, JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        DatePickerDialog dialog = new DatePickerDialog(parentFrame, title, availableDates);
        dialog.setVisible(true);
        return dialog.getChosenDate();
    }

    private void runSalesReport(){
        ArrayList<LocalDate> dates = reportService.getAvailableSalesDates();
        LocalDate date = pickDate("Select Sales Date", dates);
        if(date == null){
            return;
        }
        try{
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
        ArrayList<LocalDate> dates = reportService.getAvailableWasteDates();
        if(dates.isEmpty()){
            JOptionPane.showMessageDialog(parentFrame, "No waste dates available.", "Food Waste Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        LocalDate start = pickDate("Select Start Date", dates);
        if(start == null){
            return;
        }
        LocalDate end = pickDate("Select End Date", dates);
        if(end == null){
            return;
        }

        try{
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
    public void actionPerformed(ActionEvent e){
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


    class DatePickerDialog extends JDialog implements ActionListener, MouseListener {

        private LocalDate chosenDate;
        private JLabel lblHint;
        private JList dateList;
        private JScrollPane scroll;
        private JButton btnOk, btnCancel;
        private DefaultListModel listModel;

        public DatePickerDialog(JFrame parent, String title, ArrayList<LocalDate> availableDates){
            super(parent, title, true);
            setLayout(null);
            setSize(300, 420);
            setLocationRelativeTo(parent);
            getContentPane().setLayout(null);

            chosenDate = null;

            lblHint = new JLabel("Select a date:");
            lblHint.setBounds(10, 10, 260, 25);
            lblHint.setFont(new Font("Arial", Font.PLAIN, 13));
            add(lblHint);

            listModel = new DefaultListModel();
            for(int i = 0; i < availableDates.size(); i++){
                listModel.addElement(availableDates.get(i));
            }

            dateList = new JList(listModel);
            dateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            dateList.setSelectedIndex(0);
            dateList.setFont(new Font("Arial", Font.PLAIN, 14));
            dateList.setFixedCellHeight(28);
            dateList.addMouseListener(this);

            scroll = new JScrollPane(dateList);
            scroll.setBounds(10, 40, 270, 300);
            add(scroll);

            btnOk = new JButton("OK");
            btnOk.setBounds(100, 350, 80, 28);
            btnOk.addActionListener(this);
            add(btnOk);

            btnCancel = new JButton("Cancel");
            btnCancel.setBounds(190, 350, 85, 28);
            btnCancel.addActionListener(this);
            add(btnCancel);
        }

        public LocalDate getChosenDate(){
            return chosenDate;
        }

        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == btnOk){
                chosenDate = (LocalDate) dateList.getSelectedValue();
                dispose();
            }else if(e.getSource() == btnCancel){
                dispose();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e){
            if(e.getClickCount() == 2){
                chosenDate = (LocalDate) dateList.getSelectedValue();
                dispose();
            }
        }

        @Override
        public void mousePressed(MouseEvent e){
        }

        @Override
        public void mouseReleased(MouseEvent e){
        }

        @Override
        public void mouseEntered(MouseEvent e){
        }

        @Override
        public void mouseExited(MouseEvent e){
        }
    }
}
