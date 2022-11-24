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

    private TaskObjectiveCollection myObjectivesCommitted;

    private TaskObjectiveCollection myObjectives;

    private final Task myTask;

    private boolean isChanged = false;



    public ObjectivesTableModel(TaskObjectiveCollection myObjectivesCol) {
        this.myObjectives = myObjectivesCol;
        this.myObjectivesCommitted = myObjectivesCol;
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
            if (row >= myObjectives.size()) {
                if(myObjectives.getTotalPercentage() < 100)
                    createObjective(value, col);
            } else {
                updateObjective(value, row, col);
            }

            if(col == 3) {
                myObjectives.get(row).check((boolean)value);
            }
        } else {
            throw new IllegalArgumentException("I can't set data in row=" + row);
        }
        isChanged = true;
    }

    private void updateObjective(Object value, int row, int col) {
        TaskObjective updateTarget = myObjectives.get(row);
        myObjectives.remove(updateTarget);
        switch (col) {
            case 3: {
                updateTarget.check(((Boolean) value).booleanValue());
                if(updateTarget.isChecked())
                    myTask.setMinPercentage(updateTarget.getPercentage());
                break;
            }
            case 2: {
                int oldPercentage = updateTarget.getPercentage();
                addPercentage(-oldPercentage);

                int loadAsInt = Integer.parseInt(String.valueOf(value));
                int percentage = addPercentage(loadAsInt);
                updateTarget.setPercentage(percentage);
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
/*            case 0: {
                if (value == null) {
                    myObjectives.removeIndex(row);
                    fireTableRowsDeleted(row, row);
                }
                break;
            }*/
            default:
                break;
        }
        myObjectives.add(updateTarget);
    }

    private void createObjective(Object value, int col) {

        int id = 0;
        if (getMyObjectives() != null)
            id = myObjectives.size();
        String name = "Objective " + (id+1);
        int percentage = 0;
        boolean isChecked = false;
        switch (col) {
            case 1:
                if(!value.equals(""))
                    name = (String) value;
                break;
            case 2:
                int perc = Integer.parseInt((String) value);
                percentage = addPercentage(perc);
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

    public void clear() {
        myObjectives.clear();
    }

    public void reset() {
        myObjectives = new TaskObjectiveCollectionImpl(myObjectivesCommitted);
    }

    public void commit() {
        myObjectivesCommitted = new TaskObjectiveCollectionImpl(myObjectives);
        int checkedPercentage = myObjectives.getCheckedPercentage();
        myTask.setMinPercentage(checkedPercentage);
        myTask.setCompletionPercentage(checkedPercentage);
    }

    private int addPercentage(int num) {
        int totalPercentage = myObjectives.getTotalPercentage();
        int sum = totalPercentage+num;
        if(sum<0)
            return 0;
        if(sum <= 100) {
            totalPercentage+=num;
            return num;
        }
        int maxPossible = 100 - totalPercentage;
        totalPercentage = 100;
        return maxPossible;
    }

}
