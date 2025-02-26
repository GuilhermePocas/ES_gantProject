package net.sourceforge.ganttproject.task;

import net.sourceforge.ganttproject.GPLogger;
import net.sourceforge.ganttproject.task.dependency.*;

import java.util.*;

public class TaskObjectiveCollectionImpl implements TaskObjectiveCollection{



    private final List<TaskObjective> myObjectives;

    private static int MAX = 100;

    public TaskObjectiveCollectionImpl() {
        myObjectives = new ArrayList<>();
    }

    public TaskObjectiveCollectionImpl(TaskObjectiveCollection taskCol) {
        myObjectives = new ArrayList<>();
        this.copy(taskCol);
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
        if(getTotalPercentage() != MAX) {
            int newPercentage = Math.min(getLeftOver(), objective.getPercentage());
            objective.setPercentage(newPercentage);
            myObjectives.add(objective);
        }
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
    public boolean reachedTheMaximum(){
        return getTotalPercentage() >= MAX;
    }

    public int getTotalPercentage() {
        int TotalPercentage = 0;
        for(TaskObjective obj : myObjectives) {
            TotalPercentage+=obj.getPercentage();
        }
        return TotalPercentage;
    }

    public int getCheckedPercentage() {
        int checkedPercentage = 0;
        for(TaskObjective obj : myObjectives) {
            if(obj.isChecked())
                checkedPercentage+=obj.getPercentage();
        }
        return checkedPercentage;
    }

    public int getLeftOver() {
        return MAX - getTotalPercentage();
    }

    public void copy(TaskObjectiveCollection list) {
        myObjectives.clear();
        if(list != null) {
            for(TaskObjective obj: list.getObjectivesList()) {
                int id = obj.getId();
                String name = obj.getName();
                int percentage = obj.getPercentage();
                boolean check = obj.isChecked();
                TaskObjective newObj = new TaskObjectiveImpl(id, name, percentage, check);

                myObjectives.add(newObj);
            }
        }
    }
}
