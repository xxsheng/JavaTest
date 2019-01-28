//package lottery.domains.content.api.ag;
//
//import lottery.JunitEnvironment;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * Created by Nick on 2017/2/2.
// */
//public class AGAPITest extends JunitEnvironment {
//    @Autowired
//    private AGAPI agAPI;
//
//    @Test
//    public void forwardGame() throws Exception {
//        String url = agAPI.forwardGame("agtest1_2064", "XITXlW", "www.yingxinyl.com", "1", "1");
//        System.out.println(url);
//    }
//
//    @Test
//    public void transValidationFromString() throws Exception {
//        String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request action=\"userverf\"><element id=\"20170207120813466\"><properties name=\"pcode\">P31</properties><properties name=\"gcode\">P31300</properties><properties name=\"userid\">qqq123_8009</properties><properties name=\"password\">qwerrzEFukqwerty</properties><properties name=\"token\">4abc28058246e6390a2dbaef8f226c65</properties><properties name=\"cagent\">8FEF2C2AED8B64BB2C139D6E327CD828</properties></element></request>";
//        AGValidation validation = agAPI.transValidationFromString(body);
//        System.out.println(validation);
//    }
//
//
//}