//package lottery.domains.utils.open;
//
//import lottery.JunitEnvironment;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
///**
//* Created by Nick on 2016/10/1.
//*/
//public class LotteryOpenUtilTest extends JunitEnvironment {
//   @Autowired
//   private LotteryOpenUtil openUtil;
//   @Test
//   public void getCurrOpenTime() throws Exception {
//       // System.out.println("==========");
//       // OpenTime currOpenTime = openUtil.getCurrOpenTime(118);
//       // System.out.println("expect=" + currOpenTime.getExpect());
//       // System.out.println("startTime=" + currOpenTime.getStartTime());
//       // System.out.println("stopTime=" + currOpenTime.getStopTime());
//       // System.out.println("openTime=" + currOpenTime.getOpenTime());
//       //
//       // System.out.println("==========");
//       // currOpenTime = openUtil.getOpentime(118, currOpenTime.getExpect());
//       // System.out.println("expect=" + currOpenTime.getExpect());
//       // System.out.println("startTime=" + currOpenTime.getStartTime());
//       // System.out.println("stopTime=" + currOpenTime.getStopTime());
//       // System.out.println("openTime=" + currOpenTime.getOpenTime());
//       //
//       // System.out.println("==========");
//       // currOpenTime = openUtil.getLastOpenTime(118);
//       // System.out.println("expect=" + currOpenTime.getExpect());
//       // System.out.println("startTime=" + currOpenTime.getStartTime());
//       // System.out.println("stopTime=" + currOpenTime.getStopTime());
//       // System.out.println("openTime=" + currOpenTime.getOpenTime());
//
//       List<OpenTime> openTimeList = openUtil.getOpenDateList(118, "2016-11-29");
//       for (OpenTime openTime : openTimeList) {
//           System.out.println(openTime.getExpect());
//       }
//       System.out.println(openTimeList.size());
//   }
//
//}