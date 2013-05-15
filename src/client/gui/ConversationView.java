package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import client.user.Conversation;
import client.user.Message;
import client.user.User;
import client.user.UserInfo;
import exceptions.DuplicateConvoException;

public class ConversationView extends JPanel{
    private static final long serialVersionUID = 1L;
    private static JFrame frame;
    private static JTabbedPane tabby;
    private static JMenuBar menuBar;
    private final JMenu file;
    private final JMenuItem logout;
    private final JMenuItem closeConvo;
    private static DefaultListModel listModel;
    private static JList list;
    private static ConcurrentHashMap<String, TabPanel> tabMap = new ConcurrentHashMap<String, TabPanel>();
    public final static ConcurrentHashMap<String, Color> colorMap = new ConcurrentHashMap<String, Color>();
    public final User user;
    public final UserGUI gui;
    
    public ConversationView(final User user, final UserGUI gui) {
        super(new GridLayout(1, 1));
        this.user = user;
        this.gui = gui;

        //Make color map
        colorMap.put("red", Color.red);
        colorMap.put("orange", Color.orange);
        colorMap.put("yellow", Color.yellow);
        colorMap.put("green", Color.green);
        colorMap.put("blue", Color.blue);
        colorMap.put("pink", Color.pink);
        
        //CREATE WINDOW
        setName("Hermes Messenger");
        setPreferredSize(new Dimension(600,400));
        
        //MENU
        menuBar = new JMenuBar();
        menuBar.setBackground(colorMap.get(user.getColor()));
        file = new JMenu(user.getUsername());
        file.setBackground(colorMap.get(user.getColor()));
        file.addMenuListener(new MenuListener() {
            public void menuCanceled(MenuEvent arg0) {}
            @Override
            public void menuDeselected(MenuEvent arg0) {}
            @Override
            public void menuSelected(MenuEvent arg0) {
                if (tabby.getSelectedIndex() == 0) {
                    closeConvo.setVisible(false);
                } else {
                    closeConvo.setVisible(true);
                }
            }
        });
        menuBar.add(file);
        //LOGOUT BUTTON
        logout = new JMenuItem("Logout");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                user.quit();
            }
        });
        file.add(logout);
        //CLOSE CONVO BUTTON
        closeConvo = new JMenuItem("Close Conversation");
        closeConvo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                user.closeConvo(((TabPanel) tabby.getSelectedComponent()).getConvo());
            }
        });
        file.add(closeConvo);
        
        //TABBY
        tabby = new JTabbedPane();
        tabby.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg0) {
                tabby.setBackgroundAt(tabby.getSelectedIndex(), null);
            }
            @Override
            public void focusLost(FocusEvent arg0) {}
        });
        //TAB AND PANEL FOR CREATING NEW CONVOS     
        JComponent newPanel = newConvoPanel();
        tabby.addTab("New", newPanel);       
        add(tabby);
        tabby.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
      //Turn off metal's use of bold fonts
