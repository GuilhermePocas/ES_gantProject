package net.sourceforge.ganttproject.gui.taskproperties;


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

    private final TaskObjectiveCollection myObjectives;

    private final Task myTask;


    private boolean isChanged = false;

    public ObjectivesTableModel(TaskObjectiveCollection myObjectivesCol) {
        this.myObjectives = myObjectivesCol;
        myTask = myObjectivesCol.getTask();
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
        return myObjectives.size() + 1;
    }

    @Override
    public String getColumnName(int col) {
        return Column.values()[col].getName();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result;
        if (row >= 0) {
            if (row < myObjectives.size()) {
                TaskObjective objective = myObjectives.get(row);
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
        return !(col == 0);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (row >= 0) {
            if (row >= myObjectives.size()) {
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
        TaskObjective updateTarget = myObjectives.get(row);
        switch (col) {
            case 3: {
                updateTarget.check(((Boolean) value).booleanValue());
                break;
            }
            case 2: {
                int loadAsInt = Integer.parseInt(String.valueOf(value));
                if(myObjectives.reachedTheMaximum()) {
                    updateTarget.setPercentage(0);
                    throw new Over100Exception();
                }else if(loadAsInt + myObjectives.getTotalPercentage() > 100){
                    updateTarget.setPercentage(100 - myObjectives.getTotalPercentage());
                    throw new Over100Exception();
                } else {
                    updateTarget.setPercentage(loadAsInt);
                }
                break;
            }
            case 1: {
                updateTarget.setName(String.valueOf(value));
                break;
            }
            case 0: {
                if (value == null) {
                    myObjectives.removeIndex(row);
                    fireTableRowsDeleted(row, row);
                }
                break;
            }
            default:
                break;
        }
    }

    private void createObjective(Object value, int col) {
        int id = 0;
        if (getMyObjectives() == null){
            id = 0;
        }
        else
            id = myObjectives.size();
        String name = "Objective " + (id+1);
        int percentage = 0;
        boolean isChecked = false;
        switch (col) {
            case 1:
                name = (String) value;
                break;
            case 2:
                int perc = Integer.parseInt((String) value);
                if(myObjectives.reachedTheMaximum()) {
                    percentage = 0;
                    throw new Over100Exception();
                }else if(perc + myObjectives.getTotalPercentage() > 100){
                    percentage = 100 - myObjectives.getTotalPercentage();
                    throw new Over100Exception();
                } else{
                    percentage = perc;
                }
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
        myObjectives.add(newObjective);
        fireTableRowsInserted(myObjectives.size(), myObjectives.size());
    }

    public List<TaskObjective> getMyObjectives() {
        return Collections.unmodifiableList(myObjectives.getObjectivesList());
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void delete(int[] selectedRows) {
        List<TaskObjective> selected = new ArrayList<>();
        for (int row : selectedRows) {
            if (row < myObjectives.size()) {
                selected.add(myObjectives.get(row));
            }
        }
        myObjectives.removeAll(selected);
        fireTableDataChanged();
    }

}
