package manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import user.User;
import user.ValidationException;

public class UserManagementPanel extends JPanel implements ActionListener {

    private static final String[] ROLES = {"MANAGER", "CASHIER"};

    private final UserManagementService userManagementService;
    private final JFrame parentFrame;

    private JSeparator separator;
    private JTable tableUsers;
    private JScrollPane scrollUsers;
    private JLabel lblTitle, lblSubTitle;
    private DefaultTableModel modelUsers;
    private DefaultTableCellRenderer centerColumn;
    private JButton btnUserAdd, btnUserEdit, btnUserDelete;

    public UserManagementPanel(UserManagementService userManagementService, JFrame parentFrame) {
        this.userManagementService = userManagementService;
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        Users();
    }

    private void Users() {
        lblTitle = new JLabel("Users");
        lblTitle.setBounds(20, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle);

        lblSubTitle = new JLabel("Manage system user accounts and roles");
        lblSubTitle.setBounds(20, 35, 400, 30);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblSubTitle);

        separator = new JSeparator();
        separator.setBounds(20, 70, 1260, 1);
        separator.setForeground(Color.BLACK);
        add(separator);

        btnUserAdd = new JButton("Add");
        btnUserAdd.setBounds(20, 80, 80, 30);
        btnUserAdd.addActionListener(this);
        add(btnUserAdd);

        btnUserEdit = new JButton("Edit");
        btnUserEdit.setBounds(110, 80, 80, 30);
        btnUserEdit.addActionListener(this);
        add(btnUserEdit);

        btnUserDelete = new JButton("Delete");
        btnUserDelete.setBounds(200, 80, 80, 30);
        btnUserDelete.addActionListener(this);
        add(btnUserDelete);

        modelUsers = new DefaultTableModel(new String[]{"Username", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableUsers = new JTable(modelUsers);
        tableUsers.setRowHeight(28);
        tableUsers.setFillsViewportHeight(true);
        tableUsers.getTableHeader().setReorderingAllowed(false);
        tableUsers.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        tableUsers.getTableHeader().setPreferredSize(new Dimension(tableUsers.getPreferredSize().width, 28));

        centerColumn = new DefaultTableCellRenderer();
        centerColumn.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tableUsers.getColumnCount(); i++) {
            tableUsers.getColumnModel().getColumn(i).setCellRenderer(centerColumn);
        }

        scrollUsers = new JScrollPane(tableUsers);
        scrollUsers.setBounds(20, 120, 1260, 450);
        add(scrollUsers);

        refreshUsers();
    }

    public void refreshUsers() {
        modelUsers.setRowCount(0);
        ArrayList<User> users = userManagementService.getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            modelUsers.addRow(new Object[]{
                user.getUserName(),
                user.getUserRole().name()
            });
        }
    }

    private void addUser() {
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JComboBox comboRole = new JComboBox(ROLES);

        Object[] fields = {
            "Username:", txtUsername,
            "Password:", txtPassword,
            "Role:", comboRole
        };

        int result = JOptionPane.showConfirmDialog(parentFrame, fields, "Add User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) comboRole.getSelectedItem();

        try {
            userManagementService.addUser(username, password, role);
            refreshUsers();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUser() {
        int row = tableUsers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Select a user.");
            return;
        }

        String username = (String) modelUsers.getValueAt(row, 0);
        String currentRole = (String) modelUsers.getValueAt(row, 1);

        JPasswordField txtPassword = new JPasswordField();
        JComboBox comboRole = new JComboBox(ROLES);
        comboRole.setSelectedItem(currentRole);

        Object[] fields = {
            "New Password (leave blank to keep):", txtPassword,
            "Role:", comboRole
        };

        int result = JOptionPane.showConfirmDialog(parentFrame, fields, "Edit User: " + username, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword = new String(txtPassword.getPassword()).trim();
        String newRole = (String) comboRole.getSelectedItem();

        if (newPassword.isEmpty()) {
            ArrayList<User> allUsers = userManagementService.getAllUsers();
            for (int i = 0; i < allUsers.size(); i++) {
                User u = allUsers.get(i);
                if (u.getUserName().equals(username)) {
                    newPassword = u.getUserPassword();
                    break;
                }
            }
        }

        try {
            userManagementService.updateUser(username, newPassword, newRole);
            refreshUsers();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int row = tableUsers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Select a user.");
            return;
        }

        String username = (String) modelUsers.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Delete user '" + username + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userManagementService.deleteUser(username);
            refreshUsers();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUserAdd) {
            addUser();
        } else if (e.getSource() == btnUserEdit) {
            editUser();
        } else if (e.getSource() == btnUserDelete) {
            deleteUser();
        }
    }
}
