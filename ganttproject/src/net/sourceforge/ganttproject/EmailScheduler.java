package net.sourceforge.ganttproject;

import net.sourceforge.ganttproject.task.Task;
import biz.ganttproject.core.time.GanttCalendar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;

public class EmailScheduler {

    private String taskName;
    private int percentage;
    private String[] recipients;
    private long milliUntilSend;


    public EmailScheduler(GanttCalendar startDate, GanttCalendar endDate, double percentage, String taskName, String[] recipients) {
        this.taskName = taskName;
        this.recipients = recipients;

        // percentage is only kept to send its value in the mail so while it's used in this method as a double,
        // it's kept in this class as an int
        // the parameter itself is passed as an int so there is no problem in casting it back to int
        this.percentage = (int) percentage;

        // for some reason Java's LocalDate Month starts at 0 (January) so they must be offset by 1
        LocalDate startLocalDate = LocalDate.of(startDate.getYear(), startDate.getMonth()+1, startDate.getDay());
        LocalDate endLocalDate = LocalDate.of(endDate.getYear(), endDate.getMonth()+1, endDate.getDay());
        Instant startInstant = startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        long startEpochMilli = startInstant.toEpochMilli();
        long endEpochMilli = endInstant.toEpochMilli();

        long taskDurationMilli = endEpochMilli-startEpochMilli;
        long notificationEpochMilli = startEpochMilli + Math.round(taskDurationMilli*(percentage/100));

        milliUntilSend = notificationEpochMilli-System.currentTimeMillis();
    }

    public void scheduleEmail() {
        if(scheduledTimeHasPassed()) return;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EmailSender.getInstance().sendEmail(recipients, taskName, percentage);
            }
        }, milliUntilSend);
    }

    public boolean scheduledTimeHasPassed() {
        return milliUntilSend < 0;
    }
}