// package lottery.domains.content.biz;
//
// import javautils.jdbc.PageList;
// import lottery.domains.content.entity.User;
// import lottery.web.WebJSON;
//
// public interface UserBetsPlanService {
//
// 	boolean publish(WebJSON json, String billno, int orderId, User uEntity, int tid, int rate);
//
// 	PageList search(String billno, int lotteryId, String expect, String method, int start, int limit);
//
// 	PageList search(Integer[] targetUsers, Integer lotteryId, String expect, String sTime, String eTime, int start, int limit);
//
// }