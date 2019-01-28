// package lottery.domains.content.jobs;
//
// import javautils.date.Moment;
// import lottery.domains.content.biz.ActivityRewardService;
// import lottery.domains.content.global.Global;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// /**
//  * 佣金活动
//  */
// @Component
// public class ActivityRewardJob {
//
// 	@Autowired
// 	private ActivityRewardService aRewardBillService;
//
// 	boolean isRunning = false;
// 	@Scheduled(cron = "0 0 2 0/1 * *")
// 	public void start() {
// 		if(!isRunning) {
// 			isRunning = true;
// 			try {
// 				String date = new Moment().subtract(1, "days").toSimpleDate();
// 				try {
// 					System.out.println("开始计算" + date + "的佣金。");
// 					boolean result = aRewardBillService.calculate(Global.ACTIVITY_REBATE_REWARD_XIAOFEI, date);
// 					System.out.println("消费佣金计算完毕，结果：" + result);
// 				} catch (Exception e) {
// 					System.out.println("消费佣金计算失败。");
// 				}
// 				try {
// 					boolean result = aRewardBillService.calculate(Global.ACTIVITY_REBATE_REWARD_YINGKUI, date);
// 					System.out.println("盈亏佣金计算完毕，结果：" + result);
// 				} catch (Exception e) {
// 					System.out.println("盈亏佣金计算失败。");
// 				}
// 				try {
// 					boolean result = aRewardBillService.agreeAll(date);
// 					System.out.println("佣金派发完毕，结果：" + result);
// 				} catch (Exception e) {
// 					System.out.println("佣金派发失败。");
// 				}
// 			} catch (Exception e) {
// 				e.printStackTrace();
// 			} finally {
// 				isRunning = false;
// 			}
// 		}
// 	}
//
// }