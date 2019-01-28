// package lottery.domains.content.jobs;
//
// import com.alibaba.fastjson.JSON;
// import lottery.domains.content.biz.UserDailySettleBillService;
// import lottery.domains.content.entity.UserDailySettleBill;
// import lottery.domains.content.global.Global;
// import org.apache.commons.collections.CollectionUtils;
// import org.hibernate.criterion.Criterion;
// import org.hibernate.criterion.Order;
// import org.hibernate.criterion.Restrictions;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * 上级余额不足，日结扣发任务
//  * Created by Nick on 2017/1/3.
//  */
// @Component
// public class DailySettleIssueJob {
//     private static final Logger log = LoggerFactory.getLogger(DailySettleIssueJob.class);
//     private static volatile boolean isRunning = false; // 标识任务是否正在运行
//
//     @Autowired
//     private UserDailySettleBillService uDailySettleBillService;
//
//     @Scheduled(cron = "0 0,20,40 * * * ?")
//     // @Scheduled(cron = "0 0 11,23 * * ?")
//     // @Scheduled(cron = "0/10 * * * * ?")
//     public void schedule() {
//         synchronized (DailySettleIssueJob.class) {
//             if (isRunning == true) {
//                 // 任务正在运行，本次中断
//                 return;
//             }
//             isRunning = true;
//         }
//
//         try {
//             // 开始
//             start();
//         } finally {
//             isRunning = false;
//         }
//     }
//
//     private void start() {
//         List<UserDailySettleBill> bills = findBills();
//         if (CollectionUtils.isEmpty(bills)) {
//             return;
//         }
//
//         log.info("正在发放余额不足日结账单");
//         for (UserDailySettleBill bill : bills) {
//             log.info("正在发放ID为{}的余额不足日结账单", bill.getId());
//             issue(bill);
//             log.info("完成发放ID为{}的余额不足日结账单", bill.getId());
//         }
//         log.info("完成发放余额不足日结账单");
//     }
//
//     private void issue(UserDailySettleBill bill) {
//         try {
//             uDailySettleBillService.issue(bill.getId());
//         } catch (Exception e) {
//             log.error("扣发日结时出错：" + JSON.toJSONString(bill), e);
//         }
//     }
//
//     /**
//      * 找出余额不足的日结单
//      */
//     private List<UserDailySettleBill> findBills() {
//         List<Criterion> criterions = new ArrayList<>();
//         criterions.add(Restrictions.eq("status", Global.DAILY_SETTLE_BILL_INSUFFICIENT));
//
//         List<Order> orders = new ArrayList<>();
//         orders.add(Order.desc("id"));
//
//         List<UserDailySettleBill> bills = uDailySettleBillService.findByCriteria(criterions, orders);
//         return bills;
//     }
// }
