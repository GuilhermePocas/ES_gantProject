package net.sourceforge.ganttproject.task;

import net.sourceforge.ganttproject.resource.HumanResource;

import java.util.List;

public interface TaskObjectiveCollection {

    TaskObjective[] getObjectives();

    void addAssignment(TaskObjective resource);

    void deleteAssignment(int id);

    Task getTask();

    int size();

    TaskObjective get(int index);

    void remove(int index);

    void removeAll(List<TaskObjective> selected);
}
