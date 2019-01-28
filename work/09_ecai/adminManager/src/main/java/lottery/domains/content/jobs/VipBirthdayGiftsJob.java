// package lottery.domains.content.jobs;
//
// import lottery.domains.content.biz.VipBirthdayGiftsService;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import javautils.date.Moment;
//
// /**
//  * VIP生日礼金
//  */
// @Component
// public class VipBirthdayGiftsJob {
//
// 	@Autowired
// 	private VipBirthdayGiftsService vBirthdayGiftsService;
//
// 	boolean isRunning = false;
// 	@Scheduled(cron = "0 0 0 0/1 * *")
// 	public void start() {
// 		if(!isRunning) {
// 			isRunning = true;
// 			try {
// 				try {
// 					String birthday = new Moment().toSimpleDate();
// 					System.out.println("开始发放" + birthday + "VIP生日礼金。");
// 					boolean result = vBirthdayGiftsService.calculate(birthday);
// 					System.out.println("VIP生日礼金发放完毕，结果：" + result);
// 				} catch (Exception e) {
// 					System.out.println("VIP生日礼金发放失败。");
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