package net.sourceforge.ganttproject.task;

public interface TaskObjective {


    String getName();

    void setName(String name);

    int getId();

    int getPercentage();

    void setPercentage(int percentage);

    boolean isChecked();

    void check(boolean value);


}