//        UIManager.put("swing.boldMetal", Boolean.FALSE);
//        System.out.println("before create");
//        createAndShowGUI(user, gui);
//        System.out.println("after create");
    }
    
    /**
     * Parses convoID to remove own name for display on tab
     * @param convoID
     * @return new name
     */
    public String parseConvoID(String convoID) {
        StringBuilder sb = new StringBuilder();
        for (String un:convoID.split(" ")) {
            if (!un.equals(user.getUsername())) {
                sb.append(un);
                sb.append(" ");
            }
        } return sb.toString();
    }
    
    /**
     * creates panel for starting new conversations
     * @return the new conversation panel
     */
    private JComponent newConvoPanel() {
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        updateOnlineUsers();
        JScrollPane listScroller = new JScrollPane(list);        
        //START CONVERSATION BUTTON
        JButton startButton = new JButton();
        startButton.setText("Start Conversation");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Object[] usernames = list.getSelectedValues();
                try {
                    user.startConvo(usernames);
                    tabby.setSelectedIndex(tabby.getTabCount()-1);
                } catch (DuplicateConvoException e) {
                    JOptionPane.showMessageDialog(getRootPane(), "Conversation already exists");
                }
            }
        });
        //VIEW HISTORY CONVERSATION BUTTON
        JButton historyButton = new JButton();
        historyButton.setText("View Chat History");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Object[] usernames = list.getSelectedValues();
                ConcurrentHashMap<String, UserInfo> participants = new ConcurrentHashMap<String, UserInfo>();
                for (Object un: usernames) {
                    participants.put((String) un, User.getOnlineUsers().get(un));
                }
                participants.put(user.getUsername(), new UserInfo(user.getUsername(), user.getColor()));
                Conversation convo = new Conversation(participants);
                if (User.getInactiveConvos().keySet().contains(convo.getConvoID())) {
                    //SHOW CONVERSATION HISTORY
                    //Create and set up the window.
                    frame = new JFrame(parseConvoID(convo.getConvoID()) + "Chat History");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setPreferredSize(new Dimension(400, 300));
                    
                    //CREATE JLIST CONTAINING HISTORY
                    DefaultListModel historyModel = new DefaultListModel();
                    JList history = new JList(historyModel);
                    history.setLayoutOrientation(JList.VERTICAL);
                    JScrollPane historyScroll = new JScrollPane(history);
                    for (Message msg : User.getInactiveConvos().get(convo.getConvoID()).getMessages()) {
                        historyModel.addElement(msg.getSender().getUsername() + ": " + msg.getText());
                    }
                    //Add content to the window.
                    frame.add(historyScroll, BorderLayout.CENTER);
                    
                    //Display the window.
                    frame.pack();
                    frame.setVisible(true);
                } else if (User.getMyConvos().keySet().contains(convo.getConvoID())) {
                    JOptionPane.showMessageDialog(getRootPane(), "Conversation is currently active");
                } else {
                    JOptionPane.showMessageDialog(getRootPane(), "No chat history to display");
                }
            }
        });
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(startButton, BorderLayout.LINE_START);
        buttonPanel.add(historyButton, BorderLayout.LINE_END);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(listScroller, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.PAGE_END);
        return panel;
    }
    
    /**
     * Updates panel containing all online users
     */
    public static void updateOnlineUsers() {
        listModel.removeAllElements();
        ConcurrentHashMap<String, UserInfo> users = User.getOnlineUsers();
        for (String un: users.keySet()) {
            listModel.addElement(un);
        }
    }
    
    /**
     * Adds tabs for each conversation if they don't already exist
     */
    public void updateTabs() {
        for (Conversation convo : User.getMyConvos().values()) {
            if (!tabMap.keySet().contains(convo.getConvoID())) {
                String convoID = convo.getConvoID();
                TabPanel panel = new TabPanel(convo, user);
                tabby.addTab(this.parseConvoID(convoID), panel);
                JLabel cid = new JLabel(this.parseConvoID(convoID));
                cid.setName(convoID);
                tabby.setTabComponentAt(tabby.getTabCount()-1, cid);
                //tabby.setBackgroundAt(tabby.getTabCount()-1, panel.getColor());
                tabMap.put(convoID, panel);
            }
        }
    }
    
    /**
     * Closes tab for a conversation that was closed
     */
    public static void removeTab(String convoID) {
        tabMap.remove(convoID);
        for (int i=tabby.getTabCount()-1; i > 0; i--) {
           if (((TabPanel)tabby.getComponentAt(i)).getConvo().getConvoID().equals(convoID)) {
               tabby.remove(i);
           }
        }
    }
    
    /**
     * updates the conversation in the tab containing conversation given by convoID
     * @param convoID the conversation to be updated
     */
    public static void updateTab(String convoID) {
         tabMap.get(convoID).showMessage();
         for (int i=tabby.getTabCount()-1; i > 0; i--) {
             //finds the appropriate tab that corresponds to the convoID
             if (((TabPanel)tabby.getComponentAt(i)).getConvo().getConvoID().equals(convoID) && tabby.getSelectedIndex() != i) {
                 //finds the color of the most recent message sender, and sets the
                 //tab background to be that color
                 tabby.setBackgroundAt(i, colorMap.get(User.getMyConvos().get(convoID).getMessages().get(User.getMyConvos().get(convoID).getMessages().size()-1).getSender().getColor()));
             }
         }
    }
    
    /**
     * fills the conversation history in the tab containing conversation given by convoID
     * @param convoID the conversation to be updated
     */
    public static void fillHistory(String convoID) {
         tabMap.get(convoID).fillHistory();
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI(User user, UserGUI gui) {
        //Create and set up the window.
        frame = new JFrame("Hermes Messenger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add content to the window.
        ConversationView convoView = new ConversationView(user, gui);
        gui.setConvoView(convoView);
        user.setConversationView(convoView);
        frame.add(convoView, BorderLayout.CENTER);
        frame.setJMenuBar(menuBar);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
     
    public static void main(final User user1, final UserGUI gui1) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI(user1, gui1);
            }
        });
    }
    
    /**
     * closes the frame
     */
    public static void close() {
        frame.setVisible(false);
        frame.dispose();
    }
}