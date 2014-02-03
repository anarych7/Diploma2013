package modules.service;

import client.gui.ClientFrame;
import modules.object.Pack;
import modules.object.Task;
import server.ReplicationServer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

public class Filter {
    private final Logger log = Logger.getLogger(Filter.class.getName());
    private LinkedBlockingQueue<Pack> commandQueue;
    private PriorityBlockingQueue<Task> taskQueue;
    private LinkedBlockingQueue<Pack> dataQueue;

    public Filter(String runSide) {
        if ("server".equals(runSide)) {
            commandQueue = ReplicationServer.getCommandQueue();
            taskQueue = ReplicationServer.getTaskQueue();
        }
        if ("client".equals(runSide)) {
            commandQueue = ClientFrame.getCommandQueue();
            dataQueue = ClientFrame.getDataQueue();
        }
    }

    //Метод фильтрации пакетов
    public void toFilter(Pack pack) {
        try {
            log.info("Start");
            if (pack.getNetObject().command != null) {

                //Распределение пакетов по очередям
                switch (pack.getNetObject().commandType) {
                    case SYSTEM:
                    case ADD_TASK:
                    case DEL_TASK:
                    case SHOW_TASK:
                        log.info("to commandQueue");
                        commandQueue.put(pack);
                        break;
                    case TASK:
                        log.info("to taskQueue");
                        taskQueue.put((Task) pack.getNetObject().command);
                        break;
                    case DATA:
                        log.info("to dataQueue");
                        dataQueue.put(pack);
                        break;
                }
            }
            log.info("Stop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}