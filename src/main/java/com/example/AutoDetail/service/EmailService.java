package com.example.AutoDetail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Отправка простого текстового email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@autodetail.ru");

            mailSender.send(message);
            logger.info("✅ Email отправлен на: {}", to);
        } catch (Exception e) {
            logger.error("❌ Ошибка отправки email на {}: {}", to, e.getMessage());
        }
    }

    /**
     * Отправка HTML email с использованием Thymeleaf шаблона
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@autodetail.ru");

            mailSender.send(mimeMessage);
            logger.info("✅ HTML email отправлен на: {}", to);
        } catch (MessagingException e) {
            logger.error("❌ Ошибка отправки HTML email на {}: {}", to, e.getMessage());
        }
    }

    /**
     * Отправка уведомления о создании заказа
     */
    public void sendOrderCreatedNotification(String clientEmail, String clientName, Long orderId, Double totalAmount) {
        String subject = "Ваш заказ №" + orderId + " успешно оформлен - AutoDetail";
        String text = String.format(
                "Уважаемый %s!\n\n" +
                        "Ваш заказ №%d успешно оформлен.\n" +
                        "Сумма заказа: %.2f руб.\n\n" +
                        "Следите за статусом заказа в вашем личном кабинете.\n\n" +
                        "С уважением,\nКоманда AutoDetail",
                clientName, orderId, totalAmount
        );

        sendSimpleEmail(clientEmail, subject, text);
    }

    /**
     * Отправка уведомления об изменении статуса заказа
     */
    public void sendOrderStatusUpdateNotification(String clientEmail, String clientName, Long orderId, String status) {
        String subject = "Статус вашего заказа №" + orderId + " изменен - AutoDetail";
        String text = String.format(
                "Уважаемый %s!\n\n" +
                        "Статус вашего заказа №%d изменен на: %s.\n\n" +
                        "С уважением,\nКоманда AutoDetail",
                clientName, orderId, status
        );

        sendSimpleEmail(clientEmail, subject, text);
    }

    /**
     * Отправка красивого HTML уведомления о создании заказа
     */
    public void sendOrderCreatedHtmlNotification(String clientEmail, String clientName, Long orderId, Double totalAmount) {
        Context context = new Context();
        context.setVariable("clientName", clientName);
        context.setVariable("orderId", orderId);
        context.setVariable("totalAmount", totalAmount);

        sendHtmlEmail(clientEmail,
                "Ваш заказ №" + orderId + " успешно оформлен - AutoDetail",
                "emails/order-created",
                context);
    }

    /**
     * Отправка красивого HTML уведомления об изменении статуса
     */
    public void sendOrderStatusUpdateHtmlNotification(String clientEmail, String clientName, Long orderId, String newStatus) {
        Context context = new Context();
        context.setVariable("clientName", clientName);
        context.setVariable("orderId", orderId);
        context.setVariable("newStatus", newStatus);

        sendHtmlEmail(clientEmail,
                "Статус заказа №" + orderId + " изменен - AutoDetail",
                "emails/order-status-update",
                context);
    }
}