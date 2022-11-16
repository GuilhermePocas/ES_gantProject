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

    private ObjectivesTableModel myModel;
    //private final HumanResourceManager myHRManager;
    //private final RoleManager myRoleManager;
    private final Task myTask;
    /*private final DefaultBooleanOption myCostIsCalculated = new DefaultBooleanOption("taskProperties.cost.calculated");
    private final DefaultDoubleOption myCostValue = new DefaultDoubleOption("taskProperties.cost.value") {

        @Override
        public void setValue(Double value) {
            // TODO Auto-generated method stub
            super.setValue(value);
        }

    };*/
    //private final GPOptionGroup myCostGroup = new GPOptionGroup("task.cost", myCostIsCalculated, myCostValue);

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
        /*String layoutDef = "(ROW weight=1.0 (LEAF name=objectives weight=0.5))";

        JXMultiSplitPane result = new JXMultiSplitPane();
        result.setDividerSize(0);

        MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
        result.getMultiSplitLayout().setModel(modelRoot);
        result.add(tablePanel, "objectives");
        return result;*/
    }
/*
    private JComponent createCostPanel() {
        myCostIsCalculated.setValue(myTask.getCost().isCalculated());
        myCostIsCalculated.addChangeValueListener(new ChangeValueListener() {
            @Override
            public void changeValue(ChangeValueEvent event) {
                myCostValue.setWritable(!myCostIsCalculated.isChecked());
                myCostValue.setValue(myTask.getCost().getValue().doubleValue());
            }
        });
        myCostValue.setValue(myTask.getCost().getValue().doubleValue());
        myCostValue.setWritable(!myCostIsCalculated.isChecked());

        OptionsPageBuilder builder = new OptionsPageBuilder();
        OptionsPageBuilder.BooleanOptionRadioUi radioUi = OptionsPageBuilder.createBooleanOptionRadioUi(myCostIsCalculated);

        JPanel optionsPanel = new JPanel();
        optionsPanel.add(radioUi.getYesButton());
        optionsPanel.add(new JLabel(myTask.getCost().getCalculatedValue().toPlainString()));
        optionsPanel.add(radioUi.getNoButton());
        optionsPanel.add(builder.createOptionComponent(myCostGroup, myCostValue));
        OptionsPageBuilder.TWO_COLUMN_LAYOUT.layout(optionsPanel, 2);

        final String yesLabelKey = builder.getI18N().getCanonicalOptionLabelKey(myCostIsCalculated) + ".yes";
        radioUi.getYesButton().setText(GanttLanguage.getInstance().getText(yesLabelKey));
        radioUi.getNoButton().setText(GanttLanguage.getInstance().getText(builder.getI18N().getCanonicalOptionLabelKey(myCostIsCalculated) + ".no"));
        UIUtil.createTitle(optionsPanel, builder.getI18N().getOptionGroupLabel(myCostGroup));

        JPanel result = new JPanel(new BorderLayout());
        result.add(optionsPanel, BorderLayout.NORTH);
        return result;
    }



    public void commit() {
        if (myTable.isEditing()) {
            myTable.getCellEditor().stopCellEditing();
        }
        myModel.commit();
        Task.Cost cost = myTask.getCost();
        cost.setCalculated(myCostIsCalculated.getValue());
        if (!cost.isCalculated()) {
            cost.setValue(BigDecimal.valueOf(myCostValue.getValue()));
        }
    }

 */
}
