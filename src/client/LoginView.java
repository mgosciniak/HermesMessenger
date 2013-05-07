package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.UnknownHostException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import exceptions.InvalidUsernameException;

public class LoginView extends JFrame{
    private final JTextField ipAddress;
    private final JTextField portNumber;
    private final JTextField username;
    private final JComboBox colorDropDown;
    private final JButton submitButton;
    private final JLabel hermes;
    private final JLabel messenger;

    public LoginView() {
        setTitle("Hermes Messenger Login");
        setBackground(new Color(96, 80, 220));
        setPreferredSize(new Dimension(600,400));
        
        //HERMES
        hermes = new JLabel("HERMES");
        hermes.setForeground(Color.white);
        hermes.setAlignmentY(CENTER_ALIGNMENT);
        hermes.setFont(new Font("Serif", Font.BOLD, 50));
        messenger = new JLabel("MESSENGER");
        messenger.setForeground(Color.black);
        messenger.setAlignmentY(CENTER_ALIGNMENT);
        messenger.setFont(new Font("SansSerif", Font.PLAIN, 25));

        //IP ADDRESS FIELD
        ipAddress = new JTextField();
        ipAddress.setText("IP Address");
        ipAddress.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {}
            @Override
            public void focusLost(FocusEvent arg0) {
                if (ipAddress.getText().equals("")) {
                    ipAddress.setText("IP Address");
                }
            }
        });
        ipAddress.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (ipAddress.getText().equals("IP Address")) {
                    ipAddress.setText("");                
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });
        
        //PORT NUMBER
        portNumber = new JTextField();
        portNumber.setText("Port");
        portNumber.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {}
            @Override
            public void focusLost(FocusEvent arg0) {
                if (portNumber.getText().equals("")) {
                    portNumber.setText("Port");
                }
            }
        });
        portNumber.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (portNumber.getText().equals("Port")) {
                    portNumber.setText("");                
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });
        
        //USERNAME
        username = new JTextField();
        username.setText("Username");
        username.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {}
            @Override
            public void focusLost(FocusEvent arg0) {
                if (username.getText().equals("")) {
                    username.setText("Username");
                }
            }
        });
        username.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (username.getText().equals("Username")) {
                    username.setText("");
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });
        //COLOR PICKER
        String[] colorStrings = {"red", "orange", "yellow", "green", "blue", "pink"};
        colorDropDown = new JComboBox(colorStrings);
        colorDropDown.setSelectedIndex(4);
        
        //SUBMIT BUTTON
        submitButton = new JButton();
        submitButton.setText("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    if(ChatClient.attemptLogin(ipAddress.getText(), portNumber.getText(), username.getText(), (String) colorDropDown.getSelectedItem())) {
                        exit();
                    }
                    else {
                        JOptionPane.showMessageDialog(getContentPane(), "Username already in use");
                    }
                    //exit();
                } catch (NumberFormatException e1) {
                    //INVALID PORT NUMBER
                    JOptionPane.showMessageDialog(getContentPane(), "Invalid port number");
                    portNumber.setText("Port");
                } catch (UnknownHostException e2) {
                    //INVALID IP
                    JOptionPane.showMessageDialog(getContentPane(), "Invalid IP Address");
                    ipAddress.setText("IP Address");
                } catch (InvalidUsernameException e3) {
                    JOptionPane.showMessageDialog(getContentPane(), "Invalid Username");
                    username.setText("Username");
                } catch (Exception e4) {
                    System.out.println(e4);
                    JOptionPane.showMessageDialog(getContentPane(), e4.getStackTrace());
                    ipAddress.setText("IP Address");
                    portNumber.setText("Port");
                    username.setText("Username");
                }
            }
        });
        
        //GROUP LAYOUT
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        //HORIZONTAL GROUPINGS
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(hermes)
                .addComponent(messenger)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(ipAddress)
                    .addComponent(portNumber)
                    .addComponent(username)
                    .addComponent(colorDropDown)
                    .addComponent(submitButton)));
        //VERTICAL GROUPINGS
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(hermes)
                .addComponent(messenger)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(ipAddress)
                    .addComponent(portNumber)
                    .addComponent(username)
                    .addComponent(colorDropDown)
                    .addComponent(submitButton)));
        getRootPane().setDefaultButton(submitButton);
        pack();
    }
    public void exit() {
        UserGUI.openConversationView();
    }
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginView main = new LoginView();
                main.setVisible(true);
            }
        });
    }
}
