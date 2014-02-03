package modules.connector;

import modules.service.Receiver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

public class ClientConnector implements Connector {
    private final Logger log = Logger.getLogger(ClientConnector.class.getName());
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Thread receiverThread;
    private final Object sync = new Object();

    public ClientConnector(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        log.info("Connected");
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    //Включение режима приема соединений
    @Override
    public void switchOn() {
        try {
            synchronized (sync) {
                objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                receiverThread = new Thread(new Receiver(objectInputStream, objectOutputStream, "client"));
                receiverThread.start();
            }
        } catch (SocketException e) {
            log.info("clientSocket is closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Выключение режима приема соединений
    @Override
    public void switchOff() {
        try {
            clientSocket.close();
            synchronized (sync) {
                if (receiverThread != null) {
                    receiverThread.interrupt();
                }
            }
        } catch (IOException e) {
            log.info("Can't close socket");
            e.printStackTrace();
        }
    }
}
