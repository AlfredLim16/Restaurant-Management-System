package user;

import cashier.CashierFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;
import manager.ManagerFrame;

/**
 *
 * @author Tom De Jesus
 */
public class LoginFrame extends JFrame implements ActionListener {

    private final URL logoPath;
    private final IUser userData;
    private final JButton btnLogin;
    private final JCheckBox showPasswordCheck;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final PermissionService permService;
    private final JLabel lblLogo, lblTitle, lblUser, lblPass, lblError;
    private final AuthenticationService authService;

    public LoginFrame(){
        setTitle("Byte Bite - Login");
        setLayout(null);
        setSize(400, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        lblLogo = new JLabel("", SwingConstants.CENTER);
        lblLogo.setBounds(150, 15, 80, 80);
        logoPath = getClass().getResource("/images/logo.jpg");
        if(logoPath != null){
            Image logoImage = new ImageIcon(logoPath).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(logoImage));
        }
        add(lblLogo);

        lblTitle = new JLabel("Byte Bite");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        lblTitle.setBounds(150, 100, 200, 25);
        add(lblTitle);

        // username
        lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 130, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 155, 300, 30);
        add(txtUsername);

        // password
        lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 195, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 220, 300, 30);
        add(txtPassword);

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setBounds(50, 260, 300, 30);
        showPasswordCheck.setBackground(Color.WHITE);
        showPasswordCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordCheck.addActionListener(this);
        add(showPasswordCheck);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(50, 305, 300, 35);
        btnLogin.addActionListener(this);
        add(btnLogin);

        // error
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setBounds(0, 345, 400, 25);
        lblError.setForeground(Color.RED);
        add(lblError);

        userData = new DbUser();
        authService = new AuthenticationService(userData);
        permService = new PermissionService(userData);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnLogin){
            String userName = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            if(userName.isEmpty() || password.isEmpty()){
                lblError.setText("Please enter username and password");
                return;
            }
            User user = authService.login(userName, password);
            if(user != null){
                lblError.setText("");
                if(user.getUserRole() == Role.MANAGER){
                    new ManagerFrame(user, permService).setVisible(true);
                }else{
                    new CashierFrame(user, permService).setVisible(true);
                }
                this.dispose();
            }else{
                lblError.setText("Invalid username or password");
                txtPassword.setText("");
            }
        }

        if(e.getSource() == showPasswordCheck){
            if(showPasswordCheck.isSelected()){
                txtPassword.setEchoChar((char) 0);
            }else{
                txtPassword.setEchoChar('\u2022');
            }
        }
    }
}
