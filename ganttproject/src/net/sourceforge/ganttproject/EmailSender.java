package net.sourceforge.ganttproject;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class EmailSender {

    private static EmailSender instance;

    // to change "percentage", can't think of a better word for now
    private final String SUBJECT_TEMPLATE = "GanttProject %s Task Percentage Reached";
    private final String BODY_TEMPLATE = "Your GanttProject Task %s has reached %d%% progress.";

    private Session session;

    public static EmailSender getInstance() {
        if(instance == null)
            instance = new EmailSender();
        return instance;
    }

    private EmailSender() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ganttprojectemailnotification", "eodneynywgmlhzop");
            }
        });
    }

    public void sendEmail(String recipients, String taskName, int percentage) {
        if(recipients.length() == 0) return;
        try {
            MimeMessage message = new MimeMessage(session);

            String subject = String.format(SUBJECT_TEMPLATE, taskName);
            String body = String.format(BODY_TEMPLATE, taskName, percentage);

            message.setRecipients(Message.RecipientType.TO, recipients);
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


}