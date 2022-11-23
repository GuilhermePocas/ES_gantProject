package net.sourceforge.ganttproject.gui.taskproperties;

public class MilestoneObjectiveException extends RuntimeException {
    final static String message = "Can't add objectives to a milestone.";

    public MilestoneObjectiveException() {
        super(message);
    }
}
