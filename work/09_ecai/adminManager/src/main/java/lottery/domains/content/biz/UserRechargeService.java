package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.vo.user.HistoryUserRechargeVO;
import lottery.domains.content.vo.user.UserRechargeVO;

import java.util.List;

public interface UserRechargeService {
	
	UserRechargeVO getById(int id);
	//历史充值详情
	HistoryUserRechargeVO getHistoryById(int id);
	
	List<UserRechargeVO> getLatest(int userId, int status, int count);

	List<UserRecharge> listByPayTimeAndStatus(String sDate, String eDate, int status);
	
	PageList search(Integer type,String billno, String username, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId, int start, int limit);
	/**
	 * 查询历史充值记录
	 * @return
	 */
	PageList searchHistory(String billno, String username, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId, int start, int limit);

	boolean addSystemRecharge(String username, int subtype, int account, double amount, int isLimit, String remarks);
	
	boolean patchOrder(String billno, String payBillno, String remarks);
	
	boolean cancelOrder(String billno);

	double getTotalRecharge(Integer type,String billno, String username, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId);
	
	double getHistoryTotalRecharge(String billno, String username, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId);
}