import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestClientServer implements Runnable {
    static int port = 6970;

    TestClientServer() {
        Thread serverThread = new Thread(this);
        serverThread.start();
    }

    public static void main(String[] args) {
        new TestClientServer();

        Client client1, client2, client3;
        try {
            client1 = new Client(6970, "TestClient 1");
            client2 = new Client(6970, "TestClient 2");
            client3 = new Client(6970, "TestClient 3");

            System.out.println("Client 1 sends message: 1");
            client1.sendMessageText("1");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("Client1 receives message: " + client1.getNextMessage());
            System.out.println("Client2 receives message: " + client2.getNextMessage());
            System.out.println("Client3 receives message: " + client3.getNextMessage());

            System.out.println("Client 2 sends message: 2");
            client2.sendMessageText("2");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("Client1 receives message: " + client1.getNextMessage());
            System.out.println("Client2 receives message: " + client2.getNextMessage());
            System.out.println("Client3 receives message: " + client3.getNextMessage());

            System.out.println("Client1 receives message: " + client1.getNextMessage());

        } catch (InterruptedException | IOException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            new Server(port);
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace();
        }
    }
}
