package net.sourceforge.ganttproject.parser;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.sourceforge.ganttproject.CustomPropertyManager;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskObjectiveImpl;
import org.xml.sax.Attributes;

public class TaskObjectivesTagHandler extends AbstractTagHandler implements ParsingListener{
    private final ParsingContext myContext;

    public TaskObjectivesTagHandler(ParsingContext context) {
        super("objective");
        myContext = context;
    }

    @Override
    protected boolean onStartElement(Attributes attrs) {
        loadTaskObjective(attrs);
        return true;
    }

    private void loadTaskObjective(Attributes atts) {
        String name = atts.getValue("name");
        String id = atts.getValue("id");
        String percentage = atts.getValue("percentage");
        String cheked = atts.getValue("cheked");

        Task task = myContext.popTask();
        task.addObjective(new TaskObjectiveImpl(Integer.parseInt(id), name, Integer.parseInt(percentage), Boolean.parseBoolean(cheked)));
        myContext.pushTask(task);
    }

    @Override
    public void parsingStarted() {

    }

    @Override
    public void parsingFinished() {

    }
}
