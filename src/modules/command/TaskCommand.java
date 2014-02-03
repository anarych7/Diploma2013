package modules.command;

import modules.object.NetObject;
import modules.object.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TaskCommand implements Command {
    private final Task task;
    private Object systemMessage;
    private Object errorMessage;

    public TaskCommand(Task task) {
        this.task = task;
    }

    @Override
    public NetObject execute() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            switch (task.taskType) {
                case COMMON:
                    break;
                case VZDUMP:
                    stringBuilder.append("vzdump");
                    stringBuilder.append(" ");
                    stringBuilder.append(task.vmid);
                    break;
                case VZRESTORE:
                    stringBuilder.append("vzrestore");
                    stringBuilder.append(" ");
                    stringBuilder.append(task.archive);
                    stringBuilder.append(" ");
                    stringBuilder.append(task.vmid);
                    break;
            }

            String commandString = stringBuilder.toString();
            Process process = Runtime.getRuntime().exec(commandString);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            stringBuilder = new StringBuilder();
            String strTemp;

            strTemp = inputReader.readLine();
            while (strTemp != null) {
                stringBuilder.append(strTemp);
                stringBuilder.append("\n");
                strTemp = inputReader.readLine();
            }
            systemMessage = stringBuilder.toString();

            stringBuilder = new StringBuilder();
            strTemp = errorReader.readLine();
            while (strTemp != null) {
                stringBuilder.append(strTemp);
                stringBuilder.append("\n");
                strTemp = errorReader.readLine();
            }
            errorMessage = stringBuilder.toString();

            inputReader.close();
            errorReader.close();
            process.destroy();
        } catch (Exception e) {
            errorMessage = "Cannot run program";
            e.printStackTrace();
        } finally {
            return new NetObject(NetObject.CommandType.DATA, null, null, systemMessage, errorMessage);
        }
    }
}
