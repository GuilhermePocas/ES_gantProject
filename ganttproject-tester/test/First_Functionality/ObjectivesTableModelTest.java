package First_Functionality;

import net.sourceforge.ganttproject.TestSetupHelper;
import net.sourceforge.ganttproject.gui.taskproperties.ObjectivesTableModel;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.TaskManagerImpl;
import org.junit.Before;
import org.junit.Test;
import

import static org.junit.Assert.*;

public class ObjectivesTableModelTest {

    TaskManager taskManager;
    Task task;
    ObjectivesTableModel objTable;

    @Before
    public void setUp() throws Exception {
        taskManager = TestSetupHelper.newTaskManagerBuilder().build();
        task = taskManager.createTask();
        objTable = new ObjectivesTableModel(task);
        //task.add
    }

    @Test
    public void getColumnCount() {
    }

    @Test
    public void getRowCount() {
    }

    @Test
    public void getValueAt() {
    }

    @Test
    public void isCellEditable() {
    }

    @Test
    public void setValueAt() {
    }

    @Test
    public void getMyObjectives() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void clear() {
    }

    @Test
    public void commit() {
    }
}