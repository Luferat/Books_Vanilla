package com.books.api.service;

import com.books.api.config.Config; // Importar a classe Config
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private final Config config; // Injetar a classe Config
    private final SendGrid sendGrid; // SendGrid será inicializado no construtor

    // O construtor será chamado após a injeção dos valores @Value
    // Removido @Value e agora injetamos Config diretamente
    public EmailService(Config config) {
        this.config = config;
        this.sendGrid = new SendGrid(this.config.getSendgridAPIKey());
    }

    /**
     * Envia um e-mail simples usando a API do SendGrid.
     *
     * @param to      O endereço de e-mail do destinatário.
     * @param subject O assunto do e-mail.
     * @param text    O corpo do e-mail.
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        // Usar o fromEmail da classe Config
        Email from = new Email(config.getSendgridFromEmail());
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            System.out.println("E-mail enviado via SendGrid para: " + to);
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                // Sucesso
            } else {
                // Erro na API do SendGrid
                throw new RuntimeException("Falha ao enviar e-mail via SendGrid. Status: " + response.getStatusCode() + ", Body: " + response.getBody());
            }

        } catch (IOException e) {
            System.err.println("Erro de IO ao enviar e-mail para " + to + ": " + e.getMessage());
            throw new RuntimeException("Falha de IO ao enviar e-mail.", e);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao enviar e-mail para " + to + ": " + e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail.", e);
        }
    }
}
