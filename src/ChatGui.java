import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

class ChatGui extends JFrame {
    ConnectionManager connectionManager;

    JEditorPane messagesArea;
    JTextField textField;
    HTMLEditorKit htmlEditor;

    ChatGui() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(200, 200));

        JMenuBar menuBar = new JMenuBar();
        add(menuBar, BorderLayout.NORTH);
        menuBar.setVisible(true);

        JMenu optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        JMenuItem optionsMenuConnect = new JMenuItem("Connect");
        optionsMenuConnect.addActionListener(new ConnectAction());
        optionsMenu.add(optionsMenuConnect);

        JMenuItem optionsMenuDisconnect = new JMenuItem("Disconnect");
        optionsMenuDisconnect.addActionListener(new DisconnectAction());
        optionsMenu.add(optionsMenuDisconnect);

        JMenuItem optionsMenuExportChat = new JMenuItem("Export Chat");
        optionsMenuExportChat.addActionListener(new ExportChatAction());
        optionsMenu.add(optionsMenuExportChat);

        JMenuItem aboutMenu = new JMenuItem("About");
        aboutMenu.addActionListener(new AboutAction());
        menuBar.add(aboutMenu);

        messagesArea = new JEditorPane("text/html", null);
        htmlEditor = new HTMLEditorKit();
        messagesArea.setEditorKit(htmlEditor);
        JScrollPane messagesAreaScroll = new JScrollPane(messagesArea);
        add(messagesAreaScroll, BorderLayout.CENTER);
        messagesArea.setEditable(false);
        messagesAreaScroll.setVisible(true);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setLayout(new BorderLayout());

        textField = new JTextField();
        bottomPanel.add(textField, BorderLayout.CENTER);
        textField.setMinimumSize(new Dimension(50, 10));
        textField.setVisible(true);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendAction());
        bottomPanel.add(sendButton, BorderLayout.EAST);
        sendButton.setVisible(true);

        setVisible(true);
    }

    void clearChat() {
        messagesArea.setText("");
    }

    class ConnectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int port = 9898;
            String nickname;
            String password;

            // --
            JTextField nicknameField = new JTextField();
            JTextField portField = new JTextField();
            JTextField passwordField = new JPasswordField();
            Object[] fields = {
                    "Port number:", portField,
                    "Nickname:", nicknameField,
                    "Password:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(null, fields, "Connect", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if (portField != null && !portField.getText().isBlank()) {
                    String portStr = portField.getText();
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Error: Please type a numerical value");
                        return;
                    }
                    if (!(port >= 1024 && port < 65535)) {
                        JOptionPane.showMessageDialog(null, "Error: Please select value between 1024 and 65535");
                        return;
                    }
                }
                if (nicknameField == null || nicknameField.getText().isBlank()) {
                    nickname = "Anonymous";
                } else {
                    nickname = nicknameField.getText();
                }
                if (passwordField == null || passwordField.getText().isBlank()) {
                    password = "MySuperAwesomeDefaultPassword";
                } else {
                    password = passwordField.getText();
                }

                // new DisconnectAction().actionPerformed(null);
                connectionManager = new ConnectionManager(port, nickname, password, messagesArea, htmlEditor, textField);
            }
        }
    }

    class DisconnectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (connectionManager != null) {
                connectionManager.disconnect();
            }
        }
    }

    class ExportChatAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO popup window
        }
    }

    class AboutAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Java Secure Chat by Wojciech Kopanski");
        }
    }

    class SendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            connectionManager.sendMessage(textField.getText());
        }
    }
}
