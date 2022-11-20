package net.sourceforge.ganttproject.gui.taskproperties;

public class Over100Exception extends RuntimeException{

    final static String message = "Task percentage exceeds 100";
    public Over100Exception() {
        super(message);
    }
}
