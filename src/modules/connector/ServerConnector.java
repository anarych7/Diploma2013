package modules.connector;

import modules.service.Receiver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

public class ServerConnector implements Connector {
    private final Logger log = Logger.getLogger(ServerConnector.class.getName());
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Thread receiverThread;
    private final Object sync = new Object();

    public ServerConnector(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    //Включение режима приема соединений
    @Override
    public void switchOn() {
        try {
            synchronized (sync) {
                while (!serverSocket.isClosed()) {
                    log.info("Waiting for new clients...");
                    socket = serverSocket.accept();
                    log.info("Client accepted.");
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    receiverThread = new Thread(new Receiver(objectInputStream, objectOutputStream, "server"));
                    receiverThread.start();
                }
            }
        } catch (SocketException e) {
            log.info("serverSocket is closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Выключение режима приема соединений
    @Override
    public void switchOff() {
        try {
            serverSocket.close();
            synchronized (sync) {
                if (receiverThread != null) {
                    receiverThread.interrupt();
                    socket.close();
                }
            }
        } catch (IOException e) {
            log.info("Can't close socket");
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
