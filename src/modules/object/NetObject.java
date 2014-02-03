package modules.object;

import java.io.Serializable;

public class NetObject implements Serializable {
    public final CommandType commandType;
    public final AnswerType answerType;
    public final Object command;
    public final Object systemMessage;
    public final Object errorMessage;

    public NetObject(CommandType commandType,
                     AnswerType answerType,
                     Object command,
                     Object systemMessage,
                     Object errorMessage) {
        this.commandType = commandType;
        this.answerType = answerType;
        this.command = command;
        this.systemMessage = systemMessage;
        this.errorMessage = errorMessage;
    }

    //Типы команд
    public enum CommandType {
        SYSTEM,
        ADD_TASK,
        DEL_TASK,
        SHOW_TASK,
        TASK,
        DATA
    }

    //Типы ответов
    public enum AnswerType {
        GENERAL,
        QM_LIST,
        VZDUMP,
        VZRESTORE,
        DUMP_LIST,
        LIST_FROM_XML,
        TASK_LIST
    }
}



