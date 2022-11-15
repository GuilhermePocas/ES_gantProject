package net.sourceforge.ganttproject.task;

import net.sourceforge.ganttproject.resource.HumanResource;

public interface TaskObjectiveCollection {

    TaskObjective[] getObjectives();

    void addAssignment(TaskObjective resource);

    void deleteAssignment(int id);

    Task getTask();

    int size();
}
