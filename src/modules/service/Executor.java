package modules.service;

import modules.command.*;
import modules.object.NetObject;
import modules.object.Pack;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Executor implements Runnable {
    private final Logger log = Logger.getLogger(Executor.class.getName());
    private final LinkedBlockingQueue<Pack> commandQueue;

    public Executor(LinkedBlockingQueue<Pack> commandQueue) {
        this.commandQueue = commandQueue;
    }

    //Работа сервиса исполнения команд
    @Override
    public void run() {
        try {
            log.info("Thread is run...");
            Pack pack;
            Command command = null;
            NetObject netObject;
            while (!Thread.currentThread().isInterrupted()) {
                pack = commandQueue.take();

                //Определение типа команды в пакете для создания нужной команды
                switch (pack.getNetObject().commandType) {
                    case SYSTEM:
                        command = new SystemCommand(pack);
                        break;
                    case ADD_TASK:
                        command = new AddTaskCommand(pack);
                        break;
                    case DEL_TASK:
                        command = new DelTaskCommand(pack);
                        break;
                    case SHOW_TASK:
                        command = new ShowTaskCommand(pack);
                        break;
                }

                //Исполннение команды
                log.info("Command " + pack.getNetObject().command.toString() + " is executed...");
                netObject = command.execute();
                log.info("Command " + pack.getNetObject().command.toString() + " has been executed.");

                //Отправка данных
                new Sender().send(pack.getObjectOutputStream(), netObject);
            }
            commandQueue.clear();
            log.info("Thread is stopped.");
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            commandQueue.clear();
            log.info("Thread is stopped.");
            Thread.currentThread().interrupt();
        }
    }
}
