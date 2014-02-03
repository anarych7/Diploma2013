package modules.command;

import modules.object.NetObject;
import modules.object.Pack;
import modules.object.Task;
import modules.service.XMLParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DelTaskCommand implements Command {
    private final Pack pack;
    private Object systemMessage;
    private Object errorMessage;
    private String PATH_TO_TASK_LIST = "taskList.xml";

    public DelTaskCommand(Pack pack) {
        this.pack = pack;
    }

    @Override
    public NetObject execute() {
        try {
            new XMLParser().delTaskFromTaskList(new File(PATH_TO_TASK_LIST), (Task) pack.getNetObject().command);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
