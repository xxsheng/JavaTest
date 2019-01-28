// package lottery.domains.open.jobs;
//
// import lottery.domains.content.dao.UserBillDao;
// import lottery.domains.content.dao.UserDao;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// /**
//  * 清理各种负数金额，重新计算冻结金额
//  * Created by Nick on 2016/10/21.
//  */
// @Component
// public class ClearNegateMoneyJob {
//     private static final Logger log = LoggerFactory.getLogger(ClearNegateMoneyJob.class);
//
//     @Autowired
//     private UserDao userDao;
//     @Autowired
//     private UserBillDao userBillDao;
//
//     // @Scheduled(cron = "0 0/10 * * * ?")
//     public synchronized void clearNegate() {
//         clearUserNegateFreezeMoney();
//         // clearUserBillNegateBeforeMoney();
//         // clearUserBillNegateAfterMoney();
//     }
//
//     private void clearUserNegateFreezeMoney() {
//         int updated = userDao.clearUserNegateFreezeMoney();
//         if (updated > 0) {
//             log.debug("清理了用户表{}条负数冻结金额", updated);
//         }
//     }
//
//     private void clearUserBillNegateBeforeMoney() {
//         int updated = userBillDao.clearUserBillNegateBeforeMoney();
//         if (updated > 0) {
//             log.debug("清理了账单表{}条负数之前金额", updated);
//         }
//     }
//
//     private void clearUserBillNegateAfterMoney() {
//         int updated = userBillDao.clearUserBillNegateAfterMoney();
//         if (updated > 0) {
//             log.debug("清理了账单表{}条负数之后金额", updated);
//         }
//     }
// }
