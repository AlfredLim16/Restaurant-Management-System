package manager;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class DatePicker extends JDialog implements ActionListener, MouseListener {

    private LocalDate chosenDate;
    private JLabel lblHint;
    private JList dateList;
    private JScrollPane scroll;
    private JButton btnOk, btnCancel;
    private DefaultListModel listModel;

    public DatePicker(JFrame parent, String title, ArrayList<LocalDate> availableDates){
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
