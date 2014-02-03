package modules.command;

import modules.object.NetObject;
import modules.object.Pack;
import modules.object.Task;
import modules.service.XMLParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ShowTaskCommand implements Command {
    private final Pack pack;
    private Object systemMessage;
    private Object errorMessage;
    private String PATH_TO_TASK_LIST = "taskList.xml";

    public ShowTaskCommand(Pack pack) {
        this.pack = pack;
    }

    @Override
    public NetObject execute() {
        Task[] tasks = new Task[0];
        try {
            tasks = new XMLParser().getTaskList(new File(PATH_TO_TASK_LIST));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : tasks) {
            stringBuilder.append(task.getId()).append("|");
            stringBuilder.append(task.dateTaskCreated).append("|");
            stringBuilder.append(task.dateTaskExecute).append("|");
            stringBuilder.append(task.taskType).append("|");
            stringBuilder.append(task.vmid).append("|");
            stringBuilder.append(task.archive).append("|");
            stringBuilder.append(task.periodType).append("|");
            stringBuilder.append("\n");
        }
        System.out.println(stringBuilder.toString());
        systemMessage = stringBuilder.toString();
        errorMessage = "";
        return new NetObject(NetObject.CommandType.DATA, pack.getNetObject().answerType, pack.getNetObject().command, systemMessage, errorMessage);
    }
}