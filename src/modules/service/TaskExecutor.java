package modules.service;

import modules.command.Command;
import modules.command.TaskCommand;
import modules.object.NetObject;
import modules.object.Task;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

public class TaskExecutor implements Runnable {
    private final Logger log = Logger.getLogger(TaskExecutor.class.getName());
    private final PriorityBlockingQueue<Task> taskQueue;
    private String PATH_TO_TASK_LIST = "taskList.xml";

    public TaskExecutor(PriorityBlockingQueue<Task> taskQueue) {
        this.taskQueue = taskQueue;
    }

    //Работа сервиса исполнения задач
    @Override
    public synchronized void run() {
        try {
            log.info("Thread is run...");
            Task task;
            Command command = null;
            XMLParser xmlParser = new XMLParser();
            while (!Thread.currentThread().isInterrupted()) {
                if (!taskQueue.isEmpty()) {
                    if (Calendar.getInstance().getTime().compareTo(taskQueue.peek().dateTaskExecute) == 0) {
                        task = taskQueue.take();
                        Task oldTask = task;
                        Task newTask = task;
                        command = new TaskCommand(task);
                        log.info("Task " + task.taskType.toString() + " is running...");
                        NetObject netObject = command.execute();
                        log.info("Task " + task.taskType.toString() + " is stopped.");

                        //Определене нового времени исполнения задачи
                        switch (task.periodType) {
                            case ONCE:
                                break;
                            case EVERY_DAY:
                                newTask.dateTaskExecute.setTime(newTask.dateTaskExecute.getTime() + 24 * 60 * 60 * 1000);
                                taskQueue.put(newTask);
                                xmlParser.updateTaskInTaskList(new File(PATH_TO_TASK_LIST), oldTask, newTask);
                                break;
                            case EVERY_WEEK:
                                newTask.dateTaskExecute.setTime(newTask.dateTaskExecute.getTime() + 7 * 24 * 60 * 60 * 1000);
                                taskQueue.put(newTask);
                                xmlParser.updateTaskInTaskList(new File(PATH_TO_TASK_LIST), oldTask, newTask);
                                break;
                            case EVERY_MONTH:
                                newTask.dateTaskExecute.setTime(newTask.dateTaskExecute.getTime() + 30 * 24 * 60 * 60 * 1000);
                                taskQueue.put(newTask);
                                xmlParser.updateTaskInTaskList(new File(PATH_TO_TASK_LIST), oldTask, newTask);
                                break;
                        }
                    }
                }
            }
            log.info("Thread is stopped.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.info("Thread is stopped.");
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
