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
        
        // this happens if user changes the start or/and end dates of the task
        if(millisUntilSend < 0) {
            sendLateEmail();
            return;
        }

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
        if(System.currentTimeMillis()>sendEpochMilli()) return;
        scheduleEmail();
    }

    public void cancelScheduledEmail() {
        if(!emailScheduled || timer == null) return;
        timer.cancel();
        timer.purge();
        emailScheduled = false;
    }

    private void sendEmail() {
        sendEmail(task.getEmailNotificationPercentage());
    }

    private void sendEmail(int percentage) {
        EmailSender.getInstance().sendEmail(getRecipients(), task.getName(), percentage);
        task.setEmailNotificationActivated(false);
        emailSent = true;
        emailScheduled = false;
    }

    private String getRecipients() {
        ResourceAssignment[] myAssignments = task.getAssignments();

        String recipients = "";
        for(int i = 0; i < myAssignments.length; i++) {
            ResourceAssignment assignment = myAssignments[i];
            if(assignment.isCoordinator()) {
                String mail = assignment.getResource().getMail();
                if(!mail.isEmpty())
                    recipients += mail+",";
            }
        }
        if(!recipients.isEmpty()) 
            recipients = recipients.substring(0, recipients.length()-1);
        return recipients;
    }

    public double getMinPercentage() {
        long startEpochMilli = startEpochMilli();
        long endEpochMilli = endEpochMilli();

        if(System.currentTimeMillis() <= startEpochMilli) return 0;
        if(System.currentTimeMillis() >= endEpochMilli) return -1;

        long taskTotalDurationMilli = endEpochMilli-startEpochMilli;
        long taskRemainingDurationMilli = endEpochMilli-System.currentTimeMillis();

        double minPercentageDecimal = 1-((double)taskRemainingDurationMilli)/((double)taskTotalDurationMilli);
        return minPercentageDecimal*100;
    }

    public boolean emailHasBeenSent() {
        return emailSent;
    }

    // this method needs a small delay because when opening a project and importing tasks
    // the email scheduler is created before the assigned people are loaded
    // this lets the program load the assigned people before sending the email
    public void sendLateEmail() {
        Timer lateMailTimer = new Timer();
        lateMailTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int newPercentage = (int) Math.floor(getMinPercentage());
                sendEmail(newPercentage);
            }
        }, 1000);   
    }
}