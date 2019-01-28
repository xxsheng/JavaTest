// package lottery.domains.content.jobs;
//
// import com.alibaba.fastjson.JSON;
// import lottery.domains.content.biz.UserDividendBillService;
// import lottery.domains.content.dao.UserDividendBillDao;
// import lottery.domains.content.entity.UserDividendBill;
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
//  * 上级余额不足，分红扣发任务
//  * Created by Nick on 2017/1/3.
//  */
// @Component
// public class DividendIssueJob {
//     private static final Logger log = LoggerFactory.getLogger(DividendIssueJob.class);
//     private static volatile boolean isRunning = false; // 标识任务是否正在运行
//
//     @Autowired
//     private UserDividendBillService uDividendBillService;
//     @Autowired
//     private UserDividendBillDao uDividendBillDao;
//
//     // @Scheduled(cron = "0 10,30,50 * * * ?")
//     @Scheduled(cron = "0 0,10,20,30,40,50 * * * ?")
//     // @Scheduled(cron = "0 0 11,23 * * ?")
//     // @Scheduled(cron = "0/10 * * * * ?")
//     public void schedule() {
//         synchronized (DividendIssueJob.class) {
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
//         // 是否正在审核中
//         boolean hasUnApproved = hasUnApproved();
//         if (hasUnApproved) {
//             log.info("有账单正在审核中，不进行扣发");
//             return;
//         }
//
//         List<UserDividendBill> bills = findBills();
//         if (CollectionUtils.isEmpty(bills)) {
//             return;
//         }
//
//         log.info("正在发放余额不足分红账单");
//         for (UserDividendBill bill : bills) {
//             log.info("正在发放ID为{}的余额不足分红账单", bill.getId());
//             issue(bill);
//             log.info("完成发放ID为{}的余额不足分红账单", bill.getId());
//         }
//         log.info("完成发放余额不足分红账单");
//     }
//
//     private void issue(UserDividendBill bill) {
//         try {
//             uDividendBillService.issueInsufficient(bill.getId());
//         } catch (Exception e) {
//             log.error("扣发分红时出错：" + JSON.toJSONString(bill), e);
//         }
//     }
//
//     private boolean hasUnApproved() {
//         //  查找是否有待审核的账单，否则的话，任务不能进行，因为可能在审核中
//         List<Criterion> criterions = new ArrayList<>();
//         criterions.add(Restrictions.eq("status", Global.DIVIDEND_BILL_UNAPPROVE));
//
//         List<Order> orders = new ArrayList<>();
//
//         List<UserDividendBill> bills = uDividendBillService.findByCriteria(criterions, orders);
//
//         if (CollectionUtils.isNotEmpty(bills)) {
//             return true;
//         }
//
//         return false;
//     }
//
//     /**
//      * 找出余额不足的分红单
//      */
//     private List<UserDividendBill> findBills() {
//         List<Criterion> criterions = new ArrayList<>();
//         criterions.add(Restrictions.eq("status", Global.DIVIDEND_BILL_INSUFFICIENT));
//
//         List<Order> orders = new ArrayList<>();
//         orders.add(Order.desc("id"));
//
//         List<UserDividendBill> bills = uDividendBillService.findByCriteria(criterions, orders);
//         return bills;
//     }
// }
