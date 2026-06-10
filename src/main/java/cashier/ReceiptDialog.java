package cashier;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ReceiptDialog extends JDialog implements ActionListener {

    private JLabel lblTitle, lblOrderId, lblDate, lblTable, lblItems, lblAmount, lblTip, lblTotal, lblMethod;
    private JLabel vOrderId, vDate, vTable, vAmount, vTip, vTotal, vMethod;
    private JTextArea txtItems;
    private JScrollPane scrollItems;
    private JSeparator separator1, separator2;
    private JButton btnClose;

    public ReceiptDialog(JFrame parent, int orderId, String table, String items, String amount, double tip, String method){
        super(parent, "Receipt", true);
        setLayout(null);
        setSize(360, 460);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        double amountValue = 0.0;
        try{
            amountValue = Double.parseDouble(amount);
        }catch(NumberFormatException ex){
            amountValue = 0.0;
        }
        double totalValue = amountValue + tip;

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        lblTitle = new JLabel("RECEIPT", SwingConstants.CENTER);
        lblTitle.setBounds(10, 15, 320, 28);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        separator1 = new JSeparator();
        separator1.setBounds(20, 50, 300, 1);
        separator1.setForeground(Color.GRAY);
        add(separator1);

        lblOrderId = new JLabel("Order ID:");
        lblOrderId.setBounds(20, 60, 130, 25);
        add(lblOrderId);
        vOrderId = new JLabel(String.valueOf(orderId));
        vOrderId.setBounds(160, 60, 170, 25);
        add(vOrderId);

        lblDate = new JLabel("Date:");
        lblDate.setBounds(20, 90, 130, 25);
        add(lblDate);
        vDate = new JLabel(dateStr);
        vDate.setBounds(160, 90, 170, 25);
        add(vDate);

        lblTable = new JLabel("Table:");
        lblTable.setBounds(20, 120, 130, 25);
        add(lblTable);
        vTable = new JLabel(table);
        vTable.setBounds(160, 120, 170, 25);
        add(vTable);

        lblItems = new JLabel("Items:");
        lblItems.setBounds(20, 150, 130, 25);
        add(lblItems);

        txtItems = new JTextArea(items.replace(", ", "\n"));
        txtItems.setEditable(false);
        txtItems.setFont(new Font("Arial", Font.PLAIN, 13));
        txtItems.setBackground(Color.WHITE);

        scrollItems = new JScrollPane(txtItems);
        scrollItems.setBounds(160, 150, 160, 60);
        add(scrollItems);

        separator2 = new JSeparator();
        separator2.setBounds(20, 220, 300, 1);
        separator2.setForeground(Color.GRAY);
        add(separator2);

        lblAmount = new JLabel("Subtotal:");
        lblAmount.setBounds(20, 230, 130, 25);
        add(lblAmount);
        vAmount = new JLabel(amount);
        vAmount.setBounds(160, 230, 170, 25);
        add(vAmount);

        lblTip = new JLabel("Tip:");
        lblTip.setBounds(20, 260, 130, 25);
        add(lblTip);
        vTip = new JLabel(String.format("%.2f", tip));
        vTip.setBounds(160, 260, 170, 25);
        add(vTip);

        lblTotal = new JLabel("TOTAL:");
        lblTotal.setBounds(20, 290, 130, 25);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblTotal);
        vTotal = new JLabel(String.format("%.2f", totalValue));
        vTotal.setBounds(160, 290, 170, 25);
        vTotal.setFont(new Font("Arial", Font.BOLD, 14));
        add(vTotal);

        lblMethod = new JLabel("Payment Method:");
        lblMethod.setBounds(20, 320, 130, 25);
        add(lblMethod);
        vMethod = new JLabel(method);
        vMethod.setBounds(160, 320, 170, 25);
        add(vMethod);

        btnClose = new JButton("Close");
        btnClose.setBounds(120, 380, 110, 30);
        btnClose.addActionListener(this);
        add(btnClose);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnClose){
            dispose();
        }
    }
}
