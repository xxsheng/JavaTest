// package lottery.domains.service;
//
// import javautils.jdbc.hibernate.HibernateSuperDao;
// import lottery.JunitEnvironment;
// import lottery.domains.content.dao.UserBetsDao;
// import lottery.domains.content.dao.UserDao;
// import lottery.domains.content.dao.read.UserReadDao;
// import lottery.domains.content.entity.User;
// import lottery.domains.content.entity.UserBets;
// import org.junit.Test;
// import org.springframework.beans.factory.annotation.Autowired;
//
// import java.util.List;
//
// /**
//  * <p>模拟每天3000个用户连续一个月进行投注，每个用户每天200注,玩法随机，彩种随机</p>
//  *
//  * <p>假设一个月是31天</p>
//  *
//  * <p>总数据条件是<strong color='#FFFF00' style='font-size:22px;font-family:微软雅黑;'>3000 * 200 * 31 = 18600000</strong></p>
//  *
//  * <p>本测试用例尽可能模拟真实环境的投注情况</p>
//  *
//  * Created by Nick on 2017-06-22.
//  */
// public class UserBetsService extends JunitEnvironment {
//     @Autowired
//     private HibernateSuperDao<UserBets> superDao;
//
//     @Autowired
//     private UserReadDao uDao;
//
//     @Test
//     public void test() {
//         int[] upUserIds = new int[]{4592, 198, 16833, 291, 228, 15890, 16059, 199}; // 主管ID
//         int dailyUserCount = 3000; // 每天多少个用户
//         int dailyEachUserBetsCount = 200;
//
//         start();
//     }
//
//     private void start(int[] upUserIds, int dailyUserCount, int dailyEachUserBetsCount) {
//         // 获取用户列表
//
//         // 每个用户随机获取200注
//
//         // 批量提交
//     }
//
//     private List<User> getUsers() {
//         // 获取下级最多的代理
//         int[] topUserIds = 4592;
//
//         uDao.
//     }
// }
