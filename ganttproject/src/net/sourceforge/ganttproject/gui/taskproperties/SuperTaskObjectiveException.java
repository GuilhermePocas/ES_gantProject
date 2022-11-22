package net.sourceforge.ganttproject.gui.taskproperties;

public class SuperTaskObjectiveException extends RuntimeException{

    final static String message = "Can't add objectives to a super task";

    public SuperTaskObjectiveException() {
        super(message);
    }
}
