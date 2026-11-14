    package com.fincore.identity.service.impl;

    import lombok.RequiredArgsConstructor;
    import org.springframework.mail.SimpleMailMessage;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    public class EmailService {

        private final JavaMailSender mailSender;

        public void sendVerificationEmail(
                String to,
                String verificationLink
        ){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("akshayambaliya8542@gmail.com");
            message.setSubject("Verify your FinCore AI email address");
            message.setText(
                    "Hello,\n\n" +
                    "Thank you for registering with FinCore AI.\n\n" +
                    "Please click the link below to verify your email address:\n\n" +
                    verificationLink +
                    "\n\nThis verification link expires in 24 hours.\n\n" +
                    "If you did not create this account, please ignore this email.\n\n" +
                    "Regards,\nFinCore AI Team"
            );

            mailSender.send(message);
        }


        public void sendTestEmail(String to) {

            try {
                SimpleMailMessage message = new SimpleMailMessage();

                message.setTo(to);
                message.setFrom("akshayambaliya8542@gmail.com");
                message.setSubject("FinCore AI - SMTP Test");

                message.setText(
                        "Congratulations!\n\n" +
                                "Your FinCore AI application successfully sent " +
                                "this email using Brevo SMTP.\n\n" +
                                "Email integration is working!"
                );

                mailSender.send(message);

                System.out.println(">>> EMAIL SENT SUCCESSFULLY <<<");

            } catch (Exception e) {
                System.err.println(">>> EMAIL SENDING FAILED <<<");
                e.printStackTrace();

                throw e;
            }
        }


        public void sendPasswordResetEmail(String to, String resetLink) {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("akshayambaliya8542@gmail.com");

            message.setSubject("Reset your FinCore AI password");

            message.setText(
                    "Hello,\n\n" +
                            "We received a request to reset your FinCore AI password.\n\n" +
                            "Click the link below to reset your password:\n\n" +
                            resetLink +
                            "\n\nThis link expires in 15 minutes and can only be used once.\n\n" +
                            "If you did not request a password reset, please ignore this email.\n\n" +
                            "Regards,\n" +
                            "FinCore AI Team"
            );

            mailSender.send(message);
        }

    }
