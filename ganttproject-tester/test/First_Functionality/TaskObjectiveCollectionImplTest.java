package First_Functionality;

import net.sourceforge.ganttproject.task.TaskObjective;
import net.sourceforge.ganttproject.task.TaskObjectiveCollectionImpl;
import net.sourceforge.ganttproject.task.TaskObjectiveImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TaskObjectiveCollectionImplTest {

    private TaskObjectiveCollectionImpl col;

    @Before
    public void setup(){
        col = new TaskObjectiveCollectionImpl();
        col.add(new TaskObjectiveImpl(1, "obj1", 10, false));
        col.add(new TaskObjectiveImpl(2, "obj2", 20, false));
        col.add(new TaskObjectiveImpl(3, "obj3", 30, false));
    }

    @Test
    public void removeAll() {
        List<TaskObjective> list = new ArrayList<>();
        list.add(col.get(0));
        list.add(col.get(2));
        col.removeAll(list);
        assert(col.get(0).getId() == 2);
    }

    @Test
    public void reachedTheMaximum() {
        assert(col.reachedTheMaximum() == false);
        col.add(new TaskObjectiveImpl(4, "obj4", 20, false));
        assert(col.reachedTheMaximum() == false);
        col.add(new TaskObjectiveImpl(5, "obj5", 20, false));
        assert(col.reachedTheMaximum() == true);
    }


    @Test
    public void getTotalPercentage() {
        assert(col.getTotalPercentage() == 60);
        col.add(new TaskObjectiveImpl(4, "obj4", 20, false));
        assert(col.getTotalPercentage() == 80);
        col.add(new TaskObjectiveImpl(5, "obj5", 500, false));
        assert(col.getTotalPercentage() == 100);
    }

    @Test
    public void getCheckedPercentage() {
        assert(col.getCheckedPercentage() == 0);
        col.get(0).check(true);
        assert(col.getCheckedPercentage() == 10);
        col.get(1).check(true);
        assert(col.getCheckedPercentage() == 30);
        col.get(2).check(true);
        assert(col.getCheckedPercentage() == 60);
        col.add(new TaskObjectiveImpl(4, "obj4", 40, true));
        assert(col.getCheckedPercentage() == 100);

    }

    @Test
    public void getLeftOver() {
        assert(col.getLeftOver() == 40);
        col.add(new TaskObjectiveImpl(4, "obj4", 20, false));
        assert(col.getLeftOver() == 20);
        col.add(new TaskObjectiveImpl(5, "obj5", 20, false));
        assert(col.getLeftOver() == 0);
    }

    @Test
    public void copy() {
        TaskObjectiveCollectionImpl col2 = new TaskObjectiveCollectionImpl();
        col2.add(new TaskObjectiveImpl(4, "obj4", 10, false));
        col2.add(new TaskObjectiveImpl(5, "obj5", 20, true));

        col.copy(col2);
        assert(col.get(0).getId() == 4);
        assert(col.get(1).getId() == 5);
        assert(col.getTotalPercentage() == 30);
        assert(col.getCheckedPercentage() == 20);
    }


}