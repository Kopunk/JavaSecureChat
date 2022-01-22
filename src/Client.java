import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

class Client extends Logger {
    final Socket clientSocket;
    final String nickname;

    LinkedList<Message> incommingMessageQueue;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    Receiver receiver;
    Message message;

    Client(int port, String nickname) throws UnknownHostException, IOException {
        clientSocket = new Socket("localhost", port);
        this.nickname = nickname;

        incommingMessageQueue = new LinkedList<Message>();

        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

        receiver = new Receiver();
    }

    private void sendMessage(Message message) throws IOException {
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    void sendMessageText(String text) throws IOException {
        sendMessage(new Message(nickname, text));
    }

    void closeConnection() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        clientSocket.close();
    }

    Message getNextMessage() {
        if (!incommingMessageQueue.isEmpty()) {
            return incommingMessageQueue.removeFirst();
        }
        return null;
    }

    class Receiver implements Runnable {

        Receiver() {
            Thread receiverThread = new Thread(this);
            receiverThread.start();
        }

        public void run() {
            while (!clientSocket.isClosed()) {
                try {
                    incommingMessageQueue.addLast((Message) objectInputStream.readObject());
                } catch (ClassNotFoundException | IOException e) {
                    incommingMessageQueue.addLast(new Message(nickname, null, Message.Code.ERR));
                    log("ERROR reading message: " + e);
                    // e.printStackTrace();
                }
            }
        }
    }
}
