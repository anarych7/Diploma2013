package server;

import modules.connector.ServerConnector;
import modules.object.Pack;
import modules.object.Task;
import modules.service.Executor;
import modules.service.TaskExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ReplicationServer {
    private static final String WELCOME_MESSAGE =
            "##############################################\n" +
                    "# WELCOME TO REPLICATION SERVER              #\n" +
                    "# THIS PROGRAM WAS CREATED AS DIPLOMA PROJECT#\n" +
                    "# AUTHOR: Adushkin Aleksandr Yur'evich       #\n" +
                    "# YEAR: 2013                                 #\n" +
                    "##############################################\n";
    private static ServerConnector serverConnector;
    private static LinkedBlockingQueue<Pack> commandQueue;
    private static PriorityBlockingQueue<Task> taskQueue;
    private static Executor executor;
    private static TaskExecutor taskExecutor;
    private static Thread executorServerThread;
    private static Thread taskExecutorServerThread;

    public static LinkedBlockingQueue<Pack> getCommandQueue() {
        return commandQueue;
    }

    public static PriorityBlockingQueue<Task> getTaskQueue() {
        return taskQueue;
    }

    private static void init() {
        commandQueue = new LinkedBlockingQueue<Pack>();
        taskQueue = new PriorityBlockingQueue<Task>();
        executor = new Executor(commandQueue);
        taskExecutor = new TaskExecutor(taskQueue);
        executorServerThread = new Thread(executor, "executorServerThread");
        taskExecutorServerThread = new Thread(taskExecutor, "taskExecutorServerThread");
    }

    //ToDo Welcome message(in production)
    public static void main(String[] args) {
        System.out.println(WELCOME_MESSAGE);
        try {
            init();
            executorServerThread.start();
            taskExecutorServerThread.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String commandFromUser;
            int port;
            Thread.sleep(1000);
            while (true) {
                commandFromUser = bufferedReader.readLine();
                if ("on".equals(commandFromUser)) {
                    System.out.print("port:");
                    commandFromUser = bufferedReader.readLine();
                    if (checkString(commandFromUser)) {
                        port = Integer.parseInt(commandFromUser);
                        serverConnector = new ServerConnector(port);
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                serverConnector.switchOn();
                            }
                        };
                        new Thread(run).start();
                        Thread.sleep(1000);
                    }
                }
                if ("off".equals(commandFromUser)) {
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            serverConnector.switchOff();
                        }
                    };
                    new Thread(run).start();
                }
                if ("exit".equals(commandFromUser)) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkString(String string) {
        if (string == null || string.length() == 0) return false;
        int i = 0;
        if (string.charAt(0) == '-') {
            if (string.length() == 1) {
                return false;
            }
            i = 1;
        }
        char c;
        for (; i < string.length(); i++) {
            c = string.charAt(i);
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }
}