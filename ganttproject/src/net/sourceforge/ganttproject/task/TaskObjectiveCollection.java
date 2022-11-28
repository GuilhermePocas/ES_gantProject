package net.sourceforge.ganttproject.task;

import net.sourceforge.ganttproject.resource.HumanResource;

import java.util.List;

public interface TaskObjectiveCollection {

    TaskObjective[] getObjectives();

    List<TaskObjective> getObjectivesList();

    void add(TaskObjective obj);

    int size();

    TaskObjective get(int index);

    void remove(TaskObjective obj);

    void removeIndex(int index);

    void removeAll(List<TaskObjective> selected);

    int getTotalPercentage();

    boolean reachedTheMaximum();

    int getCheckedPercentage();

    int getLeftOver();

    void copy(TaskObjectiveCollection list);

}
