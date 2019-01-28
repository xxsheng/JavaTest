//package lottery.domains.content.jobs;
//
//import admin.domains.jobs.MailJob;
//import javautils.date.Moment;
//import lottery.domains.content.biz.*;
//import lottery.domains.content.dao.UserDailySettleDao;
//import lottery.domains.content.dao.UserDao;
//import lottery.domains.content.dao.UserLotteryReportDao;
//import lottery.domains.content.entity.User;
//import lottery.domains.content.entity.UserDailySettle;
//import lottery.domains.content.global.Global;
//import lottery.domains.pool.LotteryDataFactory;
//import lottery.web.content.utils.UserCodePointUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//
///**
// * 日结任务
// */
//@Component
//public class CheckNeibuZhaoShangJob {
//    private static final Logger log = LoggerFactory.getLogger(CheckNeibuZhaoShangJob.class);
//    @Autowired
//    private UserDao uDao;
//    @Autowired
//    private UserService uService;
//    @Autowired
//    private UserDailySettleService uDailySettleService;
//    @Autowired
//    private UserDailySettleDao userDailySettleDao;
//    @Autowired
//    private UserBillService uBillService;
//    @Autowired
//    private UserLotteryReportService uLotteryReportService;
//    @Autowired
//    private UserLotteryReportDao uLotteryReportDao;
//    @Autowired
//    private UserSysMessageService uSysMessageService;
//    @Autowired
//    private UserCodePointUtil uCodePointUtil;
//    @Autowired
//    private MailJob mailJob;
//
//    @Autowired
//    private LotteryDataFactory dataFactory;
//
//     @PostConstruct
//    public void schedule() {
//        try {
//
//            log.info("检查内部招商契约日结开始");
//
//            check();
//
//            log.info("检查内部招商契约日结完成");
//        } catch (Exception e) {
//            log.error("检查内部招商契约日结出错", e);
//        }
//    }
//
//    private void check() {
//        List<User> neibuZhaoShangs = uService.findNeibuZhaoShang();
//
//
//        if (CollectionUtils.isEmpty(neibuZhaoShangs)) {
//            return;
//        }
//
//        for (User neibuZhaoShang : neibuZhaoShangs) {
//            UserDailySettle uDailySettle = uDailySettleService.getByUserId(neibuZhaoShang.getId());
//
//            double scale = dataFactory.getDailySettleConfig().getNeibuZhaoShangScale();
//            int minValidUser = dataFactory.getDailySettleConfig().getNeibuZhaoShangMinValidUser();
//            int initStatus = Global.DAILY_SETTLE_VALID;
//            int fixed = Global.DAIYU_FIXED_FIXED; // 浮动比例
//
//            if (uDailySettle == null) {
//                // 原为空，新增
//                add(neibuZhaoShang, scale, minValidUser, initStatus, fixed, scale, scale);
//            }
//        }
//    }
//
//    private boolean add(User user, double scale, int minValidUser, int status, int fixed, double minScale, double maxScale) {
//        UserDailySettle bean = uDailySettleService.getByUserId(user.getId());
//        if(bean == null) {
//            int userId = user.getId();
//            Moment moment = new Moment();
//            String createTime = moment.toSimpleTime();
//            String createDate = moment.toSimpleDate();
//            String endDate = moment.add(99, "years").toSimpleDate();
//            double totalAmount = 0;
//            UserDailySettle entity = new UserDailySettle(userId, scale, minValidUser, createTime, createTime, createDate, endDate, totalAmount, status, fixed, minScale, maxScale);
//            userDailySettleDao.add(entity);
//            log.info("为内部招商号{}修复契约日结规则", user.getUsername());
//            return true;
//        }
//        return false;
//    }
//}