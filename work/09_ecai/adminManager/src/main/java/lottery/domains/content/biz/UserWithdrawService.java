package lottery.domains.content.biz;

import admin.domains.content.entity.AdminUser;
import admin.web.WebJSONObject;
import javautils.jdbc.PageList;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.vo.user.HistoryUserWithdrawVO;
import lottery.domains.content.vo.user.UserWithdrawVO;

import java.util.List;

public interface UserWithdrawService {
	
	UserWithdrawVO getById(int id);
	//历史
	HistoryUserWithdrawVO getHistoryById(int id);
	
	List<UserWithdrawVO> getLatest(int userId, int status, int count);

	List<UserWithdraw> listByRemitStatus(int[] remitStatuses, boolean third, String sTime, String eTime);
	
	PageList search(Integer type,String billno, String username, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId, int start, int limit);
	//历史用户提现
	PageList searchHistory(String billno, String username, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId, int start, int limit);
	
	boolean manualPay(AdminUser uEntity,WebJSONObject json, int id, String payBillno, String remarks, String operatorUser);

	boolean completeRemit(AdminUser uEntity,WebJSONObject json, int id, String operatorUser);

	boolean apiPay(AdminUser uEntity, WebJSONObject json, int id, PaymentChannel channel);

	boolean check(AdminUser uEntity,WebJSONObject json, int id, int status);

	boolean refuse(AdminUser uEntity,WebJSONObject json, int id, String reason, String remarks, String operatorUser);

	boolean withdrawFailure(AdminUser uEntity,WebJSONObject json, int id, String remarks, String operatorUser);
	/**
	 * 审核失败
	 */
	boolean reviewedFail(AdminUser uEntity,WebJSONObject json, int id, String remarks, String operatorUser);
	
	boolean lock(AdminUser uEntity,WebJSONObject json, int id, String operatorUser);

	boolean unlock(AdminUser uEntity,WebJSONObject json, int id, String operatorUser);

	boolean update(UserWithdraw withdraw);

	/**
	 * 第一位是总提现(recMoney)，第二位是总手续费(feeMoney)
	 */
	double[] getTotalWithdraw(String billno, String username, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId);
	
	/**
	 * 第一位是总提现(recMoney)，第二位是总手续费(feeMoney)
	 */
	double[] getHistoryTotalWithdraw(String billno, String username, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId);

}