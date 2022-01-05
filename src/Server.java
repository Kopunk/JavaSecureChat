import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server extends Logger {
    final ServerSocket serverSocket;

    LinkedList<Message> messageQueue;
    LinkedList<Connection> clientList;

    Broadcaster broadcaster;

    Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        messageQueue = new LinkedList<Message>();
        clientList = new LinkedList<Connection>();

        // start broadcaster
        broadcaster = new Broadcaster();

        // incoming connections loop
        do {
            log("waiting for new clients");

            Socket clientSocket = serverSocket.accept(); // blocks
            clientList.addLast(new Connection(clientSocket));

        } while (!clientList.isEmpty());

        log("closing server socket");
        serverSocket.close();
        log("server socket closed");
    }

    class Connection implements Runnable {
        Socket clientSocket;

        InputStream inputStream;
        DataInputStream dataInputStream;
        DataOutputStream dataOutputStream;
        ObjectInputStream objectInputStream;
        ObjectOutputStream objectOutputStream;

        Message inputMessage;

        Connection(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            Thread connectionThread = new Thread(this);
            connectionThread.start();
        }

        @Override
        public void run() {
            try {
                inputStream = clientSocket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);

                do {
                    log("reading messages from: " + clientSocket);
                    objectInputStream = new ObjectInputStream(dataInputStream);
                    inputMessage = (Message) objectInputStream.readObject();

                    if (inputMessage.code == Message.Code.MSG) {
                        messageQueue.addLast(inputMessage);
                    }

                } while (inputMessage.code != Message.Code.EXIT);

                // messageQueue.addLast(new Message(clientSocket + " left"));

                objectOutputStream.close();
                dataOutputStream.close();
                objectInputStream.close();
                dataInputStream.close();
                clientSocket.close();

            } catch (IOException | ClassNotFoundException e) {
                if (e.getClass().equals(EOFException.class)) {
                    log("client disconnected");
                } else {
                    log("ERROR: IO Exception: " + e);
                }

            } finally {
                clientList.remove(this);
            }

            // if (clientList.isEmpty()) {
            // try {
            // serverSocket.close();
            // } catch (Exception e) {
            // log("close server");
            // }
            // }

        }

        // ObjectOutputStream getObjectOutputStream() {
        //     return objectOutputStream;
        // }
    }

    class Broadcaster implements Runnable {
        Thread broadcasterThread;

        Broadcaster() {
            broadcasterThread = new Thread(this);
            broadcasterThread.start();
        }

        @Override
        public void run() {
            while (messageQueue.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log("ERROR: InterruptedException: " + e);
                }
            }
            while (!clientList.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {
                    log("ERROR: InterruptedException: " + e1);
                }

                while (!messageQueue.isEmpty()) {

                    log("broadcasting message: " + messageQueue.getFirst().toString());
                    for (Connection client : clientList) {
                        try {
                            client.objectOutputStream.writeObject(messageQueue.getFirst());
                            client.objectOutputStream.flush();

                            // client.getDataOutputStream().writeUTF(messageQueue.getFirst().toString());
                            // client.getDataOutputStream().flush();
                        } catch (IOException e) {
                            log("ERROR: IO Exception: " + e);
                        }
                    }
                    messageQueue.removeFirst();
                }
            }
        }

    }
}
