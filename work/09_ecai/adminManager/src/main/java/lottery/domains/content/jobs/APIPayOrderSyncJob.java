 package lottery.domains.content.jobs;

 import static lottery.domains.content.biz.impl.UserWithdrawServiceImpl.PROCESSING_STATUSES;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javautils.date.Moment;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.UserWithdrawLogService;
import lottery.domains.content.biz.UserWithdrawService;
import lottery.domains.content.biz.impl.UserWithdrawServiceImpl;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.entity.UserWithdrawLog;
import lottery.domains.content.global.Global;
import lottery.domains.content.global.RemitStatusConstants;
import lottery.domains.content.payment.RX.RXDaifuQueryResult;
import lottery.domains.content.payment.RX.RXPayment;
import lottery.domains.content.payment.af.AFDaifuQueryResult;
import lottery.domains.content.payment.af.AFPayment;
import lottery.domains.content.payment.cf.CFPayQueryResult;
import lottery.domains.content.payment.cf.CFPayment;
import lottery.domains.content.payment.fkt.FKTPayResult;
import lottery.domains.content.payment.fkt.FKTPayment;
import lottery.domains.content.payment.ht.HTPayResult;
import lottery.domains.content.payment.ht.HTPayment;
import lottery.domains.content.payment.htf.HTFPayQueryResult;
import lottery.domains.content.payment.htf.HTFPayment;
import lottery.domains.content.payment.tgf.TGFPayment;
import lottery.domains.content.payment.tgf.utils.QueryResponseEntity;
import lottery.domains.content.payment.yr.YRDaifuQueryResult;
import lottery.domains.content.payment.yr.YRPayment;
import lottery.domains.content.payment.zs.ZSDaifuQueryResult;
import lottery.domains.content.payment.zs.ZSPayment;
import lottery.domains.content.vo.user.UserWithdrawVO;
import lottery.domains.pool.LotteryDataFactory;

 /**
  * API代付单同步任务
  * Created by Nick on 2017-09-11.
  */
 @Component
 public class APIPayOrderSyncJob {
     private static final Logger log = LoggerFactory.getLogger(APIPayOrderSyncJob.class);
     private static volatile boolean isRunning = false; // 标识任务是否正在运行
     private static final int SYNC_STATUS_TIMEOUT_MINUTES = 10; // 查询状态的代付单多少分钟超时
     private static final int NULL_RESULT_TIMEOUT_MINUTES = 10; // 返回空数据的代付单多少分钟超时
     private static Map<String, Moment> FIRST_TIME_NULL_RESULT = new HashMap<>();

     // 处理中状态

     @Autowired
     private UserWithdrawService uWithdrawService;
 	@Autowired
 	private UserWithdrawLogService userWithdrawLogService;
     @Autowired
     private HTPayment htPayment;
     @Autowired
     private ZSPayment zsPayment;
     @Autowired
     private RXPayment rxPayment;
     @Autowired
     private CFPayment cfPayment;
     @Autowired
     private FKTPayment fktPayment;
     @Autowired
     private HTFPayment htfPayment;
     
     @Autowired
     private YRPayment yrPayment;
     
     @Autowired
     private AFPayment afPayment;
     
     
     @Autowired
     private TGFPayment tgfPayment;
     
     @Autowired
     private UserBillService uBillService;

     @Autowired
     private UserSysMessageService uSysMessageService;

     @Autowired
     private LotteryDataFactory dataFactory;

     // @Scheduled(cron = "0/10 * * * * ?")
     @Scheduled(cron = "0,20,40 * * * * ?")
     public void scheduler() {
         synchronized (APIPayOrderSyncJob.class) {
             if (isRunning == true) {
                 // 任务正在运行，本次中断
                 return;
             }
             isRunning = true;
         }

         try {
             // 开始同步注单状态
             startSync();
         } catch (Exception e) {
             log.error("同步API代付单状态出错", e);
         } finally {
             isRunning = false;
         }
     }

     private void startSync() {
         // 查询待处理的注单
         List<UserWithdraw> withdrawOrders = getWithdrawOrders();
         if (CollectionUtils.isEmpty(withdrawOrders)) {
             return;
         }

//         log.info("开始同步API代付单状态，共计{}笔", withdrawOrders.size());

         for (UserWithdraw withdrawOrder : withdrawOrders) {
             syncOrder(withdrawOrder);
         }

//         log.info("完成同步API代付单状态");
     }

     private void syncOrder(UserWithdraw withdrawOrder) {
         if (withdrawOrder.getPaymentChannelId() == null || withdrawOrder.getPaymentChannelId() <= 0) {
             log.warn("API代付注单{}为未知第三方代付{}或不是第三方代付，本次不查询", withdrawOrder.getBillno(), withdrawOrder.getPaymentChannelId());
             return;
         }

         if (withdrawOrder.getRemitStatus() == Global.USER_WITHDRAW_REMITSTATUS_SYNC_STATUS) {
             // 查询状态中
             processSyncStatus(withdrawOrder);
         }
         else {
             // 同步打款状态
             processRemitStatus(withdrawOrder);
         }
     }

     /**
      * 处理查询状态中
      */
     private void processSyncStatus(UserWithdraw withdrawOrder) {
         // 注单是否超时
         boolean timeout = isTimeoutForSyncStatus(withdrawOrder);
         if (timeout) {
             updateRemitStatus(withdrawOrder, Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN);
             log.info("API代付单{}已超时，将注单修改为未知状态，且不再处理", withdrawOrder.getBillno());
             return;
         }

         // 同步打款状态
         processRemitStatus(withdrawOrder);
     }

     /**
      * 同步打款状态
      */
     private void processRemitStatus(UserWithdraw withdrawOrder) {
         PaymentChannel channel = dataFactory.getPaymentChannelFullProperty(withdrawOrder.getPaymentChannelId());
         if (channel == null) {
             log.warn("API代付单{}为未知第三方支付{}，本次不查询", withdrawOrder.getBillno(), withdrawOrder.getPaymentChannelId());
             return;
         }

//         log.info("正在查询API代付单状态{},第三方{}", withdrawOrder.getBillno(), channel.getName());

         Object[] thirdStatus = getThirdStatus(channel, withdrawOrder);
         if (thirdStatus == null) {
             // 如果注单超过N分钟查询不到状态，改成未知状态
             if (FIRST_TIME_NULL_RESULT.containsKey(withdrawOrder.getBillno())) {
                 Moment firstTimeNullResult = FIRST_TIME_NULL_RESULT.get(withdrawOrder.getBillno());
                 Moment now = new Moment();
                 // 超过N分钟即超时
                 int minutes = now.difference(firstTimeNullResult, "minute");
                 if (minutes >= NULL_RESULT_TIMEOUT_MINUTES) {
                     FIRST_TIME_NULL_RESULT.remove(withdrawOrder.getBillno());
                     updateRemitStatus(withdrawOrder, Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN);
                     log.info("API代付单{}，第三方{}超过{}分钟未返回数据，修改为未知状态", withdrawOrder.getBillno(), channel.getName(), NULL_RESULT_TIMEOUT_MINUTES);
                 }
             }
             else {
                 FIRST_TIME_NULL_RESULT.put(withdrawOrder.getBillno(), new Moment());
                 log.info("API代付单{}，第三方{}返回空数据，本次不修改", withdrawOrder.getBillno(), channel.getName());
             }
             return;
         }

         FIRST_TIME_NULL_RESULT.remove(withdrawOrder.getBillno());

         String payBillno = thirdStatus[0] == null ? null : thirdStatus[0].toString();
         int remitStatus = Integer.valueOf(thirdStatus[1].toString());

         if (StringUtils.isEmpty(payBillno)) {
             updateRemitStatus(withdrawOrder, Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN);
             log.info("API代付单{}，第三方{}返回注单号为空，修改为未知状态", withdrawOrder.getBillno(), channel.getName());
             return;
         }

         if (remitStatus == withdrawOrder.getRemitStatus()) {
             log.info("API代付单{}，第三方{}返回状态与数据库一致，本次不修改", withdrawOrder.getBillno(), channel.getName());
             return;
         }

         if (StringUtils.isEmpty(withdrawOrder.getPayBillno()) && StringUtils.isNotEmpty(payBillno)) {
             withdrawOrder.setPayBillno(payBillno);
         }

         // 银行处理完成
         if (remitStatus == Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED) {
             updateAsBankProcessed(withdrawOrder, payBillno);
             log.info("API代付单{}，第三方{}返回状态表示银行已打款完成，本注单最终处理成功", withdrawOrder.getBillno(), channel.getName());
         }
         else {
             updateRemitStatus(withdrawOrder, remitStatus);
//             log.info("API代付单{}，第三方{}返回状态{}", withdrawOrder.getBillno(), channel.getName(), remitStatus);
         }
     }


     /**
      * 获取第三方注单状态，返回[第三方注单号,对应我方状态]
      */
     private Object[] getThirdStatus(PaymentChannel channel, UserWithdraw order) {
        switch(channel.getChannelCode()) {
            case Global.PAYMENT_CHANNEL_HT:
            case Global.PAYMENT_CHANNEL_HTWECHAT:
            case Global.PAYMENT_CHANNEL_HTALIPAY:
            case Global.PAYMENT_CHANNEL_HTQQ:
            case Global.PAYMENT_CHANNEL_HTJDPAY:
                return getHTStatus(channel, order);
            case Global.PAYMENT_CHANNEL_ZS:
            case Global.PAYMENT_CHANNEL_ZSWECHAT:
            case Global.PAYMENT_CHANNEL_ZSALIPAY:
            case Global.PAYMENT_CHANNEL_ZSQQ:
                return getZSStatus(channel, order);
            case Global.PAYMENT_CHANNEL_RX:
            case Global.PAYMENT_CHANNEL_RXWECHAT:
            case Global.PAYMENT_CHANNEL_RXQQ:
                return getRXStatus(channel, order);
            case Global.PAYMENT_CHANNEL_CF:
            case Global.PAYMENT_CHANNEL_CFWECHAT:
            case Global.PAYMENT_CHANNEL_CFALIPAY:
            case Global.PAYMENT_CHANNEL_CFQQ:
            case Global.PAYMENT_CHANNEL_CFJDPAY:
                return getCFStatus(channel, order);
            case Global.PAYMENT_CHANNEL_FKT:
            case Global.PAYMENT_CHANNEL_FKTWECHAT:
            case Global.PAYMENT_CHANNEL_FKTALIPAY:
            case Global.PAYMENT_CHANNEL_FKTQQ:
            case Global.PAYMENT_CHANNEL_FKTJDPAY:
                return getFKTStatus(channel, order);
            case Global.PAYMENT_CHANNEL_HTF:
            case Global.PAYMENT_CHANNEL_HTFQQ:
            case Global.PAYMENT_CHANNEL_HTFWECHAT:
            case Global.PAYMENT_CHANNEL_HTFALIPAY:
            case Global.PAYMENT_CHANNEL_HTFJDPAY:
                return getHTFStatus(channel, order);
            case Global.PAYMENT_CHANNEL_YR:
            case Global.PAYMENT_CHANNEL_YRQQ:
            case Global.PAYMENT_CHANNEL_YRWECHAT:
            case Global.PAYMENT_CHANNEL_YRALIPAY:
                return getYRStatus(channel, order);
 
            case Global.PAYMENT_CHANNEL_AF:
            case Global.PAYMENT_CHANNEL_AFQQ:
            case Global.PAYMENT_CHANNEL_AFWECHAT:
            case Global.PAYMENT_CHANNEL_AFALIPAY:
            case Global.PAYMENT_CHANNEL_AFQUICK:
                return getAFStatus(channel, order);
                
    		case Global.PAYMENT_CHANNEL_TGF:
    		case Global.PAYMENT_CHANNEL_TGFQQ:
    		case Global.PAYMENT_CHANNEL_TGFQUICK:
    		case Global.PAYMENT_CHANNEL_TGFJDPAY:
                return getTGFStatus(channel, order);
        }

        throw new RuntimeException("不支持的第三方代付查询：" + channel.getName());
     }
     
     /**
      * 同步的状态
      */
     private Object[] getTGFStatus(PaymentChannel channel, UserWithdraw order) {
    	 QueryResponseEntity result = tgfPayment.query(order, channel);
         if (result != null && StringUtils.isNotEmpty(result.getStatus())) {
             int remitStatus = tgfPayment.transferBankStatus(result.getStatus());
             return new Object[]{order.getBillno(), remitStatus};
         }

         return null;
     }
     
     /**
      * 同步的状态
      */
     private Object[] getAFStatus(PaymentChannel channel, UserWithdraw order) {
    	 AFDaifuQueryResult result = afPayment.query(order, channel);
         if (result != null && StringUtils.isNotEmpty(result.getResult())) {
             int remitStatus = afPayment.transferBankStatus(result.getResult());
             return new Object[]{result.getOrder_no(), remitStatus};
         }

         return null;
     }
     
     /**
      * 同步的状态
      */
     private Object[] getYRStatus(PaymentChannel channel, UserWithdraw order) {
    	 YRDaifuQueryResult result = yrPayment.query(order, channel);
         if (result != null && StringUtils.isNotEmpty(result.getRemitStatus())) {
             int remitStatus = yrPayment.transferBankStatus(result.getRemitStatus());
             return new Object[]{result.getOutTradeNo(), remitStatus};
         }

         return null;
     }

     /**
      * 同步汇通的状态
      */
     private Object[] getHTStatus(PaymentChannel channel, UserWithdraw order) {
         HTPayResult result = htPayment.query(order, channel);
         if (result != null && StringUtils.isNotEmpty(result.getBankStatus())) {
             int remitStatus = htPayment.transferBankStatus(result.getBankStatus());
             return new Object[]{result.getOrderId(), remitStatus};
         }

         return null;
     }

     /**
      * 同步泽圣的状态
      */
     private Object[] getZSStatus(PaymentChannel channel, UserWithdraw order) {
         ZSDaifuQueryResult result = zsPayment.query(order, channel);
         if (result != null && StringUtils.isNotEmpty(result.getState())) {
             int remitStatus = zsPayment.transferBankStatus(result.getState());
             return new Object[]{result.getOutOrderId(), remitStatus};
         }

         return null;
     }

     /**
      * 同步荣讯的状态
      */
     private Object[] getRXStatus(PaymentChannel channel, UserWithdraw order) {
         RXDaifuQueryResult result = rxPayment.query(order, channel);
         if (result != null && StringUtils.isNotEmpty(result.getOrderId_state())) {
             int remitStatus = rxPayment.transferBankStatus(result.getOrderId_state());
             return new Object[]{result.getOrderId(), remitStatus};
         }

         return null;
     }

     /**
      * 同步创富的状态
      */
     private Object[] getCFStatus(PaymentChannel channel, UserWithdraw order) {
         CFPayQueryResult result = cfPayment.query(order, channel);
         if (result != null && cfPayment.isAccepted(result)) {
             int remitStatus = cfPayment.transferBankStatus(result.getBatchContent());
             return new Object[]{result.getBatchNo(), remitStatus};
         }

         return null;
     }

     /**
      * 同步福卡通的状态
      */
     private Object[] getFKTStatus(PaymentChannel channel, UserWithdraw order) {
         FKTPayResult result = fktPayment.query(order, channel);
         if (result != null && fktPayment.isAcceptedRequest(result.getIsSuccess())) {
             int remitStatus = fktPayment.transferBankStatus(result.getBankStatus());
             return new Object[]{result.getOrderId(), remitStatus};
         }

         return null;
     }

     /**
      * 同步汇天付的状态
      */
     private Object[] getHTFStatus(PaymentChannel channel, UserWithdraw order) {
         HTFPayQueryResult result = htfPayment.query(order, channel);
         if (result != null && htfPayment.isAcceptedRequest(result.getRetCode()) && StringUtils.isNotEmpty(result.getHyBillNo())) {
             int remitStatus = htfPayment.transferBankStatus(result.getDetailData());
             return new Object[]{result.getHyBillNo(), remitStatus};
         }

         return null;
     }

     /**
      * 修改打款状态
      */
     private void updateRemitStatus(UserWithdraw withdraw, int remitStatus) {
         UserWithdrawVO newestData = uWithdrawService.getById(withdraw.getId());
         if (newestData == null || newestData.getBean() == null) {
             return;
         }

         if (Arrays.binarySearch(UserWithdrawServiceImpl.PROCESSING_STATUSES, newestData.getBean().getRemitStatus()) <= -1) {
             log.warn("API代付注单{}不是可操作状态，无法将打款状态修改为{}, 本次不处理", withdraw.getBillno(), remitStatus);
             return;
         }

         withdraw.setRemitStatus(remitStatus);
         uWithdrawService.update(withdraw);

         String content= RemitStatusConstants.Status.getTypeByContent(remitStatus);
         if (StringUtils.isBlank(content)) {
         	content="未知";
 		}
         String time = new Moment().toSimpleTime();
 		String action = String.format("%s；操作人：系统", content);
 		userWithdrawLogService.add(new UserWithdrawLog(withdraw.getBillno(),withdraw.getUserId(), -1, action, time));
     }

     /**
      * 修改为打款完成，并修改报表
      */
     private void updateAsBankProcessed(UserWithdraw withdraw, String payBillno) {
         UserWithdrawVO newestData = uWithdrawService.getById(withdraw.getId());
         if (newestData == null || newestData.getBean() == null) {
             return;
         }
         if (Arrays.binarySearch(PROCESSING_STATUSES, newestData.getBean().getRemitStatus()) <= -1) {
             log.warn("API代付注单{}不是处理中状态，无法修改为打款完成", withdraw.getBillno());
             return;
         }

         String infos = "您的提现已处理，请您注意查收！";
         withdraw.setStatus(1); // 0：未处理；1：已完成；-1：拒绝支付；
         withdraw.setInfos(infos);
         withdraw.setPayBillno(payBillno);
         withdraw.setLockStatus(0); // 0：未锁定；1：已锁定；
         withdraw.setRemitStatus(Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED);
         boolean result = uWithdrawService.update(withdraw);
         if(result) {
             // 更新报表
             uBillService.addWithdrawReport(withdraw);
             uSysMessageService.addConfirmWithdraw(withdraw.getUserId(), withdraw.getMoney(), withdraw.getRecMoney());
             String content= RemitStatusConstants.Status.getTypeByContent(Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSED);
             String time = new Moment().toSimpleTime();
 			String action = String.format("%s；操作人：系统", content);
 			userWithdrawLogService.add(new UserWithdrawLog(withdraw.getBillno(),withdraw.getUserId(), -1, action, time));
         }
     }

     /**
      * 是否在查询状态中超时
      */
     private boolean isTimeoutForSyncStatus(UserWithdraw withdraw) {
         Moment now = new Moment();
         Moment operateTime = new Moment().fromTime(withdraw.getOperatorTime());

         // 超过10分钟即超时
         int minutes = now.difference(operateTime, "minute");

         return minutes >= SYNC_STATUS_TIMEOUT_MINUTES;
     }

     private List<UserWithdraw> getWithdrawOrders() {
         String sTime = new Moment().subtract(1, "days").toSimpleDate();
         String eTime = new Moment().add(1, "days").toSimpleDate();
         return uWithdrawService.listByRemitStatus(PROCESSING_STATUSES, true, sTime, eTime);
     }
 }
