//  package lottery.domains.content.jobs;
//
//  import admin.domains.jobs.MailJob;
//  import javautils.date.Moment;
//  import lottery.domains.content.biz.*;
//  import lottery.domains.content.dao.PaymentCardDao;
//  import lottery.domains.content.dao.UserDao;
//  import lottery.domains.content.dao.UserRechargeDao;
//  import lottery.domains.content.entity.My18UserBankTransfer;
//  import lottery.domains.content.entity.PaymentThrid;
//  import lottery.domains.content.entity.User;
//  import lottery.domains.content.entity.UserRecharge;
//  import lottery.domains.content.global.Global;
//  import org.apache.commons.codec.digest.DigestUtils;
//  import org.apache.commons.collections.CollectionUtils;
//  import org.hibernate.SQLQuery;
//  import org.hibernate.Session;
//  import org.hibernate.SessionFactory;
//  import org.hibernate.criterion.Criterion;
//  import org.hibernate.criterion.Order;
//  import org.hibernate.criterion.Restrictions;
//  import org.slf4j.Logger;
//  import org.slf4j.LoggerFactory;
//  import org.springframework.beans.factory.annotation.Autowired;
//  import org.springframework.beans.factory.annotation.Qualifier;
//  import org.springframework.scheduling.annotation.Scheduled;
//  import org.springframework.stereotype.Component;
//
//  import java.math.BigDecimal;
//  import java.util.ArrayList;
//  import java.util.List;
//
//  /**
//   * 银行转账单检查
//   * Created by Nick on 2016/10/1.
//   */
//  @Component
//  public class AutoLoadJob {
//      private static final Logger log = LoggerFactory.getLogger(AutoLoadJob.class);
//      private static final String BANK_TRANSFER_SECRET_KEY = "a987411dda11G30Oie";
//
//      private static volatile boolean isRunning = false; // 标识任务是否正在运行
//      @Autowired
//      private UserRechargeDao uRechargeDao;
//      @Autowired
//      private UserDao uDao;
//      @Autowired
//      private UserBillService uBillService;
//      @Autowired
//      private UserSysMessageService uSysMessageService;
//      @Autowired
//      private UserWithdrawLimitService uWithdrawLimitService;
//      @Autowired
//      private PaymentCardService paymentCardService;
//      @Autowired
//      private MailJob mailJob;
//      @Autowired
//      private PaymentThridService paymentThridService;
//
//      @Autowired
//      @Qualifier(value = "sessionFactoryAutoLoad")
//      private SessionFactory sessionFactoryAutoLoad;
//
//      @Scheduled(cron = "0,10,20,30,40,50 * * * * *")
//      public void syncOrder() {
//          synchronized (AutoLoadJob.class) {
//              if (isRunning == true) {
//                  // 任务正在运行，本次中断
//                  return;
//              }
//              isRunning = true;
//          }
//
//          try {
//              PaymentThrid paymentThrid = paymentThridService.getById(Global.USER_RECHARGE_TYPE_ONLINE_BANK_TRANSFER);
//              if (paymentThrid != null && paymentThrid.getStatus() == 0) {
//                  // 开始同步注单
//                  startSync();
//              }
//          } finally {
//              isRunning = false;
//          }
//      }
//
//     private void startSync() {
//         List<UserRecharge> orders = getOrders();
//         if (CollectionUtils.isEmpty(orders)) {
//             return;
//         }
//
//         int batchSize = 100;
//         int batch = orders.size() / batchSize + (orders.size() % batchSize > 0 ? 1 : 0);
//         List<My18UserBankTransfer> records = new ArrayList<>();
//         for (int i = 0; i < batch; i++) {
//             int start = i * batchSize;
//             int end = start + batchSize;
//             if (end >= orders.size()) {
//                 end = orders.size();
//             }
//             List<UserRecharge> batchOrders = orders.subList(start, end);
//             if (CollectionUtils.isNotEmpty(batchOrders)) {
//                 List<My18UserBankTransfer> batchRecords = getMy18Records(batchOrders);
//                 if (CollectionUtils.isNotEmpty(batchRecords)) {
//                     records.addAll(batchRecords);
//                 }
//             }
//         }
//
//         Moment now = new Moment(); // 现在时间
//         for (My18UserBankTransfer record : records) {
//             for (UserRecharge order : orders) {
//                 if (order.getBillno().equals(record.getBillno())
//                         && order.getRealName().equals(record.getRealName())) {
//
//                     int serverMoney = BigDecimal.valueOf(order.getMoney()).setScale(0, BigDecimal.ROUND_UP).intValue();
//                     int loadMoney = record.getMoney();
//                     if (serverMoney == loadMoney) {
//                         doRecharge(order, record.getPayBillno(), record.getPayTime());
//                     }
//                     else {
//                         log.error("发现MY18中有不匹配本地的金额，请检查");
//                         mailJob.addWarning("发现MY18中有不匹配本地的订单号,请检查,MY18订单号：" + record.getId() + ",系统订单号:" + order.getBillno());
//                     }
//
//                 }
//             }
//         }
//     }
//
//     public  boolean doRecharge(UserRecharge recharge, String payBillno, String payTime) {
//         UserRecharge userRecharge = uRechargeDao.getByBillno(recharge.getBillno());
//         if(userRecharge == null || userRecharge.getStatus() != 0){
//             return false;
//         }
//         int userId = userRecharge.getUserId();
//         User uBean = uDao.getById(userId);
//         double money = userRecharge.getMoney();
//         double beforeMoney = uBean.getLotteryMoney();
//         double afterMoney = beforeMoney + money;
//         double recMoney = money;
//         boolean cFlag = uRechargeDao.updateSuccess(recharge.getId(), beforeMoney, afterMoney, recMoney, payTime, payBillno);
//         if(cFlag) {
//             boolean flag = uBillService.addRechargeBill(userRecharge, uBean, recharge.getRemarks());
//             if (flag) {
//                 uDao.updateLotteryMoney(uBean.getId(), money);
//                 //消费限制
//                 uWithdrawLimitService.updateTotal(uBean.getId(), money, payTime);
//                 uSysMessageService.addTransfersRecharge(uBean.getId(), money);
//
//                 // 添加银行卡累计充值
//                 paymentCardService.addUsedCredits(recharge.getCardId(), money);
//             }
//         }
//         return cFlag;
//     }
//
//     private List<My18UserBankTransfer> getMy18Records(List<UserRecharge> orders) {
//
//         StringBuffer billnos = new StringBuffer();
//         for (int i = 0; i < orders.size(); i++) {
//             UserRecharge order = orders.get(i);
//             billnos.append("'").append(order.getBillno()).append("'");
//             if (i < orders.size() - 1) {
//                 billnos.append(",");
//             }
//         }
//
//         Session session = sessionFactoryAutoLoad.openSession();
//         try {
//             session.beginTransaction();
//             String selectSQL = "SELECT `id`, `billno`, `real_name`, `postscript`, `money`, `time`, `status`, `pay_billno`, `pay_time`, `secret` " +
//                  "FROM `user_bank_transfer` WHERE `billno` IN (" + billnos.toString() + ") and `status`=2";
//             SQLQuery idQuery = session.createSQLQuery(selectSQL);
//             Object selectResult = idQuery.list();
//
//             if (selectResult == null) {
//                 return null;
//             }
//
//             ArrayList<Object[]> datas = (ArrayList<Object[]>) selectResult;
//             if (datas == null || datas.size() <= 0) {
//                 return null;
//             }
//
//             List<My18UserBankTransfer> records = new ArrayList<>();
//             for (Object[] data : datas) {
//                 int id = Integer.valueOf(data[0].toString()); // ID
//                 String billno = data[1].toString(); // 账单
//                 String realName = data[2].toString(); // 真实姓名，由采集程序修改
//                 int postscript = Integer.valueOf(data[3].toString()); // 附言
//                 int money = Integer.valueOf(data[4].toString()); // 金额
//                 String time = data[5].toString(); // 提交时间
//                 int status = Integer.valueOf(data[6].toString()); // 状态，1：未支付，2：成功
//                 String payBillno = data[7].toString(); // 支付流水号
//                 String payTime = data[8].toString(); // 支付时间
//                 String secret = data[9].toString(); // 加密验证码
//
//                 // 验证
//                 boolean validateSecret = validateSecret(billno, time, payTime, realName, postscript, secret);
//                 if (validateSecret) {
//                     My18UserBankTransfer record = new My18UserBankTransfer();
//                     record.setId(id);
//                     record.setBillno(billno);
//                     record.setRealName(realName);
//                     record.setPostscript(postscript);
//                     record.setMoney(money);
//                     record.setTime(time);
//                     record.setStatus(status);
//                     record.setPayBillno(payBillno);
//                     record.setPayTime(payTime);
//                     record.setSecret(secret);
//                     records.add(record);
//                 }
//             }
//
//             return records;
//         }
//         catch (Exception e) {
//             log.error("查询自动上分采集数据发生错误", e);
//             session.getTransaction().rollback();
//         }
//         finally {
//             if(session != null && session.isOpen()) {
//                 session.close();
//             }
//         }
//
//         return null;
//     }
//
//     private boolean validateSecret(String billno, String time, String payTime, String realName, int postscript, String secret) {
//         Moment timeMoment = new Moment().fromTime(time);
//         Moment payTimeMoment = new Moment().fromTime(payTime);
//
//         // 支付时间必须大于提交时间
//         if (payTimeMoment.gt(timeMoment)) {
//             String serverSecret = time + realName + billno + postscript + BANK_TRANSFER_SECRET_KEY;
//             serverSecret = DigestUtils.md5Hex(serverSecret);
//
//             return serverSecret.equals(secret);
//         }
//
//         return false;
//     }
//     private List<UserRecharge> getOrders() {
//         // 查询过去2个小时未支付的订单
//         String startTime = new Moment().subtract(2, "hours").toSimpleTime();
//         String endTime = new Moment().toSimpleTime();
//         List<Criterion> criterions = new ArrayList<>();
//         criterions.add(Restrictions.eq("status", 0));// 未支付
//         criterions.add(Restrictions.ge("time", startTime));
//         criterions.add(Restrictions.le("time", endTime));
//         criterions.add(Restrictions.le("type", 2)); // 2:银行转账
//
//         List<Order> orders = new ArrayList<>();
//         orders.add(Order.asc("id")); // 时间最久的最先处理
//
//         List<UserRecharge> recharges = uRechargeDao.list(criterions, orders);
//
//         return recharges;
//     }
// }
