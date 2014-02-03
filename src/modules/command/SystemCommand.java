package modules.command;

import modules.object.NetObject;
import modules.object.Pack;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemCommand implements Command {
    private final Pack pack;
    private Object systemMessage;
    private Object errorMessage;

    public SystemCommand(Pack pack) {
        this.pack = pack;
    }

    @Override
    public NetObject execute() {
        try {
            Process process = Runtime.getRuntime().exec(pack.getNetObject().command.toString());
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder stringBuilder = new StringBuilder();
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
            return new NetObject(NetObject.CommandType.DATA, pack.getNetObject().answerType, pack.getNetObject().command, systemMessage, errorMessage);
        }
    }
}
