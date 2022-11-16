package net.sourceforge.ganttproject.task;

import java.util.ArrayList;
import java.util.List;

public class TaskObjectiveCollectionImpl implements TaskObjectiveCollection{



    private final List<TaskObjective> myObjectives;

    private final Task myTask;

    private int totalPercentage;

    public TaskObjectiveCollectionImpl(Task myTask) {
        myObjectives = new ArrayList<>();
        this.myTask = myTask;
    }

    @Override
    public TaskObjective[] getObjectives() {
        return (TaskObjective[]) myObjectives.toArray();
    }

    public List<TaskObjective> getObjectivesList() {
        return myObjectives;
    }

    @Override
    public void add(TaskObjective objective) {
        myObjectives.add(objective);
        totalPercentage += objective.getPercentage();
    }


    @Override
    public Task getTask() {
        return myTask;
    }

    public int size() {
        return myObjectives.size();
    }

    @Override
    public TaskObjective get(int index){ // index == row-1
        return myObjectives.get(index);
    }
    @Override
    public void remove(TaskObjective obj){// index == row-1
        totalPercentage-= obj.getPercentage();
        myObjectives.remove(obj);
    }

    @Override
    public void removeIndex(int index){// index == row-1
        myObjectives.remove(index);
    }

    @Override
    public void removeAll(List<TaskObjective> selected) {
        myObjectives.removeAll(selected);
    }

    @Override
    public int getTotalPercentage(){
        return totalPercentage;
    }

    @Override
    public boolean reachedTheMaximum(){
        return totalPercentage == 100;
    }
}
