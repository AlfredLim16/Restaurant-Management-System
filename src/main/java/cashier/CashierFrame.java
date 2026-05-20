package cashier;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;
import user.IPermission;
import user.User;

public class CashierFrame extends JFrame implements ActionListener {

    private OrderService orderService;
    private PaymentService paymentService;
    private final OrdersPanel panelOrders;
    private final PaymentsPanel panelPayments;
    private JButton btnOrders, btnPayments, btnLogout;

    public CashierFrame(User user, IPermission perm){
        setTitle("Byte Bite - Cashier");
        setLayout(null);
        setSize(935, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        Services();
        Navigation();

        panelOrders = new OrdersPanel(orderService, this);
        panelOrders.setBounds(20, 50, 860, 500);
        add(panelOrders);

        panelPayments = new PaymentsPanel(paymentService, this);
        panelPayments.setBounds(20, 50, 860, 500);
        add(panelPayments);

        showPanel(panelOrders);
    }

    private void Services(){
        IOrder orderData = new InMemoryOrder();
        IPayment paymentData = new InMemoryPayment();
        IMenuItem menuData = new InMemoryMenuItem();
        orderService = new OrderService(orderData, menuData);
        paymentService = new PaymentService(paymentData, orderData);
    }

    private ImageIcon loadIcon(String filename, int size){
        URL path = getClass().getResource("/icons/" + filename);
        if(path == null){
            return null;
        }
        Image img = new ImageIcon(path).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void Navigation(){
        int x = 40, y = 10, width = 150, height = 35, gap = 15;

        btnOrders = new JButton("Orders", loadIcon("order.png", 18));
        btnOrders.setOpaque(true);
        btnOrders.setIconTextGap(12);
        btnOrders.setBounds(x, y, width, height);
        btnOrders.setFocusPainted(false);
        //btnOrders.setBorderPainted(false);
        btnOrders.setContentAreaFilled(false);
        btnOrders.setBackground(Color.WHITE);
        btnOrders.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOrders.setHorizontalAlignment(SwingConstants.LEFT);
        btnOrders.addActionListener(this);
        add(btnOrders);
        x += width + gap;

        btnPayments = new JButton("Payments", loadIcon("payment.png", 18));
        btnPayments.setOpaque(true);
        btnPayments.setIconTextGap(12);
        btnPayments.setBounds(x, y, width, height);
        btnPayments.setFocusPainted(false);
        //btnPayments.setBorderPainted(false);
        btnPayments.setContentAreaFilled(false);
        btnPayments.setBackground(Color.WHITE);
        btnPayments.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPayments.setHorizontalAlignment(SwingConstants.LEFT);
        btnPayments.addActionListener(this);
        add(btnPayments);
        x += width + gap;

        btnLogout = new JButton("Logout", loadIcon("log-out.png", 18));
        btnLogout.setOpaque(true);
        btnLogout.setIconTextGap(12);
        btnLogout.setBounds(x, y, width, height);
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
        panelOrders.setVisible(false);
        panelPayments.setVisible(false);
        panel.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnOrders){
            showPanel(panelOrders);
            panelOrders.refreshOrders();
        }else if(e.getSource() == btnPayments){
            showPanel(panelPayments);
            panelPayments.refreshPayments();
        }else if(e.getSource() == btnLogout){
            System.exit(0);
        }
    }

}
