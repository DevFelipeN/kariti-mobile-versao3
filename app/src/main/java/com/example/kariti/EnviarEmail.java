package com.example.kariti;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviarEmail {

    public Boolean enviaCodigo(String email, String codigo){
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kariti2024@gmail.com", "xbpw eypf tfyk kyjh");
            }
        });
        try {
            MimeMessage m = new MimeMessage(session);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress("clickfelipeweb@gmail.com"));
            m.setSubject("Código de verificação do KARITI ("+codigo+")");
            m.setContent("<p>Insira do código de 4 dígitos abaixo para confirmar sua identidade no aplicativo Kariti:</p><br><b>" + codigo + "</b><br><p>Obrigado por nos ajudar a proteger sua conta.</p><br><p><b>Equipe Kariti</b></p>", "text/html; charset=utf-8");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(m);
                    }catch (Exception e){

                    }
                }
            });
            t.start();
        }catch (Exception e){
            return false;
        }

        return true;
    }
}
