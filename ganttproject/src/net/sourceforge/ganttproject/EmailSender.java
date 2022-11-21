package net.sourceforge.ganttproject;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class EmailSender {

    // to change "percentage", can't think of a better word for now
    private final String SUBJECT_TEMPLATE = "GanttProject %s Task Percentage Reached";
    private final String BODY_TEMPLATE = "Your GanttProject Task %s has reached %d%% progress.";
    private String subject, body;

    private final String SENDER_ADDRESS = "notification@ganttproject.gp";
    private String recipientAddress;

    private MimeMessage message;

    public EmailSender(String recipientAddress, String taskName, int percentage) {
        this.recipientAddress = recipientAddress;

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ganttprojectemailnotification", "eodneynywgmlhzop");
            }
        });

        try {
            message = new MimeMessage(session);

            subject = String.format(SUBJECT_TEMPLATE, taskName);
            body = String.format(BODY_TEMPLATE, taskName, percentage);

            message.setFrom(new InternetAddress(SENDER_ADDRESS));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.setSubject(subject);
            message.setText(body);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void send() {
        try {
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


}