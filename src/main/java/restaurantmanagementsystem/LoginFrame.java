package restaurantmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import appservice.RestaurantAppService.*;
import dataservice.RestaurantInMemory.InMemoryUser;
import dataservice.RestaurantDataService.IUser;
import model.RestaurantModel.User;
import model.RestaurantModel.Role;

/**
 *
 * @author Tom De Jesus
 */
public class LoginFrame extends JFrame implements ActionListener {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblUser, lblPass, lblError;
    private AuthenticationService authService;
    private PermissionService permService;
    private IUser userData;

    public LoginFrame(){
        setTitle("Byte Bite - Login");
        setLayout(null);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // username
        lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 30, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 55, 300, 30);
        add(txtUsername);

        // password
        lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 95, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 120, 300, 30);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(50, 170, 300, 35);
        btnLogin.addActionListener(this);
        add(btnLogin);

        // error
        lblError = new JLabel("");
        lblError.setBounds(50, 215, 300, 25);
        lblError.setForeground(Color.RED);
        add(lblError);

        userData = new InMemoryUser();
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

                }
                this.dispose();
            }else{
                lblError.setText("Invalid username or password");
                txtPassword.setText("");
            }
        }
    }
}
