package net.sourceforge.ganttproject.gui.taskproperties;


import com.sun.org.apache.xpath.internal.operations.Bool;
import net.sourceforge.ganttproject.task.*;



import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectivesTableModel extends AbstractTableModel {


    static enum Column {
        ID("id", String.class), NAME("objectivename", String.class),
        PERCENTAGE("percentage", String.class), CHECKED("checked", Boolean.class);

        private final String myName;
        private final Class<?> myClass;

        Column(String key, Class<?> clazz) {
            myName = key;
            myClass = clazz;
        }

        String getName() {
            return myName;
        }

        Class<?> getColumnClass() {
            return myClass;
        }
    }

    private final TaskObjectiveCollection myObjectivesCommitted;

    private final TaskObjectiveCollection myObjectivesBuffer;

    private final Task myTask;

    private boolean isChanged = false;


    public ObjectivesTableModel(Task task) {
        this.myObjectivesCommitted = task.getObjectivesCollection();
        this.myObjectivesBuffer = new TaskObjectiveCollectionImpl(myObjectivesCommitted);
        myTask = task;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Column.values()[columnIndex].getColumnClass();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public int getRowCount() {
        return myObjectivesBuffer.size() + 1;
    }

    @Override
    public String getColumnName(int col) {
        return Column.values()[col].getName();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result;
        if (row >= 0) {
            if (row < myObjectivesBuffer.size()) {
                TaskObjective objective = myObjectivesBuffer.get(row);
                switch (col) {
                    case 0:
                        result = String.valueOf(objective.getId());
                        break;
                    case 1:
                        result = objective.getName();
                        break;
                    case 2:
                        result = String.valueOf(objective.getPercentage());
                        break;
                    case 3:
                        result =  objective.isChecked();
                        break;
                    default:
                        result = "";
                }
            } else {
                result = null;
            }
        } else {
            throw new IllegalArgumentException("I can't return data in row=" + row);
        }
        return result;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if(getValueAt(row, 3) != null && (boolean)getValueAt(row,3))
            return col == 3;
        return !(col == 0);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if(myTask.isSupertask())
            throw new SuperTaskObjectiveException();
        if(myTask.isMilestone())
            throw new MilestoneObjectiveException();

        if (row >= 0) {
            if (row >= myObjectivesBuffer.size()) {
                createObjective(value, col);
            } else {
                updateObjective(value, row, col);
            }

        } else {
            throw new IllegalArgumentException("I can't set data in row=" + row);
        }
        isChanged = true;
    }

    private void updateObjective(Object value, int row, int col) {
        TaskObjective updateTarget = myObjectivesBuffer.get(row);
        switch (col) {
            case 3: {
                updateTarget.check((Boolean) value);
                //if(updateTarget.isChecked())
                //    myTask.setMinPercentage(updateTarget.getPercentage());
                break;
            }
            case 2: {
                int percentage = 0;
                if(value != null)
                    percentage = (Integer) value;
                int leftOver = myObjectivesBuffer.getLeftOver();
                updateTarget.setPercentage(Math.min(leftOver, percentage));
                break;
            }
            case 1: {
                String name = String.valueOf(value);
                if(!name.equals(""))
                    updateTarget.setName(name);
                else
                    updateTarget.setName("Objective " + (updateTarget.getId()+1));
                break;
            }
            default:
                break;
        }
    }

    private void createObjective(Object value, int col) {
        int id = 0;
        if (getMyObjectives() != null)
            id = myObjectivesBuffer.size();
        String name = "Objective " + (id + 1);
        int percentage = 0;
        boolean isChecked = false;
        switch (col) {
            case 1:
                if(!value.equals(""))
                    name = (String) value;
                break;
            case 2:
                if(value != null)
                    percentage = (Integer) value;
                break;
            case 3:
                isChecked = (boolean) value;
                break;
            default:
                break;
        }
        if (value instanceof TaskObjective) {
            name= ((TaskObjective) value).getName();
            percentage = ((TaskObjective) value).getPercentage();
            isChecked = ((TaskObjective) value).isChecked();
        }

        TaskObjective newObjective = new TaskObjectiveImpl(id, name , percentage, isChecked);
        myObjectivesBuffer.add(newObjective);
        fireTableRowsInserted(myObjectivesBuffer.size(), myObjectivesBuffer.size());
    }

    public List<TaskObjective> getMyObjectives() {
        return Collections.unmodifiableList(myObjectivesCommitted.getObjectivesList());
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void delete(int[] selectedRows) {
        List<TaskObjective> selected = new ArrayList<>();
        for (int row : selectedRows) {
            if (row < myObjectivesBuffer.size()) {
                selected.add(myObjectivesBuffer.get(row));
            }
        }
        myObjectivesBuffer.removeAll(selected);
        fireTableDataChanged();
    }

    public void clear() {
        myObjectivesBuffer.clear();
    }

    public void commit() {
        remove0s();
        myObjectivesCommitted.clear();
        myObjectivesCommitted.addAll(myObjectivesBuffer);
        myObjectivesBuffer.clear();

        fireTableDataChanged();
        updateTask();
    }

    private void updateTask() {
        int checkedPercentage = myObjectivesCommitted.getCheckedPercentage();
        myTask.setMinPercentage(checkedPercentage);
        myTask.setCompletionPercentage(checkedPercentage);
    }

    //removes all objectives with 0%
    private void remove0s() {
        for(int i=0; i<myObjectivesBuffer.size(); i++) {
            TaskObjective obj = myObjectivesBuffer.get(i);
            if(obj.getPercentage() == 0)
                myObjectivesBuffer.remove(obj);

        }
    }
}
