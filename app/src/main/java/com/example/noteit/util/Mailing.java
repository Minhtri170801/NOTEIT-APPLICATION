package com.example.noteit.util;

import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailing {

    private Mailing(){}
    private static Mailing instance;
    public static Mailing getInstance(){
        if(instance == null){
            instance = new Mailing();
        }
        return instance;
    }
    private String from = "applicationnoteit@gmail.com";
    private String host = "smtp.gmail.com";


    public boolean sendEmailRegister(String to, String otp, String username){
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("applicationnoteit@gmail.com", "noteitnoteit");
            }
        });
        session.setDebug(true);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Mã OTP xác nhận tài khoản NoteIt");
            message.setContent("Xin chào bạn <b>" + username + "</b> chúc mừng bạn đã đăng ký thành công tài khoản ứng dụng NoteIt. Đây là mã OTP xác thực đăng ký tài khoản của bạn: <b>" + otp + "</b>. Mã sẽ hết hạn trong vòng 1 phút","text/html;charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            Log.e("TAG",mex.toString());
            return false;
        } catch (Exception e) {
            Log.e("TAG","2" + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendEmailRecovery(String to, String otp){
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("applicationnoteit@gmail.com", "noteitnoteit");
            }
        });
        session.setDebug(true);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Mã OTP xác nhận khôi phục mật khẩu");
            message.setContent("Mã OTP xác thực khôi phục mật khẩu của bạn là: <b>" + otp + "</b>. Mã sẽ hết hạn trong vòng 1 phút","text/html;charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            Log.e("TAG",mex.toString());
            return false;
        } catch (Exception e) {
            Log.e("TAG","2" + e.toString());
            e.printStackTrace();
            return false;
        }
    }
}
