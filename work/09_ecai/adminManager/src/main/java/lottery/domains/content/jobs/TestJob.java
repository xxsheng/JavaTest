//package lottery.domains.content.jobs;
//
//import admin.domains.jobs.MailJob;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Properties;
//
///**
// * Created by Nick on 2017/12/13.
// */
//@Component
//public class TestJob {
//    @Autowired
//    private MailJob mailJob;
//
//    @Scheduled(cron = "0/10 * * * * *")
//    public void schedule() {
//        try {
//            test();
////            for (int i = 0; i < 10; i++) {
////                mailJob.addWarning("测试测试测试");
////                Thread.currentThread().sleep(5000);
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void test() {
//        JavaMailSenderImpl sender = new JavaMailSenderImpl();
//        sender.setHost("smtp.gmail.com");
//        sender.setPort(587);
//        sender.setUsername("yingxincs@gmail.com");
//        sender.setPassword("Yx6889993");
////        sender.setUsername("yingxincs@gmail.com");
////        sender.setPassword("Yx6889993");
//        sender.setProtocol("smtp");
//
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//
//        sender.setJavaMailProperties(properties);
//
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setFrom("nickathome2020@gmail.com");
//        message.setTo("winoffer8668@gmail.com");
//        message.setSubject("你好");
//        message.setText("你好，测试一下！");
//        sender.send(message);
//    }
//
//    public static void main(String[] args) {
//        test();
//    }
//}
