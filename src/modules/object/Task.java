package modules.object;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable, Comparable<Task> {
    public int id;
    public Date dateTaskCreated;
    public Date dateTaskExecute;
    public TaskType taskType;
    public String vmid;
    public String archive;
    public PeriodType periodType;

    public Task(Date dateTaskCreated,
                Date dateTaskExecute,
                TaskType taskType,
                String vmid,
                String archive,
                PeriodType periodType) {
        this.dateTaskCreated = dateTaskCreated;
        this.dateTaskExecute = dateTaskExecute;
        this.taskType = taskType;
        this.vmid = vmid;
        this.archive = archive;
        this.periodType = periodType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Task t) {
        return dateTaskExecute.compareTo(t.dateTaskExecute);
    }

    //Типы задач
    public enum TaskType {
        COMMON, VZDUMP, VZRESTORE
    }

    //Типы периодичности
    public enum PeriodType {
        ONCE, EVERY_DAY, EVERY_WEEK, EVERY_MONTH
    }
}






