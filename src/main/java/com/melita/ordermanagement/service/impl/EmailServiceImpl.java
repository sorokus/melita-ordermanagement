package com.melita.ordermanagement.service.impl;

import com.melita.ordermanagement.base.exceptions.SystemException;
import com.melita.ordermanagement.model.dto.OrderDto;
import com.melita.ordermanagement.model.dto.ProductDto;
import com.melita.ordermanagement.service.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Collection;

import jakarta.mail.internet.MimeMessage;

/*
 * @author sorokus.dev@gmail.com
 */

@Service
public class EmailServiceImpl implements EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${app.agent.email}")
    private String agentEmail;

    @Value("${spring.mail.username}")
    private String sender;

    private JavaMailSender       javaMailSender;
    private SpringTemplateEngine templateEngine;

    @Override
    public void sendNewOrderForApprovalHtmlMail(Long orderId,
                                                OrderDto orderDto,
                                                Collection<ProductDto> selectedProducts,
                                                Collection<ProductDto> approvalProducts,
                                                String linkForApproval) throws SystemException {
        final Context ctx = new Context();
        ctx.setVariable("orderId", orderId);
        ctx.setVariable("order", orderDto);
        ctx.setVariable("products", selectedProducts);
        ctx.setVariable("approvalProducts", approvalProducts);
        ctx.setVariable("approvalLink", linkForApproval);

        baseSendNewOrderHtmlMail(ctx,
                                 "new_order_requires_approval.html",
                                 "[Approval] New Order #" + orderId + " arrived from the client and requires approval");
    }

    @Override
    public void sendNewOrderHtmlMail(Long orderId,
                                     OrderDto orderDto,
                                     Collection<ProductDto> selectedProducts) throws SystemException {
        final Context ctx = new Context();
        ctx.setVariable("orderId", orderId);
        ctx.setVariable("order", orderDto);
        ctx.setVariable("products", selectedProducts);

        baseSendNewOrderHtmlMail(ctx, "new_order.html", "[Info] New Order #" + orderId + " arrived from the client");
    }

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    private void baseSendNewOrderHtmlMail(Context ctx, String templateName, String subject) throws SystemException {
        try {
            final String htmlContent = this.templateEngine.process(templateName, ctx);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(htmlContent, true);
            helper.setTo(agentEmail);
            helper.setSubject(subject);
            helper.setFrom(sender);
            javaMailSender.send(mimeMessage);
        }

        catch (Exception e) {
            throw new SystemException("Error while sending Order Mail", e);
        }
    }
}