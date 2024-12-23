package com.SafeCryptoStocks.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.SafeCryptoStocks.model.Stock;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private final String senderEmail = "rameshkumar@gmail.com"; // Set your sender's email address here

    public void sendOtpEmail(String toEmail, String subject, String otp) {
        String content = "<html><body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                + "<div style='background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center;'>"
                + "<h2 style='color: #4CAF50;'>Your OTP Verification Code</h2>"
                + "<p style='font-size: 16px; color: #333;'>Dear User,</p>"
                + "<p style='font-size: 16px; color: #333;'>Use the following OTP to complete your verification process:</p>"
                + "<p style='font-size: 24px; color: #007bff; font-weight: bold; margin: 20px 0;'>" + otp + "</p>"
                + "<p style='font-size: 14px; color: #888;'>This OTP is valid for 10 minutes. Please do not share it with anyone.</p>"
                + "<footer style='text-align: center; color: #888; font-size: 12px; margin-top: 20px;'>"
                + "<p>&copy; 2024 SafeCryptoStocks. All rights reserved.</p>"
                + "</footer>"
                + "</div>"
                + "</body></html>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail); // Set the sender's email address
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // Enable HTML content
            mailSender.send(message);
            System.out.println("OTP email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    
    
    //////////////////
    
    public void sendHtmlEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail); // Set the sender's email address
            helper.setTo(toEmail);       // Set the recipient email address
            helper.setSubject(subject);  // Set the subject
            helper.setText(body, true);  // Set the body as HTML (second parameter is set to true)

            mailSender.send(message);
            System.out.println("HTML email sent successfully to " + toEmail);
        } catch (MailException | MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public String createRegistrationEmailBody(String firstName,String lastName) {
    	return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                "<div style='background-color: #ffffff; padding: 25px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); max-width: 600px; margin: auto;'>" +
                "<h2 style='color: #4CAF50; text-align: center;'>Welcome to SafeCryptoStocks!</h2>" +
                "<p style='font-size: 16px; color: #333;'>Dear <strong style='color: #4CAF50;'>" + firstName+" "+lastName+ "</strong>,</p>" +
                "<p style='font-size: 16px; color: #333;'>Congratulations! Your registration to <strong style='color: #4CAF50;'>SafeCryptoStocks</strong> was successful. We are thrilled to have you on board and can't wait for you to begin your journey with us.</p>" +
                "<p style='font-size: 16px; color: #333;'>Now you have access to a world of secure crypto trading, tracking, and management tools. Explore our platform and start making smarter investments today!</p>" +
                "<div style='text-align: center; margin-top: 20px;'>" +
                "<a href='http://localhost:8080/dashboard' style='font-size: 16px; background-color: #4CAF50; color: #fff; padding: 12px 25px; text-decoration: none; border-radius: 5px;'>" +
                "Start Exploring Now" +
                "</a>" +
                "</div>" +
                "<p style='font-size: 16px; color: #333; margin-top: 20px;'>If you have any questions, feel free to <a href='mailto:support@safecryptostocks.com' style='color: #4CAF50;'>contact our support team</a>.</p>" +
                "<br>" +
                "<p style='font-size: 16px; color: #888;'>The <strong style='color: #4CAF50;'>SafeCryptoStocks</strong> Team</p>" +
                "<footer style='text-align: center; padding: 10px 0; color: #888; font-size: 12px; margin-top: 30px;'>" +
                "<p>&copy; 2024 SafeCryptoStocks. All rights reserved.</p>" +
                "</footer>" +
                "</div>" +
                "</body>" +
                "</html>";

    }
    
    
    /////////////////
    
    
    public void sendPurchaseEmail(String toEmail, String firstName,String lastName,List<Stock> stocks) {
        try {
            // Create MimeMessage for HTML content
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Construct email details
            helper.setTo(toEmail);
            helper.setSubject("Stock Purchase Confirmation");
            helper.setText(buildEmailContent(firstName,lastName,stocks), true); // Set content as HTML

            // Send email
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    // Build HTML content for email
    private String buildEmailContent(String firstName,String lastName,List<Stock> stocks) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>")
               .append("<div style='background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>")
               .append("<h2 style='color: #4CAF50;'>Thank You for Your Purchase at SafeCryptoStocks!</h2>")
               .append("<p>Dear <strong>").append(firstName).append(' ').append(lastName).append("</strong>,</p>")
               .append("<p>Congratulations! You've successfully purchased the following stocks:</p>")
               .append("<table style='width: 100%; border-collapse: collapse;'>")
               .append("<thead><tr>")
               .append("<th style='border: 1px solid #ddd; padding: 10px; background-color: #007bff; color: white;'>Stock Name</th>")
               .append("<th style='border: 1px solid #ddd; padding: 10px; background-color: #007bff; color: white;'>Current Price</th>")
               .append("<th style='border: 1px solid #ddd; padding: 10px; background-color: #007bff; color: white;'>Holdings</th>")
               .append("<th style='border: 1px solid #ddd; padding: 10px; background-color: #007bff; color: white;'>Avg. Buy Price</th>")
               .append("<th style='border: 1px solid #ddd; padding: 10px; background-color: #007bff; color: white;'>Percentage Change (24h)</th>")
               .append("</tr></thead><tbody>");

        for (Stock stock : stocks) {
            content.append("<tr>")
                   .append("<td style='border: 1px solid #ddd; padding: 10px;'>").append(stock.getStockName()).append("</td>")
                   .append("<td style='border: 1px solid #ddd; padding: 10px;'>₹").append(stock.getCurrentPrice()).append("</td>")
                   .append("<td style='border: 1px solid #ddd; padding: 10px;'>").append(stock.getHoldings()).append("</td>")
                   .append("<td style='border: 1px solid #ddd; padding: 10px;'>₹").append(stock.getAvgBuyPrice()).append("</td>")
                   .append("<td style='border: 1px solid #ddd; padding: 10px;'>").append(stock.getPercentChange24h()).append("</td>")
                   .append("</tr>");
        }

        content.append("</tbody></table>")
               .append("<p>If you have any questions, feel free to <a href='mailto:support@safecryptostocks.com'>contact our support team</a>.</p>")
               .append("<br><p style='color: #888;'>The SafeCryptoStocks Team</p>")
               .append("<footer style='text-align: center; padding: 10px 0; color: #888; font-size: 12px;'>")
               .append("<p>&copy; 2024 SafeCryptoStocks. All rights reserved.</p>")
               .append("</footer></div></body></html>");
        return content.toString();
    }

    ////////////////////////////
    
    
    public void sendSellNotification(String toEmail, String firstName, String lastName, String stockName, double sellQuantity, double sellPrice) {
        String subject = "Stock Sale Confirmation";

        String content = "<html><body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                + "<div style='background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>"
                + "<h2 style='color: #4CAF50; text-align: center;'>Stock Sale Confirmation</h2>"
                + "<p>Dear <strong>" + firstName + " " + lastName + "</strong>,</p>"
                + "<p>You have successfully completed a stock sale. Here are the details:</p>"
                + "<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>"
                + "<thead>"
                + "<tr style='background-color: #007bff; color: white;'>"
                + "<th style='padding: 10px; border: 1px solid #ddd;'>Stock Name</th>"
                + "<th style='padding: 10px; border: 1px solid #ddd;'>Quantity Sold</th>"
                + "<th style='padding: 10px; border: 1px solid #ddd;'>Sell Price per Unit</th>"
                + "<th style='padding: 10px; border: 1px solid #ddd;'>Total Value</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + "<tr>"
                + "<td style='padding: 10px; border: 1px solid #ddd;'>" + stockName + "</td>"
                + "<td style='padding: 10px; border: 1px solid #ddd;'>" + sellQuantity + "</td>"
                + "<td style='padding: 10px; border: 1px solid #ddd;'>₹" + sellPrice + "</td>"
                + "<td style='padding: 10px; border: 1px solid #ddd;'>₹" + (sellQuantity * sellPrice) + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<p style='margin-top: 20px; font-size: 16px; color: #333;'>"
                + "The total amount of <strong>₹" + (sellQuantity * sellPrice) + "</strong> from your stock sale transaction will be credited to your bank account ending with "
                + "<strong>#########8997</strong> within 7 working days.</p>"
                + "<p>We appreciate your trust in SafeCryptoStocks for managing your investments.</p>"
                + "<footer style='text-align: center; color: #888; font-size: 12px; margin-top: 20px;'>"
                + "<p>&copy; 2024 SafeCryptoStocks. All rights reserved.</p>"
                + "</footer>"
                + "</div>"
                + "</body></html>";

        sendHtmlEmail(toEmail, subject, content);
    }

    /////////////////////////
    
    public void sendLoginNotification(String toEmail, String firstName, String lastName) {
        try {
            String subject = "Successful Login Notification";
            String content = "<html>"
                    + "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>"
                    + "<div style='background-color: #f4f4f4; padding: 20px;'>"
                    + "  <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);'>"
                    + "    <h2 style='color: #4CAF50; text-align: center;'>Login Notification</h2>"
                    + "    <p style='color: #333;'>Dear <strong>" + firstName + " " + lastName + "</strong>,</p>"
                    + "    <p style='color: #555;'>You have successfully logged into your account on <strong>" + new Date() + "</strong>.</p>"
                    + "    <p style='color: #555;'>If this wasn't you, please secure your account immediately by changing your password and contacting our support team.</p>"
                    + "    <div style='text-align: center; margin: 20px;'>"
                    + "      <a href='https://safecryptostocks.com/reset-password' style='display: inline-block; padding: 10px 20px; color: #ffffff; background-color: #4CAF50; text-decoration: none; border-radius: 5px;'>Secure My Account</a>"
                    + "    </div>"
                    + "    <p style='color: #999; text-align: center; font-size: 12px;'>Thank you,<br>SafeCryptoStocks Team</p>"
                    + "  </div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            sendEmail(toEmail, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLoginFailureAlert(String toEmail) {
        try {
            String subject = "Failed Login Attempt Alert";
            String content = "<html>"
                    + "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0;'>"
                    + "<div style='background-color: #f4f4f4; padding: 20px;'>"
                    + "  <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);'>"
                    + "    <h2 style='color: #FF5733; text-align: center;'>Failed Login Attempt</h2>"
                    + "    <p style='color: #333;'>We detected a failed login attempt using your email (<strong>" + toEmail + "</strong>) on <strong>" + new Date() + "</strong>.</p>"
                    + "    <p style='color: #555;'>If this wasn't you, please secure your account immediately by changing your password and contacting our support team.</p>"
                    + "    <div style='text-align: center; margin: 20px;'>"
                    + "      <a href='https://safecryptostocks.com/contact-support' style='display: inline-block; padding: 10px 20px; color: #ffffff; background-color: #FF5733; text-decoration: none; border-radius: 5px;'>Contact Support</a>"
                    + "    </div>"
                    + "    <p style='color: #999; text-align: center; font-size: 12px;'>Thank you,<br>SafeCryptoStocks Team</p>"
                    + "  </div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            sendEmail(toEmail, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendEmail(String toEmail, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(senderEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(content, true); // HTML content
        mailSender.send(message);
    }
    
}
