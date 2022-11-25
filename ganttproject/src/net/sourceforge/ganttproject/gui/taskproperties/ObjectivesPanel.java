package net.sourceforge.ganttproject.gui.taskproperties;


import biz.ganttproject.core.option.*;
import net.sourceforge.ganttproject.gui.AbstractTableAndActionsComponent;
import net.sourceforge.ganttproject.gui.UIUtil;
import net.sourceforge.ganttproject.gui.options.OptionsPageBuilder;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.roles.RoleManager;
import net.sourceforge.ganttproject.task.*;
import net.sourceforge.ganttproject.task.dependency.TaskDependency;
import net.sourceforge.ganttproject.task.dependency.TaskDependencyConstraint;
import net.sourceforge.ganttproject.task.dependency.constraint.FinishFinishConstraintImpl;
import net.sourceforge.ganttproject.task.dependency.constraint.FinishStartConstraintImpl;
import net.sourceforge.ganttproject.task.dependency.constraint.StartFinishConstraintImpl;
import net.sourceforge.ganttproject.task.dependency.constraint.StartStartConstraintImpl;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;
import net.sourceforge.ganttproject.gui.taskproperties.ObjectivesTableModel.*;
import org.jdesktop.swingx.multisplitpane.DefaultSplitPaneModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class ObjectivesPanel {

    private static final Integer [] PERCENTAGE = new Integer[]{0,10,20,30,40,50,60,70,80,90,100};

    private ObjectivesTableModel myModel;
    private final Task myTask;
    private JTable myTable;

    public ObjectivesPanel(Task task) {
        myTask = task;
    }

    private JTable getTable() {
        return myTable;
    }

    public JPanel getComponent() {
        myModel = new ObjectivesTableModel(myTask.getObjectivesCollection());
        myTable = new JTable(myModel);
        UIUtil.setupTableUI(getTable());
        CommonPanel.setupComboBoxEditor(getTable().getColumnModel().getColumn(2), PERCENTAGE);
        //CommonPanel.setupComboBoxEditor(getTable().getColumnModel().getColumn(1), myHRManager.getResources().toArray());
        //CommonPanel.setupComboBoxEditor(getTable().getColumnModel().getColumn(4), myRoleManager.getEnabledRoles());

        AbstractTableAndActionsComponent<TaskObjective> tableAndActions =
                new AbstractTableAndActionsComponent<TaskObjective>(getTable()) {
                    @Override
                    protected void onAddEvent() {
                        getTable().editCellAt(myModel.getRowCount() - 1, 1);
                    }

                    @Override
                    protected void onDeleteEvent() {
                        if (getTable().isEditing() && getTable().getCellEditor() != null) {
                            getTable().getCellEditor().stopCellEditing();
                        }
                        myModel.delete(getTable().getSelectedRows());
                    }

                    @Override
                    protected TaskObjective getValue(int row) {
                        java.util.List<TaskObjective> values = myModel.getMyObjectives();
                        return (row >= 0 && row < values.size()) ? values.get(row) : null;
                    }
                };
        return CommonPanel.createTableAndActions(myTable, tableAndActions.getActionsComponent());

    }

    public void commit() {
        if (myTable.isEditing()) {
            myTable.getCellEditor().stopCellEditing();
        }
        myModel.commit();
    }


}
