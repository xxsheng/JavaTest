// package lottery.domains.content.jobs;
//
// import lottery.domains.content.biz.VipFreeChipsService;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// /**
//  * VIP免费筹码
//  */
// @Component
// public class VipFreeChipsJob {
//
// 	@Autowired
// 	private VipFreeChipsService vFreeChipsService;
//
// 	boolean isRunning = false;
// 	@Scheduled(cron = "0 0 2 10 * *")
// 	public void start() {
// 		if(!isRunning) {
// 			isRunning = true;
// 			try {
// 				try {
// 					System.out.println("开始计算VIP免费筹码。");
// 					boolean result = vFreeChipsService.calculate();
// 					System.out.println("VIP免费筹码计算完毕，结果：" + result);
// 				} catch (Exception e) {
// 					System.out.println("VIP免费筹码计算失败。");
// 				}
// 				try {
// 					System.out.println("开始发放VIP免费筹码。");
// 					boolean result = vFreeChipsService.agreeAll();
// 					System.out.println("VIP免费筹码发放完毕，记过：" + result);
// 				} catch (Exception e) {
// 					System.out.println("VIP免费筹码发放失败。");
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