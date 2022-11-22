package net.sourceforge.ganttproject.task;

import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.ResourceAssignment;
import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.EmailSender;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;

public class EmailScheduler {

    private Task task;

    private boolean emailScheduled;
    private boolean emailSent;
    private Timer timer;

    public EmailScheduler(Task task) {
        this.task = task;
        emailScheduled = false;
        emailSent = false;
    }

    // Java starts months at 0 (January) so it must be offset by 1
    private long startEpochMilli() {
        GanttCalendar startDate = task.getStart();
        LocalDate startLocalDate = LocalDate.of(startDate.getYear(), startDate.getMonth()+1, startDate.getDay());
        Instant startInstant = startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return startInstant.toEpochMilli();
    }

    private long endEpochMilli() {
        GanttCalendar endDate = task.getEnd();
        LocalDate endLocalDate = LocalDate.of(endDate.getYear(), endDate.getMonth()+1, endDate.getDay());
        Instant endInstant = endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return endInstant.toEpochMilli();
    }

    // returns instant in milliseconds when email should be sent
    private long sendEpochMilli() {
        long startEpochMilli = startEpochMilli();
        long endEpochMilli = endEpochMilli();

        long taskDurationMilli = endEpochMilli-startEpochMilli;
        double percentage = task.getEmailNotificationPercentage();
        long notificationEpochMilli = startEpochMilli + Math.round(taskDurationMilli*(percentage/100));

        return notificationEpochMilli;
    }

    public void scheduleEmail() {
        if(emailScheduled && !emailSent) recalculateSchedule();
        emailScheduled = true;
        emailSent = false;

        long millisUntilSend = sendEpochMilli()-System.currentTimeMillis();
        // the only way this condition can be met is if the user has had the task properties window open
        // for so long that the task percentage bounds for email notification are no longer valid
        if(millisUntilSend <= 0) sendEmail();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendEmail();
            }
        }, millisUntilSend);
    }

    public void recalculateSchedule() {
        if(!emailScheduled || timer == null) return;
        if(!emailSent) cancelScheduledEmail();
        scheduleEmail();
    }

    public void cancelScheduledEmail() {
        if(!emailScheduled || timer == null) return;
        timer.cancel();
        timer.purge();
        emailScheduled = false;
    }

    public void sendEmail() {
        EmailSender.getInstance().sendEmail(getRecipients(), task.getName(), task.getEmailNotificationPercentage());
        emailSent = true;
    }

    private String getRecipients() {
        ResourceAssignment[] myAssignments = task.getAssignments();

        String recipients = "";
        for(int i = 0; i < myAssignments.length; i++) {
            ResourceAssignment assignment = myAssignments[i];
            if(assignment.isCoordinator())
                recipients += assignment.getResource().getMail()+",";
        }
        return recipients.substring(0, recipients.length()-1);
    }

    public int getMinPercentage() {
        long startEpochMilli = startEpochMilli();
        long endEpochMilli = endEpochMilli();
        System.out.printf("%d %d %d\n", startEpochMilli, endEpochMilli, System.currentTimeMillis());

        if(System.currentTimeMillis() <= startEpochMilli) return 100;
        if(System.currentTimeMillis() >= endEpochMilli) return -1;

        long taskTotalDurationMilli = endEpochMilli-startEpochMilli;
        long taskRemainingDurationMilli = endEpochMilli-System.currentTimeMillis();

        double minPercentageDecimal = 1-((double)taskRemainingDurationMilli)/((double)taskTotalDurationMilli);
        int minPercentage = (int) Math.ceil(minPercentageDecimal*100);
        return minPercentage;
    }
}