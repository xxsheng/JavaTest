package lottery.domains.content.biz;

import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserCodeQuota;

public interface UserActionLogService {

	boolean modLoginPwd(int userId, String ip);

	boolean bindWithdrawPwd(int userId, String ip);

	boolean modWithdrawPwd(int userId, String ip);

	boolean bindWithdrawName(int userId, String ip, String withdrawName);
	
	boolean bindCard(int userId, String ip, int bankId, String bankBranch, String cardName, String cardId);
	
	boolean bindSecurity(int userId, String ip, String question1, String question2, String question3);
	
	boolean withdrawApply(int userId, String ip, UserCard card, double amount, double recMoney, double feeMoney);
	
	boolean bindData(int userId, String ip, String withdrawName, String birthday, int bankId, String bankBranch, String cardId);
	
	boolean transToUser(int userId, String ip, String targetUser, double amount);

	boolean editUserPoint(int userId, String ip, String targetUser, double oldPoint, double newPoint);
	
	boolean editUserPointByAmount(int userId, String ip, String targetUser, double oldPoint, double newPoint, double amount3, double amount7);

	boolean editUserQuota(int userId, String ip, String targetUser, UserCodeQuota oldQuota, int amount1, int amount2, int amount3);

	boolean addNewUser(int userId, String ip, String username, int type, double point);

	boolean registUser(int userId, String ip, String username, String upUsername, int type, double point);

	boolean bindGoogle(int userId, String ip);

	boolean modLoginValidate(int userId, String ip, int loginValidate);

	boolean requestDailySettle(int userId, String ip, String username, String scaleLevel, String lossLevel, String salesLevel, String userLevel);

	boolean agreeDailySettle(int userId, String ip);

	boolean denyDailySettle(int userId, String ip);

	boolean requestDividend(int userId, String ip, String username, String scaleLevel, String lossLevel, String salesLevel, String userLevel);

	boolean agreeDividend(int userId, String ip);

	boolean denyDividend(int userId, String ip);

	boolean collectDividend(int userId, String ip, String startDate, String endDate, double availableAmount);

	boolean collectGameDividend(int userId, String ip, String startDate, String endDate, double userAmount);

	boolean issueDividend(int userId, String ip, String startDate, String endDate, double issued);

	boolean userUnbidCard(int userId, String username,String usercard,String ip, String dateTime, String unbindNum);

	boolean sign(int userId, int days, String lastSignTime, String ip);
}