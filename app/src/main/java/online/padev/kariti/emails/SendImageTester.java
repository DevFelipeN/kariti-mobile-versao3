package online.padev.kariti.emails;

import android.util.Log;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendImageTester {

    public static boolean sendInZip(File fileZip){

        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kariti2024@gmail.com", "ukqv thud bgpr iomq");
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("clickfelipeweb@gmail.com"));
            message.setSubject("Tester Kariti");

            // Parte do texto do e-mail
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent("<p>Segue em anexo o arquivo Zip de teste do Kariti.</p>" +
                                      "<br></br><p>Equipe Kariti</p>", "text/html; charset=utf-8");

            // Parte do anexo
            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource fileSource = new FileDataSource(fileZip);
            attachmentPart.setDataHandler(new DataHandler(fileSource));
            attachmentPart.setFileName(fileZip.getName()); // Nome do arquivo no e-mail

            // Monta o corpo do e-mail
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            try {
                Transport.send(message);
            } catch (MessagingException e) {
                Log.e("kariti", e.getMessage());
            }
            /*
            // Envio do e-mail em uma thread separada
            Thread t = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    Log.e("kariti", e.getMessage());
                }
            });
            t.start();
            */
        } catch (Exception e) {
            Log.e("kariti", "Erro ao enviar backup: " + e.getMessage());
            return false;
        }
        return true;

    }

}
