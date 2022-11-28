package First_Functionality;

import net.sourceforge.ganttproject.task.TaskObjectiveImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskObjectiveImplTest {

    private TaskObjectiveImpl obj1;
    private TaskObjectiveImpl obj2;
    private TaskObjectiveImpl obj3;

    @Before
    public void setUp() throws Exception {
       obj1 = new TaskObjectiveImpl(1, "obj1", 10, false);
       obj2 = new TaskObjectiveImpl(2, "obj2", 20, true);
       obj3 = new TaskObjectiveImpl(3, "obj3", 30, false);
    }

    @Test
    public void getName() {
        assert(obj1.getName() == "obj1");
        assert(obj2.getName() == "obj2");
        assert(obj3.getName() == "obj3");
    }

    @Test
    public void getId() {
        assert(obj1.getId() == 1);
        assert(obj2.getId() == 2);
        assert(obj3.getId() == 3);
    }

    @Test
    public void getPercentage() {
        assert(obj1.getPercentage() == 10);
        assert(obj2.getPercentage() == 20);
        assert(obj3.getPercentage() == 30);
    }

    @Test
    public void isChecked() {
        assert(!obj1.isChecked());
        assert(obj2.isChecked());
        assert(!obj3.isChecked());
    }

    @Test
    public void check() {
        obj1.check(true);
        obj2.check(false);
        obj3.check(true);
        assert(obj1.isChecked());
        assert(!obj2.isChecked());
        assert(obj3.isChecked());
    }

    @Test
    public void setName() {
        obj1.setName("obj01");
        obj2.setName("obj02");
        obj3.setName("obj03");
        assert(obj1.getName() == "obj01");
        assert(obj2.getName() == "obj02");
        assert(obj3.getName() == "obj03");
    }

    @Test
    public void setPercentage() {
        obj1.setPercentage(1);
        obj2.setPercentage(2);
        obj3.setPercentage(3);
        assert(obj1.getPercentage() == 1);
        assert(obj2.getPercentage() == 2);
        assert(obj3.getPercentage() == 3);
    }
}