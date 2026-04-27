import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class envioEmail {
	private static final String EmailGestor = "ru3enbras@gmail.com";
    private static final String SenhaEmail = "hqcz iufs khpo jzqr";
	// Método estático para envio de emails (usado pelo Login e pelo Dashboard)
    public static void enviarEmailSmtp(String destino, String assunto, String corpo) 
            throws MessagingException, java.io.UnsupportedEncodingException { // Adiciona isto aqui
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailGestor, SenhaEmail);
            }
        });

        Message message = new MimeMessage(session);
        
        // Agora o erro desaparece porque adicionámos a Exception lá em cima
        message.setFrom(new InternetAddress(EmailGestor, "Sistema de autenticação")); 
        
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
        message.setSubject(assunto);
        message.setContent(corpo, "text/html; charset=utf-8");

        Transport.send(message);
        
    }

}
