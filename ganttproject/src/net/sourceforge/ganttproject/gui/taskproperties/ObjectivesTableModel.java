package net.sourceforge.ganttproject.gui.taskproperties;

import com.google.common.base.Objects;
import net.sourceforge.ganttproject.GPLogger;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.resource.HumanResource;
import net.sourceforge.ganttproject.roles.Role;
import net.sourceforge.ganttproject.task.ResourceAssignment;
import net.sourceforge.ganttproject.task.ResourceAssignmentCollection;
import net.sourceforge.ganttproject.task.ResourceAssignmentMutator;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.dependency.TaskDependency;
import net.sourceforge.ganttproject.task.dependency.TaskDependencyCollectionMutator;
import net.sourceforge.ganttproject.task.dependency.TaskDependencyConstraint;
import net.sourceforge.ganttproject.task.dependency.TaskDependencyException;
import net.sourceforge.ganttproject.task.dependency.constraint.ConstraintImpl;
import net.sourceforge.ganttproject.task.dependency.constraint.FinishStartConstraintImpl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ObjectivesTableModel extends AbstractTableModel {

    public class Objective {
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

    static enum Column {
        ID("id", String.class), NAME("objectivename", String.class),
        PERCENTAGE("percentage", String.class), CHECKED("checked", Boolean.class);

        private final String myName;
        private final Class<?> myClass;

        Column(String key, Class<?> clazz) {
            myName = key;//GanttLanguage.getInstance().getText(key);
            myClass = clazz;
        }

        String getName() {
            return myName;
        }

        Class<?> getColumnClass() {
            return myClass;
        }
    }

    private final List<Objective> myObjectives;

    //private final ResourceAssignmentMutator myMutator;

    private boolean isChanged = false;

    public ObjectivesTableModel(List<Objective> myObjectives) {
        this.myObjectives = myObjectives;
        //myMutator = assignmentCollection.createMutator();
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
                Objective objective = myObjectives.get(row);
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
        return col == 4;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (row >= 0) {
            if (row >= myObjectives.size()) {
                createObjective((Objective) value);
            } else {
                updateObjective(value, row, col);
            }
        } else {
            throw new IllegalArgumentException("I can't set data in row=" + row);
        }
        isChanged = true;
    }

    private void updateObjective(Object value, int row, int col) {
        Objective updateTarget = myObjectives.get(row);
        switch (col) {
            case 4: {
                updateTarget.check(((Boolean) value).booleanValue());
                break;
            }
            case 3: {
                int loadAsInt = Integer.parseInt(String.valueOf(value));
                updateTarget.setPercentage(loadAsInt);
                break;
            }
            case 2: {
                updateTarget.setName(String.valueOf(value));
                break;
            }
            /*case 1: {
                if (value == null) {
                    updateTarget.delete();
                    myAssignments.remove(row);
                    fireTableRowsDeleted(row, row);
                } else if (value instanceof HumanResource) {
                    float load = updateTarget.getLoad();
                    boolean coord = updateTarget.isCoordinator();
                    updateTarget.delete();
                    myMutator.deleteAssignment(updateTarget.getResource());
                    ResourceAssignment newAssignment = myMutator.addAssignment((HumanResource) value);
                    newAssignment.setLoad(load);
                    newAssignment.setCoordinator(coord);
                    myAssignments.set(row, newAssignment);
                }
                break;

            }*/
            default:
                break;
        }
    }

    private void createObjective(Objective value) {
        /*if (value instanceof Objective) {
            ResourceAssignment newAssignment = myMutator.addAssignment((HumanResource) value);
            newAssignment.setLoad(100);

            boolean coord = false;
            if (myAssignments.isEmpty())
                coord = true;
            newAssignment.setCoordinator(coord);
            newAssignment.setRoleForAssignment(newAssignment.getResource().getRole());
            myAssignments.add(newAssignment);
            fireTableRowsInserted(myAssignments.size(), myAssignments.size());*/
            Objective newObjective = new Objective(value.getId(), value.getName(), value.getPercentage());
            newObjective.setPercentage(0);
            newObjective.check(false);
            myObjectives.add(newObjective);
            fireTableRowsInserted(myObjectives.size(), myObjectives.size());
        //}
    }

    public List<Objective> getMyObjectives() {
        return Collections.unmodifiableList(myObjectives);
    }

/*    public void commit() {
        myMutator.commit();
    }*/

    public boolean isChanged() {
        return isChanged;
    }

    public void delete(int[] selectedRows) {
        List<Objective> selected = new ArrayList<Objective>();
        for (int row : selectedRows) {
            if (row < myObjectives.size()) {
                selected.add(myObjectives.get(row));
            }
        }
        /*for (Objective obj : selected) {
            myObjectives.remove(obj);
        }*/
        myObjectives.removeAll(selected);
        fireTableDataChanged();
    }

}
