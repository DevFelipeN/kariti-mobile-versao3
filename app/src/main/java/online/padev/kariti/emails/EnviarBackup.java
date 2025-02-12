package online.padev.kariti.emails;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

public class EnviarBackup {

    public boolean enviaBackup(Context context, String email, int version) {
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.auth", "true");

        if (email == null){
            Log.e("kariti", "Banco de dados não encontrado!");
            return false;
        }

        File dbFile = context.getDatabasePath("base_dados.db");

        if (!dbFile.exists()) {
            Log.e("kariti", "Banco de dados não encontrado!");
            return false;
        }

        // fecha qualquer execução do banco em aberto
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        db.close();

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kariti2024@gmail.com", "ukqv thud bgpr iomq");
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Backup Kariti");

            // Parte do texto do e-mail
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent("<p>Segue em anexo o backup do seus dados do Kariti.</p>" +
                    "<p>Siga as etapas para instalar e cadastrar seus dados no novo dispositivo.</p>" +
                    "<p>1 - Baixe e instale o app do Kariti disponível na Play Store em seu novo dipositivo </p> " +
                    "<p>2 - Baixe o arquivo deste e-mail no seu novo dispositivo</p>" +
                    "<p>3 - Abra o aplicativo do Kariti e selecione a opção 'Cadastre-se', em seguida a opção 'Clique aqui' localizada na parte inferior do app</p>" +
                    "<p>4 - Selecione o arquivo baixado que você recebeu por e-mail</p>" +
                    "<br></br><p> Prontinho, agora é só acessar o Kariti com seu login e senha para continuar usando!</p>" +
                    "<br></br><p>Equipe Kariti</p>", "text/html; charset=utf-8");

            // Parte do anexo
            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource fileSource = new FileDataSource(dbFile);
            attachmentPart.setDataHandler(new DataHandler(fileSource));
            attachmentPart.setFileName("backup_kariti_"+version+".db"); // Nome do arquivo no e-mail

            // Monta o corpo do e-mail
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            // Envio do e-mail em uma thread separada
            Thread t = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    Log.e("kariti", e.getMessage());
                }
            });
            t.start();

        } catch (Exception e) {
            Log.e("kariti", "Erro ao enviar backup: " + e.getMessage());
            return false;
        }
        return true;
    }
}