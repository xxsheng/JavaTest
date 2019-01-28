// package lottery.domains.open.utils.ssc;
//
// import javautils.array.ArrayUtils;
// import lottery.JunitEnvironment;
// import lottery.domains.content.biz.UserBetsSettleService;
// import lottery.domains.content.entity.LotteryPlayRules;
// import lottery.domains.content.entity.UserBets;
// import lottery.domains.pool.DataFactory;
// import org.apache.commons.codec.digest.DigestUtils;
// import org.aspectj.util.FileUtil;
// import org.junit.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.util.FileSystemUtils;
// import org.springframework.util.StreamUtils;
//
// import java.io.File;
// import java.io.IOException;
// import java.io.InputStream;
// import java.net.URL;
// import java.nio.charset.Charset;
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.LinkedList;
// import java.util.List;
//
// /**
//  * Created by Nick on 2016/11/26.
//  */
// public class LotterySSCPreCalculatorTest extends JunitEnvironment {
//     @Autowired
//     private LotterySSCPreCalculator preCalculator;
//     @Autowired
//     private UserBetsSettleService userBetsSettleService;
//     @Autowired
//     private DataFactory dataFactory;
//
//     @Test
//     public void test() throws Exception{
//         // testWXZHIXDS(); // 五星直选单式
//         testDW(); // 定位胆
//
//
//     }
//
//     public void testWXZHIXDS() throws IOException {
//         String codes = FileUtil.readAsString(new File("D:\\works\\yx\\lottery\\wxzhixds.txt"));
//         long start = System.currentTimeMillis();
//         UserBets userBets = new UserBets();
//         userBets.setId(1);
//         userBets.setCode(1950);
//         userBets.setCodes(codes);
//         userBets.setMethod("wxzhixds");
//         userBets.setModel("li");
//         userBets.setMultiple(3);
//         userBets.setLotteryId(101);
//
//         for (int i = 0; i < 100000; i++) {
//             Object[] obj = userBetsSettleService.testUsersBets(userBets, "0,0,1,0,1", dataFactory.getLottery(userBets.getLotteryId()), false);
//             obj = userBetsSettleService.testUsersBets(userBets, "0,0,1,0,1", dataFactory.getLottery(userBets.getLotteryId()), false);
//
//             // boolean isWin = (Boolean)obj[0];
//             // double prize = (Double)obj[1];
//             // System.out.println("isWin:" + isWin + ",prize:" + prize);
//         }
//
//         long spend = System.currentTimeMillis() - start;
//
//         System.out.println("耗时:" + spend);
//     }
//
//     public void testDW() throws IOException {
//         String codes = "-,-,-,-,89012";
//         long start = System.currentTimeMillis();
//         UserBets userBets = new UserBets();
//         userBets.setId(1);
//         userBets.setCode(1950);
//         userBets.setCodes(codes);
//         userBets.setMethod("dw");
//         userBets.setModel("li");
//         userBets.setMultiple(3);
//         userBets.setLotteryId(101);
//
//         LinkedList<String> allSSCCodes = preCalculator.getAllSSCCodes();
//
//         Iterator<String> iterator = allSSCCodes.iterator();
//
//         while (iterator.hasNext()) {
//             Object[] obj = userBetsSettleService.testUsersBets(userBets, iterator.next(), dataFactory.getLottery(userBets.getLotteryId()), false);
//             // boolean isWin = (Boolean)obj[0];
//             // double prize = (Double)obj[1];
//             // System.out.println("isWin:" + isWin + ",prize:" + prize);
//         }
//
//         long spend = System.currentTimeMillis() - start;
//
//         System.out.println("耗时:" + spend);
//     }
// }