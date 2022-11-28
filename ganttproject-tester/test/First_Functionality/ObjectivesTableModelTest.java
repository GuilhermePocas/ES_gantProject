package First_Functionality;

import net.sourceforge.ganttproject.TestSetupHelper;
import net.sourceforge.ganttproject.gui.taskproperties.ObjectivesTableModel;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.TaskObjective;
import net.sourceforge.ganttproject.task.TaskObjectiveCollection;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        objTable.setValueAt(10, 1, 2);
        objTable.setValueAt(20, 2, 2);
        objTable.setValueAt(30, 3, 2);
    }

    @Test
    public void getColumnCount() {
        assert(objTable.getColumnCount() == 4);
        objTable.setValueAt(20, 4, 2);
        objTable.commit();
        assert(objTable.getColumnCount() == 4);
    }

    @Test
    public void getRowCount() {
        assert(objTable.getRowCount() == 4);
        objTable.setValueAt(20, 4, 2);
        objTable.commit();
        assert(objTable.getRowCount() == 5);
    }

    @Test
    public void getValueAt() {
        assert(objTable.getValueAt(1, 1).equals("Objective 2"));
        assert(Integer.parseInt((String)objTable.getValueAt(0, 2)) == 10);
        objTable.setValueAt(true, 2, 3);
        assert((boolean)objTable.getValueAt(2, 3));
    }

    @Test
    public void isCellEditable() {
        assert(!objTable.isCellEditable(1, 0));
        assert(objTable.isCellEditable(1, 1));
        assert(objTable.isCellEditable(1, 2));
        assert(objTable.isCellEditable(1, 3));
        objTable.setValueAt(true, 1, 3);
        assert(!objTable.isCellEditable(1,0));
        assert(!objTable.isCellEditable(1,1));
        assert(!objTable.isCellEditable(1,2));
        assert(objTable.isCellEditable(1,3));
    }

    @Test
    public void delete() {
        int[] indexes = {0,2};
        objTable.delete(indexes);
        System.out.println(objTable.getValueAt(0,1));
        assert(objTable.getValueAt(0, 1).equals("Objective 2"));
    }

    @Test
    public void commit() {
        List<TaskObjective> listUncommited = task.getObjectivesCollection().getObjectivesList();
        assert(listUncommited.isEmpty());
        objTable.commit();

        List<TaskObjective> listCommited = task.getObjectivesCollection().getObjectivesList();
        List<TaskObjective> objList = objTable.getMyObjectives();
        assertEquals(objList.get(0).getId(), listCommited.get(0).getId());
        assertEquals(objList.get(1).getId(), listCommited.get(1).getId());
        assertEquals(objList.get(2).getId(), listCommited.get(2).getId());


    }
}