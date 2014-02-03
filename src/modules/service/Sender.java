package modules.service;

import modules.object.NetObject;

import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class Sender {
    private final Logger log = Logger.getLogger(Sender.class.getName());

    //Метод отправки данных через исходящий поток
    public void send(ObjectOutputStream objectOutputStream, NetObject netObject) {
        try {
            objectOutputStream.writeObject(netObject);
            log.info("object was send");
        } catch (Exception e) {
            log.info("something wrong");
        }
    }

    //Maybe one day I'll create separate thread for Sender :)
    /*@Override
    public void run() {
        try {
            MyLogger myLogger = new MyLogger();
            myLogger.toLog(moduleName, "Thread is run...");
            NetObject netObject;
            while (!Thread.currentThread().isInterrupted()) {
                netObject = commandToSendQueue.take();
                System.out.println(netObject.data.toString());
                objectOutputStream.writeObject(netObject);
            }
            objectOutputStream.close();
            myLogger.toLog(moduleName, "Thread is stopped.");
        } catch (InterruptedException e) {
            try {
                objectOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
