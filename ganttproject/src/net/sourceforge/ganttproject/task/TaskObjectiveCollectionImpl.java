package net.sourceforge.ganttproject.task;

import net.sourceforge.ganttproject.resource.HumanResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskObjectiveCollectionImpl implements TaskObjectiveCollection{

    public class Objective implements TaskObjective {
        private int id;
        private String name;
        private int percentage;
        private boolean checked;

        public Objective(int id, String name, int percentage) {
            this.id = id;
            this.name = name;
            this.percentage = percentage;
            this.checked = false;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public int getPercentage() {
            return percentage;
        }

        public boolean isChecked() {
            return checked;
        }

        public void check(boolean value) {
            checked =  value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }
    }

    private final List<TaskObjective> myObjectives;

    private final Task myTask;

    public TaskObjectiveCollectionImpl(Task myTask) {
        myObjectives = new ArrayList<>();
        this.myTask = myTask;
    }

    @Override
    public TaskObjective[] getObjectives() {
        return (TaskObjective[]) myObjectives.toArray();
    }

    @Override
    public void addAssignment(TaskObjective objective) {
        myObjectives.add(objective);
    }

    @Override
    public void deleteAssignment(int id) {
        for(TaskObjective o : myObjectives) {
            if(o.getId() == id)
                myObjectives.remove(o);
        }
    }

    @Override
    public Task getTask() {
        return myTask;
    }

    public int size() {
        return myObjectives.size();
    }
}
