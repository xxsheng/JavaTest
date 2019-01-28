package lottery.domains.content.jobs;

import javautils.date.Moment;
import lottery.domains.content.biz.UserWithdrawLogService;
import lottery.domains.content.biz.UserWithdrawService;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.entity.UserWithdrawLog;
import lottery.domains.content.global.Global;
import lottery.domains.content.global.RemitStatusConstants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * API代付单超时任务
 * Created by Nick on 2017-09-14.
 */
@Component
public class APIPayOrderTimeoutJob {
    private static final Logger log = LoggerFactory.getLogger(APIPayOrderTimeoutJob.class);
    private static volatile boolean isRunning = false; // 标识任务是否正在运行

    // 处理中状态
    private static final int[] PROCESSING_STATUSES = new int[]{Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING, Global.USER_WITHDRAW_REMITSTATUS_THIRD_UNPROCESS, Global.USER_WITHDRAW_REMITSTATUS_SYNC_STATUS};

    @Autowired
    private UserWithdrawService uWithdrawService;
	@Autowired
	private UserWithdrawLogService userWithdrawLogService;

     @Scheduled(cron = "0 0/10 * * * ?")
//    @PostConstruct
    public void scheduler() {
        synchronized (APIPayOrderTimeoutJob.class) {
            if (isRunning == true) {
                // 任务正在运行，本次中断
                return;
            }
            isRunning = true;
        }

        try {
            // 开始处理超时单
            process();
        } catch (Exception e) {
            log.error("处理API代付单超时任务异常", e);
        } finally {
            isRunning = false;
        }
    }

    private void process() {
        List<UserWithdraw> withdrawOrders = getWithdrawOrders();
        if (CollectionUtils.isEmpty(withdrawOrders)) {
            return;
        }

        for (UserWithdraw withdrawOrder : withdrawOrders) {
            withdrawOrder.setRemitStatus(Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN);
            uWithdrawService.update(withdrawOrder);
            String content= RemitStatusConstants.Status.getTypeByContent(Global.USER_WITHDRAW_REMITSTATUS_UNKNOWN);
            String time = new Moment().toSimpleTime();
			String action = String.format("%s；操作人：系统", content);
			userWithdrawLogService.add(new UserWithdrawLog(withdrawOrder.getBillno(),withdrawOrder.getUserId(), -1, action, time));
        }
    }

    private List<UserWithdraw> getWithdrawOrders() {
        // 只查询最近7天，且打款状态是银行处理中的数据
        String sTime = new Moment().subtract(7, "days").toSimpleDate();
        String eTime = new Moment().subtract(1, "days").toSimpleDate();
        return uWithdrawService.listByRemitStatus(PROCESSING_STATUSES, true, sTime, eTime);
    }
}
