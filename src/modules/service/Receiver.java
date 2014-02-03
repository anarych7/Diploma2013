package modules.service;


import modules.object.NetObject;
import modules.object.Pack;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class Receiver implements Runnable {
    private final Logger log = Logger.getLogger(Receiver.class.getName());
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final String runSide;

    public Receiver(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, String runSide) {
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.runSide = runSide;
    }


    @Override
    public void run() {
        try {
            log.info("Thread is run...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    NetObject netObject = (NetObject) objectInputStream.readObject();
                    if (netObject != null) {
                        new Filter(runSide).toFilter(new Pack(objectOutputStream, netObject));
                    }
                } catch (IOException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        throw e;
                    }
                }
            }
            log.info("Thread is stopped.");
        } catch (IOException e) {
            log.info("Client was disconnected");
        } catch (ClassNotFoundException e) {
            log.info("Class not found");
        }
    }
}
