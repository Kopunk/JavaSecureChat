import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

class ConnectionManager implements Runnable {
    Server server;
    Client client;

    int port;
    String nickname;
    JEditorPane chatArea;
    JTextField textField;
    String password;

    ConnectionManager(int port, String nickname, String password, JEditorPane messagesArea, HTMLEditorKit htmlEditor,
            JTextField textField) {
        this.port = port;
        this.nickname = nickname;
        this.chatArea = messagesArea;
        this.textField = textField;
        this.password = password;

        Thread serverThread = new Thread(this);

        try {
            client = new Client(port, "test");
        } catch (ConnectException e) {
            try {
                serverThread.start();
                TimeUnit.SECONDS.sleep(1);
                client = new Client(port, "test");

            } catch (IOException | InterruptedException e1) {
                System.err.println("ERROR: " + e);
                e.printStackTrace();
                System.exit(1);
            }

        } catch (IOException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
            System.exit(1);
        }
        new ChatRefresh(this);

        try {
            TimeUnit.SECONDS.sleep(1);
            this.sendMessage(nickname + " has joined the server", true, Message.Code.SERVER);
        } catch (InterruptedException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
            System.exit(1);
        }

    }

    void disconnect() {
        if (client == null)
            return;
        try {
            this.sendMessage(nickname + " has left the server", true, Message.Code.SERVER);

            client.closeConnection();
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        client = null;

        if (server != null) {
            server.closeServer();
        }
        server = null;
    }

    void sendMessage(String text, boolean sendAsServer, Message.Code code) {
        if (client == null)
            return;

        Message message = new Message(nickname, text);
        if (sendAsServer) {
            message = new Message("Server", text, code);
        }

        message.encryptMessage(password);

        try {
            client.sendMessage(message);
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    void sendMessage(String text) {
        sendMessage(text, false, Message.Code.MSG);
    }

    void receiveMessage() {
        if (client == null)
            return;

        Message message = client.getNextMessage();
        if (message == null)
            return;

        message.decryptMessage(password);

        appendMessage(message);
    }

    String formatMessage(Message message) {
        String htmlText = "<hr>";
        htmlText += "<b>" + message.author + "</b> <font color='gray'> at " + message.timeOfCreation + "</font>";
        htmlText += "<br>" + message.text;
        htmlText += "<br>";
        return htmlText;
    }

    void appendMessage(Message message) {
        String text = formatMessage(message);

        HTMLEditorKit htmlEditor = new HTMLEditorKit();
        HTMLDocument document = (HTMLDocument) chatArea.getDocument();

        try {
            htmlEditor.insertHTML(document, document.getLength(), text, 0, 0, null);
        } catch (BadLocationException | IOException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
        }

        chatArea.setDocument(document);
    }

    @Override
    public void run() {
        try {
            server = new Server(port);
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
            System.exit(1);
        }

    }

    class ChatRefresh implements Runnable {
        ConnectionManager connectionManager;

        ChatRefresh(ConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
            Thread chatRefreshThread = new Thread(this);
            chatRefreshThread.start();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    System.err.println("ERROR: " + e);
                    e.printStackTrace();
                }
                connectionManager.receiveMessage();
            }
        }
    }
}
